package Torrent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
     
public class PieceProgress {
  private enum BlockState {
    DOWNLOADING, RAW
  }

  private final static int SHA_LEN = 20;
  private int pieceIndex;
  private long pieceSize;
  private final static int BLOCK_SIZE = 8192 * 2; // 16 Кб
  private final Map<Integer, byte[]> blocks = new HashMap<>(); // Хранит полученные блоки куска → ключ — offset, значение — данные
  private final BitSet receivedBlocks; // какие блоки куска уже получены
  private int numberOfBlocks;
  private TorrentContext context;
  private BlockState[] states;
    
  public PieceProgress(TorrentContext context, int pieceIndex) {
    this.context = context;
    this.pieceIndex = pieceIndex;
    pieceSize = Math.min(context.getPieceLength(), (context.getFileSize() - (long) pieceIndex * context.getPieceLength()));
    numberOfBlocks = (int) ((pieceSize + BLOCK_SIZE - 1) / BLOCK_SIZE);
    System.out.println("numberOfBlock " + numberOfBlocks);
    System.out.println("PIECE SIZE " + pieceSize);
    receivedBlocks = new BitSet(numberOfBlocks);
    states = new BlockState[numberOfBlocks];
    Arrays.fill(states, BlockState.RAW);
  }
  
  public synchronized boolean addBlock(byte[] data, int offset) {
    int num = offset / BLOCK_SIZE;
    
    if (receivedBlocks.get(num)) {
      states[num] = BlockState.DOWNLOADING;
      return false;
    }
    
    System.out.println(offset + data.length);
    assert !(offset < 0 || offset + data.length > pieceSize);

    receivedBlocks.set(num);
    states[num] = BlockState.DOWNLOADING;
    blocks.put(offset, data);
    return true;
  }
  
  public synchronized boolean isComplete() {
    return receivedBlocks.cardinality() == numberOfBlocks;
  }
  
  public boolean verifyHash() throws NoSuchAlgorithmException {
    byte[] expectedHash = null;
    byte[] hash = null;

    try {

      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      expectedHash = Arrays.copyOfRange(context.getPieces(), pieceIndex * SHA_LEN, pieceIndex * SHA_LEN + SHA_LEN);

      hash = digest.digest(collect());

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    System.out.println(Arrays.equals(hash, expectedHash));
    return Arrays.equals(hash, expectedHash);
  }
  
  public byte[] collect() {
    byte[] result = new byte[(int) pieceSize]; 
    for (Map.Entry<Integer, byte[]> entry : blocks.entrySet()) {
      System.arraycopy(entry.getValue(), 0, result, entry.getKey(), entry.getValue().length);
    }
    return result;
  }
    
  public synchronized int getNextOffset() {
    for (int i = 0; i < numberOfBlocks; ++i) {
      if (!receivedBlocks.get(i) && states[i] == BlockState.RAW) {
        states[i] = BlockState.DOWNLOADING;
        return i * BLOCK_SIZE;
      }
    }
    return -1;
  }
  
  public long getpieceSize() {
    return pieceSize;
  }

  public static int getBlockLen() {
    return BLOCK_SIZE;
  }
}
