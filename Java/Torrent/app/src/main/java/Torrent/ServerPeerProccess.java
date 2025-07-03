package Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ServerPeerProccess implements PeerProccess{   // основной класс, где сервер будет работать с каждый клиентом
  private boolean isHandShake = false;
  private boolean isInterested = false;
  private boolean isGetBitfield = false;
  private SocketChannel client;
  private DownloadManager downloadManager;

  public ServerPeerProccess(SocketChannel client, DownloadManager downloadManager) {
    this.client = client;
    this.downloadManager = downloadManager;
  }

  @Override
  public boolean onConnected() {
    System.out.println("Server: new client connected");
    return true;
  }
 
  @Override
  public boolean processing(ByteBuffer buff) {
    if (!isHandShake) {
      doHandShake(buff);
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

  private void doHandShake(ByteBuffer buff) { // делаем handShake с отправкой пиру имеющихся у нас кусоков
    if (buff.remaining() != 68) {
      System.out.println("Server: size of peer's info hash incorrect");
      return;
    }
    
    int protocolLength = buff.get();
    if (protocolLength != 19) {
      System.out.println("Server: wrong length of client's protocol");
      return;
    }

    buff.get(protocolName);
    String protocolNameStr = new String(protocolName);

    if (!protocolNameStr.equals("BitTorrent protocol")) {
      System.out.println("Server: this is not BitTorrent protocol");
      return;
    }

    buff.get(reservedBytes); // это зарезервированные флаги, игнорим
    buff.get(infoHash);
    if (!Arrays.equals(infoHash, downloadManager.getInfoHash())){
      System.out.println("Server: client work with enother torrent file");
      return;
    }

    buff.get(peerID); 

    ByteBuffer responseHandShake = MessageMaker.makeHandShakeResponse(downloadManager.getInfoHash(), downloadManager.getPeerID());
    sendMessage(responseHandShake); 
    System.out.println("Server: send our handshake to client");

    isHandShake = true;
    System.out.println("Server: handshake with peer (ID " + peerID + ") done");

    return;
  }
  
  private boolean handleMessage(ByteBuffer buff){ 
    while (buff.hasRemaining()) {
      if (buff.remaining() < 4) {
        System.out.println("Server: incorrect peer message's length");
        return true;
      }

      int messageLength = buff.getInt();
      if (messageLength == 0) {
        System.out.println("Server: keep-alive message");
        continue;
      }

      if (buff.remaining() < 1) {
        System.out.println("Server: no ID from client's message");
        return true;
      }

      BitTorrentID type = BitTorrentID.fromByte(buff.get());
      switch (type) {
        case INTERESTED: { // interested - клиент хочет качать у нас
          isInterested = true;
          System.out.println("Server: peer (ID " + peerID + ") is interested in downloading");
          break;
        }

        case NOT_INTERESTED: { // not interested - клиент больше не хочет качать
          isInterested = false;
          System.out.println("Server: peer (ID " + peerID + ") is not interested in downloading");
          break;
        }

        case BITFIELD: { // bitField - какие куски есть у клиента
          byte[] bitField = new byte[messageLength - 1];
          if (buff.remaining() < bitField.length) {
            System.out.println("Server: incomplete bitfield message");
            return true;
          }
          
          buff.get(bitField);
          System.out.println("Server: received peer's bitfield.");

          ByteBuffer serverBitField = MessageMaker.makeBitFieldResponse(downloadManager.getAvailPieces(), downloadManager.getNumOfPieces());
          sendMessage(serverBitField); 
          System.out.println("Server: send bitfield to client.");
          isGetBitfield = true;
          break;
        }

        case REQUEST: {
          if (buff.remaining() < 12) {
            System.out.println("Server: incomplete request message");
            return true;
          }

          int pieceidx = buff.getInt();
          int offset = buff.getInt();
          int pieceDataLength = buff.getInt();
          System.out.println(
              "SERVER: CLIENT WANTS TO GET " + pieceDataLength + " with offset " + offset + " with index " + pieceidx);

          try {
            if (!handleRequest(pieceidx, offset, pieceDataLength)) {

              return false;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        }
        default:
          break;
      }
    }
    return true;
  }

  private boolean handleRequest(int index, int offset, int pieceDataLength) throws IOException {
    if (!isInterested){ 
      System.out.println("Server: client isn't interested in downloading");
      return true;
    }
    try {

      byte[] block = downloadManager.readBlock(index, offset, pieceDataLength);
    
      if (block != null) {
        ByteBuffer pieceMessage = MessageMaker.makePieceMessage(index, offset, block);
        if (!sendMessage(pieceMessage)) {
          return false;
        }
        System.out.println("Server: sent to client piece " + index + " with offset " + offset + " in it");
      } else {
        System.out.println("Server: failed to read block from disk");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  public boolean sendMessage(ByteBuffer message) {
    try { 
      while (message.hasRemaining()) {
        int bytesSent = client.write(message);
        if (bytesSent == -1) {
          return false;
        }
        System.out.println("Server: sent " + bytesSent + " bytes to peer");
      }
    } catch (IOException e) {
      System.out.println("Server: error sending message: " + e.getMessage());
      return false;
    }
    return true;
  }

  public boolean getIsHandShake() {
    return isHandShake;
  }

  public void setIsHandShake() {
    isHandShake = true;
  }

  public void setChannel(SocketChannel channel) {
    client = channel;
  }

  public boolean isGetBitfield() {
    return isGetBitfield;
  }
}
