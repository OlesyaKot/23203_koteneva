package Torrent;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class MessageMaker {
    
  public static ByteBuffer makePieceMessage(int index, int offset, byte[] block) {
    int messageLength = 9 + block.length; // 4 (index) + 4 (offset) + 1 (id) + data
    ByteBuffer buffer = ByteBuffer.allocate(4 + messageLength);

    buffer.putInt(messageLength);
    buffer.put(BitTorrentID.PIECE.getValue());        
    buffer.putInt(index);                             
    buffer.putInt(offset);                            
    buffer.put(block);                                
    buffer.flip();

    return buffer;     
  }
  
  public static ByteBuffer makeHandShakeResponse(byte[] infoHash, byte[] peerId) {
    ByteBuffer buffer = ByteBuffer.allocate(49 + 20 + 20); // Протокол + infoHash + peerId

    buffer.put((byte) 19); // длина строки "BitTorrent protocol"
    buffer.put("BitTorrent protocol".getBytes());

    byte[] reserved = new byte[8];  
    buffer.put(reserved);
    buffer.put(infoHash); 
    buffer.put(peerId); 
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeBitFieldResponse(BitSet bitSet, int numOfPieces) {
    byte[] bitField = bitSet.toByteArray();
    
    ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + bitField.length);
    buffer.putInt(1 + bitField.length);        
    buffer.put(BitTorrentID.BITFIELD.getValue()); 
    buffer.put(bitField);                        
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeInterestedMessage() {
    ByteBuffer buffer = ByteBuffer.allocate(5);
    buffer.putInt(1);                               
    buffer.put(BitTorrentID.INTERESTED.getValue()); 
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeHaveMessage(int pieceIndex) {
    ByteBuffer buffer = ByteBuffer.allocate(9);
    buffer.putInt(5); 
    buffer.put(BitTorrentID.HAVE.getValue());
    buffer.putInt(pieceIndex);
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeRequestMessage(int idx, int offset, int len) {
    ByteBuffer buffer = ByteBuffer.allocate(17); // 4 (length) + 1 (id) + 4 (index) + 4 (begin) + 4 (length)
    buffer.putInt(13); 
    buffer.put(BitTorrentID.REQUEST.getValue());
    buffer.putInt(idx);
    buffer.putInt(offset);
    buffer.putInt(len);
    buffer.flip();
    return buffer;
  }
}
