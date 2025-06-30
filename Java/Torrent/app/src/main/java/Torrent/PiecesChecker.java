package Torrent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;

public class PiecesChecker {
  private final int SHA_LEN = 20;

  public BitSet parseDataFile(TorrentContext context, File dataFile){
    BitSet availablePieces = new BitSet(context.getNumOfPieces());

    int totalPieces = context.getNumOfPieces();
    try{
      for (int i = 0; i < totalPieces; ++i) {
        if (verifyPieceHash(dataFile, context, i)) {
          availablePieces.set(i);
        }
      }
    } catch (IOException | NoSuchAlgorithmException e) {
      System.out.println("Cannot parse dataFile and create available piece map");
      e.printStackTrace();
    }
    System.out.println("Need to load " + (context.getNumOfPieces() - availablePieces.cardinality()) + " pieces");

    return availablePieces;
  }
  
//  Проверяет один кусок по индексу.
  public boolean verifyPieceHash(File file, TorrentContext context, int pieceIndex) throws IOException, NoSuchAlgorithmException { 
    try (RandomAccessFile randAccFile = new RandomAccessFile(file, "r")) {
      long pieceLength = context.getPieceLength();
      long fileSize = context.getFileSize();

      long position = pieceIndex * pieceLength;
      long length = Math.min(pieceLength, fileSize - position);
      byte[] buffer = new byte[(int) length];
      randAccFile.seek(position);
      int bytesRead = randAccFile.read(buffer);

      if (bytesRead != length)
        return false;

      return checkHash(buffer, context.getPieces(), pieceIndex);
    }
  }

  private boolean checkHash(byte[] data, byte[] allPieceHashes, int pieceIndex) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    byte[] currHash = digest.digest(data);

    byte[] expectedHash = new byte[SHA_LEN];
    System.arraycopy(allPieceHashes, pieceIndex * SHA_LEN, expectedHash, 0, SHA_LEN);

    return Arrays.equals(currHash, expectedHash);
  }
}
