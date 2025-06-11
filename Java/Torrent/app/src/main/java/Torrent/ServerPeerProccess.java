package Torrent;

import java.io.IOException;

// Парсить сообщения.
// Обрабатывать handshake.
// Управлять состоянием пира.
// Отвечать на запросы request.     

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

public class ServerPeerProccess implements PeerProccess{   // основной класс, где сервер будет работать с каждый клиентом
  private boolean isHandShake = false;
  private boolean isInterested = false;
  private SocketChannel client;
  private BitSet clientAvailablePieces;
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
    buff.get(infoHash); // его надо сравнить с хэшом нашего торрент файла
    if (!Arrays.equals(infoHash, downloadManager.getInfoHash())){
      System.out.println("Server: client work with enother torrent file");
      return;
    }

    buff.get(peerID); // TODO: ну тип нам наде где-то и куда-то сохранить id пира


    // надо в ответ отправить пиру свои данные 
    ByteBuffer responseHandShake = MessageMaker.makeHandShakeResponse(downloadManager.getInfoHash(), downloadManager.getPeerID());
    sendMessage(responseHandShake); // надо отослать ответ
    System.out.println("Server: send our handshake to client");


    isHandShake = true;
    System.out.println("Server: handshake with peer (ID " + peerID + ") done");

    // ByteBuffer bitField = MessageMaker.makeBitFieldResponse(downloadManager.getAvailPieces(), downloadManager.getNumOfPieces());
    // sendMessage(bitField); // а еще надо отослать клиенту имеющиеся у нас куски (bitfield) 
    return;
  }
  
// Handshake всегда имеет длину 68 байт:
//  1 байт - длина протокола (length) - у BitTorrent она всегда 19
//  19 байт - название протокола ("BitTorrent protocol")
//  8 байт - reserved
//  20 байт - info_hash
//  20 байт - peer_id

