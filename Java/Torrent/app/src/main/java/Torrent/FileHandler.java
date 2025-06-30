package Torrent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;

public class FileHandler {
  private TorrentContext context;
  private PiecesChecker piecesChecker = new PiecesChecker();
  private final RandomAccessFile raFile;
  private File localFile;

  public FileHandler(TorrentContext context) throws IOException {
    this.context = context;

    localFile = new File(context.getFileName());
    if (!localFile.exists()) {
      System.out.println("File isn't exist. Create a new file");
      localFile.createNewFile();
    }
        
    raFile = new RandomAccessFile(localFile, "rw");
    raFile.setLength(context.getFileSize()); 
  }
  
  public BitSet parseDataFile() {
    return piecesChecker.parseDataFile(context, localFile);
  }

  public byte[] readBlock(int index, int offset, int partOfPieceLength) throws IOException {
    try {
      
      long pos = (long) index * context.getPieceLength() + offset;
      byte[] block = new byte[partOfPieceLength];
      
      long readBytes;
      synchronized (raFile) {
        raFile.seek(pos);
        readBytes = raFile.read(block);
      }

      if (readBytes != partOfPieceLength) {
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

    synchronized (raFile) {
      raFile.seek(pos);
      raFile.write(data);
    }
  }

  public void closeFile() throws IOException{
    raFile.close();
  }
}
