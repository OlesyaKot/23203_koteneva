package Torrent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

// Этот класс отвечает за: 

//     Чтение блоков кусков (для раздачи)
//     Запись кусков на диск (при скачивании)
//     Отслеживание завершённых кусков     

public class FileHandler {
  private TorrentContext context;
  private PiecesChecker piecesChecker = new PiecesChecker();
  private final RandomAccessFile raFile;
  private File localFile;

  public FileHandler(TorrentContext context) throws NoSuchAlgorithmException, IOException{
    this.context = context;

    localFile = new File(context.getFileName());
    if (!localFile.exists()) {
      System.out.println("File isn't exist. Create a new file");
      localFile.createNewFile();
    }
        
    raFile = new RandomAccessFile(localFile, "rw");
    raFile.setLength(context.getFileSize()); // Устанавливаем финальный размер файла 
  }
  
  public BitSet parseDataFile() {
    return piecesChecker.parseDataFile(context, localFile);
  }

  public byte[] readBlock(int index, int offset, int partOfPieceLength) throws IOException{
    try {
      
      long pos = (long) index * context.getPieceLength() + offset;
      byte[] block = new byte[partOfPieceLength];
      
      long readBytes;
      synchronized (raFile) {
        raFile.seek(pos);
        readBytes = raFile.read(block);
      }

      if (index == context.getNumOfPieces() - 1) {
        if (readBytes != context.getFileSize() - context.getPieceLength() * index)
          return null; 
      } else {
        if (readBytes != partOfPieceLength)
          return null;
      }

      return block;
    } catch (IOException e){
      e.printStackTrace();
      return null;
    }
  }
  
  public void writePiece(byte[] data, int pieceIndex) throws IOException {
    long pos = (long) pieceIndex * context.getPieceLength();

    // for (int i = 0; i < 20; ++i) {
    //   System.out.print(data[i]);
    // }
    // System.out.println();
    // try{
    // Thread.sleep(1000);
    // } catch (InterruptedException e){}

    synchronized (raFile) {
      raFile.seek(pos);
      raFile.write(data);
    }
  }

  public void closeFile() throws IOException{
    raFile.close();
  }
}


// TODO: отследить все моменты клиентской части, где сервер отрубается - надо вернуть в очередь кусок который мы недокачали