// обрабатывает входящие сообщения по протоколу BitTorrent (BEP-0003) .
//  Каждое сообщение имеет определённый формат :
// <length prefix> <message ID> <payload>
// length (4б) - общая длина сообщения (включая ID и payload)
// message ID (1б) - тип сообщения (например, 6 = request, 7 = piece)
// payload  (переменная длина) - зависит от типа сообщения

  private boolean handleMessage(ByteBuffer buff){ // обрабатываем запрос от клиента (в зависимости от типа сообщение отвечаем клиенту)
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

        // case HAVE: { // have - клиент получил новый кусок
        //   int pieceIndex = buff.getInt();
        //   clientAvailablePieces.set(pieceIndex);
        //   break;
        // }

        case BITFIELD: { // bitField - какие куски есть у клиента
          byte[] bitField = new byte[messageLength - 1];
          if (buff.remaining() < bitField.length) {
            System.out.println("Server: incomplete bitfield message");
            return true;
          }
          
          buff.get(bitField);
          clientAvailablePieces = BitSet.valueOf(bitField);
          System.out.println("Server: received peer's bitfield.");

          ByteBuffer serverBitField = MessageMaker.makeBitFieldResponse(downloadManager.getAvailPieces(), downloadManager.getNumOfPieces());
          sendMessage(serverBitField); // а еще надо отослать клиенту имеющиеся у нас куски (bitfield) 
          System.out.println("Server: send bitfield to client.");
          break;
        }

        case REQUEST: { // request - Запрос на конкретный кусок
          if (buff.remaining() < 12) {
            System.out.println("Server: incomplete request message");
            return true;
          }
        
          int pieceidx = buff.getInt();
          int offset = buff.getInt();
          int pieceDataLength = buff.getInt();
          System.out.println("SERVER: CLIENT WANTS TO GET " + pieceDataLength + " with offset " + offset + " with index " + pieceidx);

          try {

            if (!handleRequest(pieceidx, offset, pieceDataLength)) {

              return false;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        }
      }
    }
    return true;
  }

  private boolean handleRequest(int index, int offset, int pieceDataLength) throws IOException {
    if (!isInterested || !downloadManager.hasVerified(index)) {
      System.out.println("Server: server doesn't have client's needed piece");
      return true;
    }
    try {
      // System.out.println("cccccccccccccc");

      byte[] block = downloadManager.readBlock(index, offset, pieceDataLength);
    
      byte[] tmp = Arrays.copyOf(block, 20);
      System.out.println("cccccccccccccc");

      System.out.print(new String(tmp, StandardCharsets.ISO_8859_1));
      System.out.println();
      try{
        Thread.sleep(10);
      } catch (InterruptedException e){}

      if (block != null) {
        ByteBuffer pieceMessage = MessageMaker.makePieceMessage(index, offset, block);
        if (!sendMessage(pieceMessage)) {
          return false;
        }
      } else {
        System.out.println("Server: failed to read block from disk");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  public boolean sendMessage(ByteBuffer message) {
    try { // Пока буфер не пуст — продолжаем запись
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
}



  // private void handlePiece(int pieceIndex, int offset, byte[] pieceData) {
  //   System.out.println("Received part of piece: " + pieceIndex + ", offset: " + offset + ", size: " + pieceData.length);

  //   if (piece.getState() == PieceState.VERIFIED) {
  //     System.out.println("Piece already verified. Ignoring.");
  //     return;
  //   }

  //   if (piece.addBlock(offset, pieceData)) {
  //     if (piece.isComplete()) {
  //       if (piece.verifyHash()) {
  //         byte[] fullPiece = piece.assemble();
  //         fileHandler.writePiece(pieceIndex, fullPiece);
  //         fileHandler.markPieceCompleted(pieceIndex);
  //         piece.setState(PieceState.VERIFIED);

  //         System.out.println("Piece " + pieceIndex + " completed and saved.");
  //         broadcastHave(pieceIndex);
  //       } else {
  //         System.err.println("Hash mismatch for piece: " + pieceIndex);
  //         piece.setState(PieceState.FAILED);
  //         piece.reset(); // очищаем данные
  //       }
  //     } else {
  //       piece.setState(PieceState.DOWNLOADING);
  //     }
  //   } else {
  //     System.err.println("Failed to add block to piece: " + pieceIndex);
  //   }
  // }













// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.nio.channels.SocketChannel;

// public class ServerPeerSession implements PeerSession {
//     private enum State { WAITING_FOR_HANDSHAKE, HANDSHAKE_RECEIVED, BITFIELD_RECEIVED, READY }
//     private State state = State.WAITING_FOR_HANDSHAKE;

//     private final SocketChannel channel;
//     private final TorrentContext context;
//     private final DownloadManager downloadManager;

//     public ServerPeerSession(SocketChannel channel, TorrentContext context, DownloadManager downloadManager) {
//         this.channel = channel;
//         this.context = context;
//         this.downloadManager = downloadManager;
//     }

//     @Override
//     public void onConnected() {
//         System.out.println("Ожидаем handshake от клиента");
//     }

//     @Override
//     public boolean process(ByteBuffer buffer) {
//         switch (state) {
//             case WAITING_FOR_HANDSHAKE:
//                 if (!receiveHandshake(buffer)) return false;
//                 sendHandshake();
//                 sendBitfield();
//                 state = State.HANDSHAKE_RECEIVED;
//                 break;

//             case HANDSHAKE_RECEIVED:
//                 if (!receiveBitfield(buffer)) return false;
//                 state = State.BITFIELD_RECEIVED;
//                 break;

//             case BITFIELD_RECEIVED:
//             case READY:
//                 if (!handleRequestMessages(buffer)) return false;
//                 break;
//         }
//         return true;
//     }

//     private void sendHandshake() {
//         ByteBuffer handshake = MessageMaker.makeHandShakeResponse(
//             downloadManager.getInfoHash(),
//             downloadManager.getPeerID()
//         );
//         sendMessage(handshake);
//     }

//     private void sendBitfield() {
//         ByteBuffer bitField = MessageMaker.makeBitFieldResponse(
//             downloadManager.getAvailPieces(),
//             downloadManager.getNumOfPieces()
//         );
//         sendMessage(bitField);
//     }

//     private boolean receiveHandshake(ByteBuffer buff) {
//         if (buff.remaining() < 68) return true; // ждём больше данных

//         byte pstrlen = buff.get();
//         if (pstrlen != 19) return false;

//         byte[] protocolName = new byte[19];
//         buff.get(protocolName);

//         if (!new String(protocolName).equals("BitTorrent protocol")) return false;

//         byte[] reserved = new byte[8];
//         buff.get(reserved);

//         byte[] infoHash = new byte[20];
//         buff.get(infoHash);

//         if (!java.util.Arrays.equals(infoHash, context.getInfoHash())) return false;

//         byte[] peerId = new byte[20];
//         buff.get(peerId); // сохраняем peer_id пира

//         System.out.println("Получен handshake от пира");
//         return true;
//     }

//     private boolean receiveBitfield(ByteBuffer buff) {
//         if (buff.remaining() < 5) return true;

//         int length = buff.getInt();
//         byte msgId = buff.get();

//         if (msgId != 5) return true;

//         byte[] bitField = new byte[length - 1];
//         if (buff.remaining() < bitField.length) return true;

//         buff.get(bitField);
//         System.out.println("Получен bitfield от пира");
//         return true;
//     }

//     private boolean handleRequestMessages(ByteBuffer buff) {
//         while (buff.hasRemaining()) {
//             if (buff.remaining() < 4) return true;

//             int length = buff.getInt();
//             if (length == 0) continue;

//             if (buff.remaining() < 1) return true;

//             byte msgId = buff.get();
//             switch (BitTorrentID.fromByte(msgId)) {
//                 case INTERESTED:
//                     System.out.println("Клиент интересуется загрузкой");
//                     break;
//                 case REQUEST:
//                     handleRequest(buff);
//                     break;
//                 default:
//                     System.out.println("Неизвестное сообщение: " + msgId);
//             }
//         }
//         return true;
//     }

//     private void handleRequest(ByteBuffer buff) {
//         try {
//             int index = buff.getInt();
//             int offset = buff.getInt();
//             int length = buff.getInt();

//             byte[] block = downloadManager.readBlock(index, offset, length);
//             ByteBuffer response = MessageMaker.makePieceMessage(index, offset, block);
//             sendMessage(response);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private boolean sendMessage(ByteBuffer message) {
//         try {
//             while (message.hasRemaining()) {
//                 int bytesSent = channel.write(message);
//                 if (bytesSent == -1) return false;
//             }
//             return true;
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }
// }