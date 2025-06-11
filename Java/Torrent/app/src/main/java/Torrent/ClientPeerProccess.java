package Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;

public class ClientPeerProccess implements PeerProccess {
  private final int BLOCK_LEN = 8192 * 2;
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
    // System.out.println("CLIENtCLIENtCLIENtCLEIt");
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

  private boolean doHandShake(ByteBuffer buff) { // делаем handShake с отправкой пиру имеющихся у нас кусоков
    isHandShake = true;

    if (buff.remaining() != 68) {
      System.out.println("Client: size of peer's info hash incorrect");
      return false;
    }

    int protocolLength = buff.get();
    if (protocolLength != 19) {
      System.out.println("Client: wrong length of client's protocol");
      return false;
    }

    buff.get(protocolName);
    String protocolNameStr = new String(protocolName);

    if (!protocolNameStr.equals("BitTorrent protocol")) {
      System.out.println("Client: this is not BitTorrent protocol");
      return false;
    }

    buff.get(reservedBytes); // это зарезервированные флаги, игнорим
    buff.get(infoHash); // его надо сравнить с хэшом нашего торрент файла
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
  
  private boolean handleMessage(ByteBuffer buff) throws IOException{ // обрабатываем запрос от клиента (в зависимости от типа сообщение отвечаем клиенту)
    while (buff.hasRemaining()) {
      if (buff.remaining() < 4) {
        System.out.println("Client: incorrect peer message's length");
        return true;
      }

      System.out.println("CLIENT: lower level: what bytes left: " + buff.remaining());

      int messageLength = buff.getInt(); // TODO: !!!!!!!!!!!!!!!!!!!!!!!
      System.out.println("CLIENT: lower level: messageLength: " + messageLength);
  
      // if (messageLength == 0) {
      //   System.out.println("Keep-alive message");
      //   continue;
      // }

      if (buff.remaining() < 1) {
        System.out.println("Client: no ID from server's message");
        return true;
      }

      BitTorrentID type = BitTorrentID.fromByte(buff.get());
      switch (type) {
        case BITFIELD: { // bitField - какие куски есть у клиента
          byte[] bitField = new byte[messageLength - 1];
          if (buff.remaining() < bitField.length) {
            System.out.println("Client: incomplete bitfield message");
            return true;
          }
          
          buff.get(bitField);
          serverAvailablePieces = BitSet.valueOf(bitField);

          // System.out.println("Client: recieve from  server bitfiels in byte[] " + bitField);
          // System.out.println("Client: recieve from  server BitSet in (binary): " + serverAvailablePieces); // штука печатает downloadedPieces в формате битов
          // for (int i = 0; i < context.getNumOfPieces(); i++) {
          //     System.out.print(downloadedPieces.get(i) ? '1' : '0');
          //     if (i % 8 == 7) System.out.print(" "); // разделяем по байтам
          // }
          // System.out.println();

          System.out.println("CLient: recieved bitfield from server");

          ByteBuffer interested = MessageMaker.makeInterestedMessage();
          if (!sendMessage(interested)) {
            return false;
          }
          sendNextRequest();
          break;
        }

        case PIECE: { // Piece - получение блока куска (если мы сами качаем).
          // System.out.println("CLIENT: lower level: what bytes left: " + buff.remaining());

          System.out.println("Client: lower level: get PIECE message from server");
          if (buff.remaining() < 8) {
            System.out.println("Client: incomplete piece message header");
            return true;
          }

          int pieceIdx = buff.getInt();
          int offset = buff.getInt();
          int pieceDataLength = messageLength - 9; // messageLength - (ID + index + begin)  4 (index) + 4 (offset) + 1 (id) 
          System.out.println("CLIENT: lower level: pieceDataLen: " + pieceDataLength + "; what bytes left: " + buff.remaining());
          if (buff.remaining() < pieceDataLength) {
            System.out.println("Client: incomplete piece message data");
            return true;
          }

          byte[] pieceData = new byte[pieceDataLength];
          buff.get(pieceData);
          try {
            System.out.println("CLient: recieved piece from server");
            downloadManager.receiveBlock(pieceIdx, offset, pieceData);
          } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
          }
          sendNextRequest();
          break;
        }
      }
    }
    return true;
  }
  
  private boolean sendNextRequest() throws IOException{
    int nextIndex = downloadManager.getNextNeededPiece(serverAvailablePieces);
    if (nextIndex == -1) {
      System.out.println("Clilent: I've already have all pieces");
      return true;
    }

    int offset = downloadManager.getBlockOffset(nextIndex); // TODO: throws IOException
    if (offset == -1) {
      return true;
    }
    System.out.println("Clilent: want to load piece " + nextIndex + " with offset " + offset + " in it");

    try {
      long block_len = BLOCK_LEN;
      if (Math.abs(downloadManager.getPieceSize(nextIndex) - (long) offset * BLOCK_LEN) < BLOCK_LEN) {
        block_len = Math.abs(downloadManager.getPieceSize(nextIndex) - (long) offset * BLOCK_LEN);
      }
      
      ByteBuffer request = MessageMaker.makeRequestMessage(nextIndex, offset, (int)block_len);
      if (!sendMessage(request)) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  private boolean sendMessage(ByteBuffer message) {
    try { // Пока буфер не пуст — продолжаем запись
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
