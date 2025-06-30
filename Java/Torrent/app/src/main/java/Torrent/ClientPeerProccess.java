package Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.BitSet;

public class ClientPeerProccess implements PeerProccess {
  private boolean isHandShake = false;
  private SocketChannel server;
  private DownloadManager downloadManager;
  private BitSet serverAvailablePieces;
  private String host;
  private int port;

  public ClientPeerProccess(SocketChannel serverChannel, DownloadManager downloadManager, String host, int port) {
    this.server = serverChannel;
    this.downloadManager = downloadManager;
    this.host = host;
    this.port = port;
    serverAvailablePieces = new BitSet();
  }

  @Override 
  public boolean onConnected() {
    System.out.println("Client: connected to server");

    ByteBuffer handShake = MessageMaker.makeHandShakeResponse(downloadManager.getInfoHash(), downloadManager.getPeerID());
    if (!sendMessage(handShake)) {
      return false;
    }

    return true;
  }
 
  @Override
  public boolean processing(ByteBuffer buff) throws IOException {
    if (!isHandShake) {
      if (!doHandShake(buff)) {
        return false;
      }
    } else {
      if (!handleMessage(buff))
        return false;
    }
    return true;
  }
  
  private byte[] protocolName = new byte[19];
  private byte[] reservedBytes = new byte[8];
  private byte[] infoHash = new byte[20];
  private byte[] peerID = new byte[20];
  private static final int INFO_HASH_LEN = 68;
  private static final int PROTOCOL_LEN = 19;

  private boolean doHandShake(ByteBuffer buff) { 
    isHandShake = true;

    if (buff.remaining() != INFO_HASH_LEN) {
      System.out.println("Client: size of peer's info hash incorrect");
      return false;
    }

    int protocolLength = buff.get();
    if (protocolLength != PROTOCOL_LEN) {
      System.out.println("Client: wrong length of client's protocol");
      return false;
    }

    buff.get(protocolName);
    String protocolNameStr = new String(protocolName);

    if (!protocolNameStr.equals("BitTorrent protocol")) {
      System.out.println("Client: this is not BitTorrent protocol");
      return false;
    }

    buff.get(reservedBytes); 
    buff.get(infoHash); 
    if (!Arrays.equals(infoHash, downloadManager.getInfoHash())){
      System.out.println("Client: server work with enother torrent file");
      return false;
    }

    buff.get(peerID); // игнорируем 

    ByteBuffer bitField = MessageMaker.makeBitFieldResponse(downloadManager.getAvailPieces(), downloadManager.getNumOfPieces());
    System.out.println("Client: recieve handshake from server; sent bitfield");
    if (!sendMessage(bitField)) {
      return false;
    }
    return true;
  }
  
  private boolean handleMessage(ByteBuffer buff) throws IOException{ 
    while (buff.hasRemaining()) {
      if (buff.remaining() < 4) {
        System.out.println("Client: incorrect peer message's length");
        return true;
      }

      int messageLength = buff.getInt();

      if (buff.remaining() < 1) {
        System.out.println("Client: no ID from server's message");
        return true;
      }

      BitTorrentID type = BitTorrentID.fromByte(buff.get());
      System.out.println(type);
      switch (type) {
        case BITFIELD: {
          byte[] bitField = new byte[messageLength - 1];
          if (buff.remaining() < bitField.length) {
            System.out.println("Client: incomplete bitfield message");
            return true;
          }

          buff.get(bitField);

          synchronized (serverAvailablePieces) {
            serverAvailablePieces = BitSet.valueOf(bitField);
          }

          System.out.println("Client: recieved bitfield from server");

          ByteBuffer interested = MessageMaker.makeInterestedMessage();
          if (!sendMessage(interested) || !sendNextRequest()) {
            return false;
          }
          break;
        }
        
        case HAVE: { // have - клиент получил новый кусок
          int pieceIndex = buff.getInt();
          System.out.println("HAVE");

          synchronized (serverAvailablePieces) {
            serverAvailablePieces.set(pieceIndex);
          }
          System.out.println("HAVE HAVE");

          if (downloadManager.getAvailPieces().get(pieceIndex))
            break;

          System.out.println("HAVE HAVE HAVE");
          if (!sendNextRequest()) {
            return false;
          }

          break;
        }

        case PIECE: {

          System.out.println("Client: lower level: get PIECE message from server");
          if (buff.remaining() < 8) {
            System.out.println("Client: incomplete piece message header");
            return true;
          }

          int pieceIdx = buff.getInt();
          int offset = buff.getInt();
          int pieceDataLength = messageLength - 9; // messageLength - (ID + index + begin)  4 (index) + 4 (offset) + 1 (id) 
          // System.out.println("CLIENT: lower level: pieceDataLen: " + pieceDataLength + "; what bytes left: " + buff.remaining());
          if (buff.remaining() < pieceDataLength) {
            System.out.println("Client: incomplete piece message data");
            return true;
          }

          byte[] pieceData = new byte[pieceDataLength];
          buff.get(pieceData);
          try {
            System.out.println("CLient: recieved piece from server");
            downloadManager.receiveBlock(pieceIdx, offset, pieceData);
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (!sendNextRequest()) {
            return false;
          }
          break;
        }
        default : 
          break;
      }
    }
    return true;
  }
  

  private int nextIndex = -1;

  private synchronized boolean sendNextRequest() throws IOException {
    if (nextIndex == -2)
      return true;

    if (nextIndex == -1) {

      synchronized (serverAvailablePieces) {
        nextIndex = downloadManager.getNextNeededPiece(serverAvailablePieces);
      }

      if (nextIndex == -1) {
        System.out.println("Clilent: server doesn't have needed pieces");
        return true;
      }
      
      if (nextIndex == -2) {
        System.out.println("Clilent: I've already have all pieces");
        return true;
      }
    }

    int offset = downloadManager.getBlockOffset(nextIndex);

    while (offset == -1) {
      synchronized (serverAvailablePieces) {
        nextIndex = downloadManager.getNextNeededPiece(serverAvailablePieces);
      }
  
      if (nextIndex == -1) {
        System.out.println("Clilent: server doesn't have needed pieces");
        return true;
      }
        
      if (nextIndex == -2) {
        System.out.println("Clilent: I've already have all pieces");
        return true;
      }
  
      offset = downloadManager.getBlockOffset(nextIndex);
    }
    
    System.out.println("Clilent: want to load piece " + nextIndex + " with offset " + offset + " in it");

    try {
      
      ByteBuffer request = MessageMaker.makeRequestMessage(nextIndex, offset, downloadManager.getBlockLen(nextIndex, offset));
      if (!sendMessage(request)) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  private boolean sendMessage(ByteBuffer message) {
    try { 
      while (message.hasRemaining()) {
        int bytesSent = server.write(message);
        if (bytesSent == -1) {
          return false;
        }
        System.out.println("Client: sent " + bytesSent + " bytes to peer");
      }
    } catch (IOException e) {
      System.out.println("Client: error sending message: " + e.getMessage());
      return false;
    }
    return true;
  }
  
  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
