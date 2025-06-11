package Torrent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

// Вспомогательный класс для сборки куска  
// Хранит: 
//     Буфер куска.
//     Какие блоки уже получены.
//     Методы проверки завершённости.
// Так как 1 кусок весит 1 Мб, а качаем мы даже один кусок по частям, то это довольно логично также хранить состояния частей одного куска
     
public class PieceProgress {
  private final int SHA_LEN = 20;
  private int pieceIndex;
  private int pieceSize;
  private final int BLOCK_SIZE = 8192 * 2; // 16 Кб 8192 *
  private final Map<Integer, byte[]> blocks = new HashMap<>(); // Хранит полученные блоки куска → ключ — offset, значение — данные
  private final BitSet receivedBlocks; // какие блоки куска уже получены
  private int numberOfBlock;
  private TorrentContext context;
    
  public PieceProgress(TorrentContext context, int pieceIndex) {
    this.context = context;
    this.pieceIndex = pieceIndex;
    pieceSize = Math.min(context.getPieceLength(), (int) (context.getFileSize() - (long) pieceIndex * context.getPieceLength()));
    numberOfBlock = (pieceSize + BLOCK_SIZE - 1) / BLOCK_SIZE;
    System.out.println("numberOfBlock " + numberOfBlock);
    System.out.println("PIECE SIZE " + pieceSize);
    receivedBlocks = new BitSet(numberOfBlock); 
  }
  
  public synchronized boolean addBlock(byte[] data, int offset) {
    int num = offset / BLOCK_SIZE;
    
    if (receivedBlocks.get(num))
      return false;
    
    System.out.println(offset + data.length);
    if (offset < 0 || offset + data.length > pieceSize) {
      return false;
    }

    receivedBlocks.set(num);
    blocks.put(offset, data);
    return true;
  }
  
  public synchronized boolean isComplete() {
    return receivedBlocks.cardinality() == numberOfBlock;
  }
  
  public boolean verifyHash() throws NoSuchAlgorithmException {
    byte[] expectedHash = null;
    byte[] hash = null;

    try {
      System.out.println("1 verifyHashverifyHashverifyHashverifyHash");

      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      System.out.println("2 verifyHashverifyHashverifyHashverifyHash");
      expectedHash = Arrays.copyOfRange(context.getPieces(), pieceIndex * SHA_LEN, pieceIndex * SHA_LEN + SHA_LEN);
      System.out.println("expectedHash " + expectedHash);
      System.out.println("3 verifyHashverifyHashverifyHashverifyHash");

      hash = digest.digest(collect());
      System.out.println("4 verifyHashverifyHashverifyHashverifyHash");
      System.out.println("hash " + hash);     

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    System.out.println("5 verifyHashverifyHashverifyHashverifyHash");
    System.out.println(Arrays.equals(hash, expectedHash));
    // return true;
    return Arrays.equals(hash, expectedHash);
  }
  
  public byte[] collect() {
    byte[] result = new byte[pieceSize];
    for (Map.Entry<Integer, byte[]> entry : blocks.entrySet()) {
      System.arraycopy(entry.getValue(), 0, result, entry.getKey(), entry.getValue().length);
    }
    // for (Map.Entry<Integer, byte[]> entry : blocks.entrySet()) {
    //   int offset = entry.getKey();
    //   byte[] block = entry.getValue();
  
    //   if (offset < 0 || offset + block.length > result.length) {
    //       System.out.println("Блок выходит за пределы куска: offset=" + offset + ", length=" + block.length);
    //       continue; // пропускаем невалидные блоки
    //   }
    //   System.arraycopy(block, 0, result, offset, block.length);
    // }
    return result;
  }

  public int getNextOffset() {
    for (int i = 0; i < pieceSize; i += BLOCK_SIZE) {
      if (!receivedBlocks.get(i / BLOCK_SIZE)) {
        return i;
      }
    }
  return -1; // все блоки уже получены
  }
  
  public int getpieceSize() {
    return pieceSize;
  }
}
