package Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DownloadManager {
  private TorrentContext context;
  private FileHandler fileHandler;
  private final Map<SocketChannel, ServerPeerProccess> connectedClietns = new ConcurrentHashMap<>();

  private BitSet downloadedPieces;  // какие куски у нас уже есть.
  private final Map<Integer, PieceProgress> piecesProgress = new ConcurrentHashMap<>();  // прогресс по частично загруженным кускам.
  private final Queue<Integer> requestQueue = new ConcurrentLinkedQueue<>();  // очередь из индексов нужных закачать кусков

  public DownloadManager(TorrentContext context) throws NoSuchAlgorithmException, IOException {
    this.context = context;

    try {
      fileHandler = new FileHandler(context); 
      downloadedPieces = fileHandler.parseDataFile(); // здесь используется PieceChecker
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < context.getNumOfPieces(); i++) {
      if (!downloadedPieces.get(i)) {
        requestQueue.offer(i);
      }
    }
  }

  public void receiveBlock(int pieceIdx, int offset, byte[] pieceData) throws IOException { 
    // System.out.println("RECIEVE BLOCK: VERIFY HASH 1");

    synchronized (downloadedPieces) {
      if (downloadedPieces.get(pieceIdx)){
        return;
      }
    }

    // System.out.println("RECIEVE BLOCK: VERIFY HASH 2");

    PieceProgress progress = piecesProgress.get(pieceIdx);

    assert progress != null; 

    if (!progress.addBlock(pieceData, offset)) {
      // System.out.println("RECIEVE BLOCK: VERIFY HASH 3");
      return;
    }

    if (!progress.isComplete()) {
      System.out.println("RECIEVE BLOCK: BLOCK ISN'T COMPLETE");
      return;
    }

    // System.out.println("RECIEVE BLOCK: VERIFY HASH 4");

    try {
      if (progress.verifyHash()) {
        saveCompletedPiece(pieceIdx, progress.collect());

        synchronized (downloadedPieces) {
          downloadedPieces.set(pieceIdx);
        }

        piecesProgress.remove(pieceIdx);
        System.out.println("Client: recieve FULL BLOCK");
      } else {
        piecesProgress.put(pieceIdx, null);

        requestQueue.offer(pieceIdx);
      }
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    }
  }

  private void saveCompletedPiece(int pieceIdx, byte[] completePiece) throws IOException {
    System.out.println("FULL PIECE");
    fileHandler.writePiece(completePiece, pieceIdx); // записываем нам весь кусок

    ByteBuffer haveMessage = MessageMaker.makeHaveMessage(pieceIdx); // отправляем всем, что у нас теперь есть этот кусок (BroadcastHave)
    System.out.println("Client: sent all servers we had ");
    for (ServerPeerProccess peer : connectedClietns.values()) {
      if (peer.isGetBitfield()) {
        peer.sendMessage(haveMessage);
      }
    }
  }

  public byte[] readBlock(int index, int offset, int pieceDataLength) throws IOException {
    return fileHandler.readBlock(index, offset, pieceDataLength);
  }

  public byte[] getPeerID() {
    return context.getPeerID();
  }

  public byte[] getInfoHash() {
    return context.getInfoHash();
  }

  public synchronized BitSet getAvailPieces() {
    return downloadedPieces;
}

  public int getNumOfPieces() {
    return context.getNumOfPieces();
  }

  public int getNextNeededPiece(BitSet serverAvailablePieces) {
    if (serverAvailablePieces.isEmpty()) 
      return -1;

    synchronized (downloadedPieces) {
      if (downloadedPieces.cardinality() == context.getNumOfPieces()) {
        System.out.println(serverAvailablePieces.cardinality());
        return -2;
      }

      if (requestQueue.isEmpty()) {
        for (int i = 0; i < context.getNumOfPieces(); i++) {
          if (!downloadedPieces.get(i)) {
            piecesProgress.put(i, null);
            requestQueue.offer(i);
          }
        }
      }
    }

    for (int i = 0; i < requestQueue.size(); ++i) {
      int index = requestQueue.poll();
      System.out.println("Client: lower level: wants to load piece number " + index);

      if (serverAvailablePieces.get(index)) {
        System.out.println("Client: lower level: server has piece number " + index);
        return index;
      }

      requestQueue.offer(index);
    }
    return -1;
  }

  public synchronized int getBlockOffset(int index) {
    if (downloadedPieces.get(index))
      return -1;
    
    if (piecesProgress.get(index) == null) {
      PieceProgress progress = new PieceProgress(context, index);
      piecesProgress.put(index, progress);
    }

    return piecesProgress.get(index).getNextOffset(); 
  }
  
  public int getBlockLen(int index, int offset) {
    PieceProgress piece = piecesProgress.get(index);
    if (Math.abs(piece.getpieceSize() - (long) offset) < PieceProgress.getBlockLen()) {
      return (int) Math.abs(piece.getpieceSize() - (long) offset);
    }
    return PieceProgress.getBlockLen();
  }

  public void addConnectedPeer(SocketChannel channel, ServerPeerProccess peer) {
    connectedClietns.put(channel, peer);
  }

  public void removeConnectedPeer(SocketChannel channel) {
    connectedClietns.remove(channel);
  }
}
