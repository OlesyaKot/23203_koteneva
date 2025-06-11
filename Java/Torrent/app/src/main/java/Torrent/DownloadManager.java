package Torrent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadManager {
  private TorrentContext context;
  private FileHandler fileHandler;
  private final Map<SocketChannel, ServerPeerProccess> connectedPeers = new ConcurrentHashMap<>();

  private BitSet downloadedPieces;  // какие куски у нас уже есть.
  private final Map<Integer, PieceProgress> piecesProgress = new ConcurrentHashMap<>();  // прогресс по частично загруженным кускам.
  private final BlockingQueue<Integer> requestQueue = new LinkedBlockingQueue<>();  // очередь из индексов нужных закачать кусков

  public DownloadManager(TorrentContext context) throws NoSuchAlgorithmException, IOException {
    this.context = context;

    try {
      fileHandler = new FileHandler(context); 
      downloadedPieces = fileHandler.parseDataFile(); // здесь используется PieceChecker
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    }

    // System.out.print("BitSet len " + downloadedPieces.size() + " (binary): "); // штука печатает downloadedPieces в формате битов
    // for (int i = 0; i < context.getNumOfPieces(); i++) {
    //     System.out.print(downloadedPieces.get(i) ? '1' : '0');
    //     if (i % 8 == 7) System.out.print(" "); // разделяем по байтам
    // }
    // System.out.println();

    // Формируем очередь недостающих кусков
    for (int i = 0; i < context.getNumOfPieces(); i++) {
      if (!downloadedPieces.get(i)) {
        requestQueue.offer(i);
      }
    }

    // System.out.println("requestQueue: " + requestQueue);
  }

  public void receiveBlock(int pieceIdx, int offset, byte[] pieceData) throws NoSuchAlgorithmException, IOException { // это мы качаем кусок от пира
    System.out.println("RECIEVE BLOCK: VERIFY HASH 1");

    if (hasVerified(pieceIdx))
      return;
    System.out.println("RECIEVE BLOCK: VERIFY HASH 2");

    PieceProgress progress = piecesProgress.get(pieceIdx);

    if (progress == null) {
      progress = new PieceProgress(context, pieceIdx); // создавать кусок в torrentProccess
      piecesProgress.put(pieceIdx, progress);
    }

    if (!progress.addBlock(pieceData, offset)) {
      System.out.println("RECIEVE BLOCK: VERIFY HASH 3");
      return;
    }

    if (!progress.isComplete()) {
      System.out.println("RECIEVE BLOCK: BLOCK ISN'T COMPLETE");
      requestQueue.offer(pieceIdx);
      // System.out.println("RECIEVE BLOCK: requestQueue: " + requestQueue);

      return;
    }

    System.out.println("RECIEVE BLOCK: VERIFY HASH 4");

    // тут надо проверить, какую часть куска мы получили (TODO: добавила ниже)
    // if (!progress.isComplete()) {
    //   requestQueue.add(pieceIdx);
    // }

    try {
      if (progress.verifyHash()) {
        saveCompletedPiece(pieceIdx, progress.collect());

        synchronized (downloadedPieces) {
          downloadedPieces.set(pieceIdx);
        }
        piecesProgress.remove(pieceIdx);
        System.out.println("Client: recieve FULL BLOCK");
        // torrentProccess.removeProgress(pieceIdx);
      } else {
        piecesProgress.put(pieceIdx, null);
        requestQueue.offer(pieceIdx);
        // torrentProccess.resetProgress(pieceIdx);
      }
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    }
  }

  private void saveCompletedPiece(int pieceIdx, byte[] completePiece) throws IOException {
    System.out.println("FULL PIECE");
    fileHandler.writePiece(completePiece, pieceIdx); // записываем нам весь кусок

    // ByteBuffer message = MessageMaker.makeHaveMessage(pieceIdx); // отправляем всем, что у нас теперь есть этот кусок (BroadcastHave)
    System.out.println("Client: sent all servers we had ");
    // for (ServerPeerProccess peer : connectedPeers.values()) {
    //   peer.sendMessage(message);
    // }
  }

  public boolean hasVerified(int idx) {
    synchronized (downloadedPieces) {
      // System.out.println("CLient: index " + idx + "; hasVerified:");
      // System.out.print("BitSet len " + downloadedPieces.size() + " (binary): "); // штука печатает downloadedPieces в формате битов
      // for (int i = 0; i < context.getNumOfPieces(); i++) {
      //     System.out.print(downloadedPieces.get(i) ? '1' : '0');
      //     if (i % 8 == 7) System.out.print(" "); // разделяем по байтам
      // }
      // System.out.println();
      if (downloadedPieces != null) {
        return downloadedPieces.get(idx);
      }
    }
    return false;
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

  public BitSet getAvailPieces() {
    return downloadedPieces;
}

  public int getNumOfPieces() {
    return context.getNumOfPieces();
  }

  public int getNextNeededPiece(BitSet serverAvailablePieces) throws IOException {
    Integer index = -1;
    while (!requestQueue.isEmpty()) {
      index = requestQueue.poll();
      System.out.println("Client: lower level: wants to load piece number " + index);
      if (serverAvailablePieces.get(index)) {
        // PieceProgress progress = piecesProgress.get(index);
        System.out.println("Client: lower level: server has piece number " + index);
        // if ()
        // requestQueue.offer(index); // TODO: !!!!!!!!!!!!!!!!!!
        break;
      } else {
        // Возвращаем обратно в очередь, если не доступен сейчас
        requestQueue.offer(index);
        index = -1;
      }
    }

    if (index == -1){
      // fileHandler.closeFile();
    }
    return index;
  }
  
  public int getBlockOffset(int index) {
    // PieceProgress progress = torrentProccess.getPieceProgress(index);
    if (piecesProgress.get(index) == null) {
      PieceProgress progress = new PieceProgress(context, index);
      piecesProgress.put(index, progress);
    }

    // if (piecesProgress.get(index).getNextOffset() < piecesProgress.get(index).getpieceSize()) {
    //   requestQueue.offer(index); 
    // }
    return piecesProgress.get(index).getNextOffset(); // TODO: если кусок состоит из нескольких частей, то отсюда всегда будет возвращаться всего запрос на 1 часть куска, а остального не будет
    // то есть либо отправлять в очередь по количетсву частей в куске, либо как-то надо закрепить за кусоком, чтобы он смотрел на свои части и по оставшемуся количеству отправлял запросы
  }
  
  public void addConnectedPeer(SocketChannel channel, ServerPeerProccess peer) {
    connectedPeers.put(channel, peer);
  }

  public void removeConnectedPeer(SocketChannel channel) {
    connectedPeers.remove(channel);
  }

  public long getPieceSize(int index) {
    return piecesProgress.get(index).getpieceSize();
  }
}
