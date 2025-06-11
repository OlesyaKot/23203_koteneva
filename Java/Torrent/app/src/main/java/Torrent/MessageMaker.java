package Torrent;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class MessageMaker {
    
  public static ByteBuffer makePieceMessage(int index, int offset, byte[] block) {
    int messageLength = 9 + block.length; // 4 (index) + 4 (offset) + 1 (id) + data
    ByteBuffer buffer = ByteBuffer.allocate(4 + messageLength);

    buffer.putInt(messageLength);                     // длина сообщения
    buffer.put(BitTorrentID.PIECE.getValue());        // ID сообщения
    buffer.putInt(index);                             // индекс куска
    buffer.putInt(offset);                            // смещение
    buffer.put(block);                                // данные блока
    buffer.flip();

    // System.out.println("Server: Message maker: len " + messageLength + " and realsize " + buffer.remaining());
    return buffer;     
  }
  
  public static ByteBuffer makeHandShakeResponse(byte[] infoHash, byte[] peerId) {
    ByteBuffer buffer = ByteBuffer.allocate(49 + 20 + 20); // Протокол + infoHash + peerId

    // Протокол BitTorrent v1
    buffer.put((byte) 19); // длина строки "BitTorrent protocol"
    buffer.put("BitTorrent protocol".getBytes()); // имя протокола

    byte[] reserved = new byte[8];   // Зарезервированные флаги (все нули)
    buffer.put(reserved);
    buffer.put(infoHash); // Info hash
    buffer.put(peerId); // Peer ID
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeBitFieldResponse(BitSet bitSet, int numOfPieces) {
    // byte[] bitField = new byte[(numOfPieces + 7) / 8]; // на всякий округляем в большую сторону, размером, кратным 8

    // for (int i = 0; i < bitField.length; ++i) {
    //   for (int j = 0; j < 8; ++j) {
    //     if (bitSet.get(i * 8 + j)) {
    //       bitField[i] |= (1 << (7 - j)); // устанавливаем соответствующий бит
    //     }
    //   }
    // }

    byte[] bitField = bitSet.toByteArray(); // ✅ Преобразуем в byte[]
    // System.out.println("BitSet to byte[]: " + Arrays.toString(bitField));

    ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + bitField.length);
    buffer.putInt(1 + bitField.length);           // длина сообщения
    buffer.put(BitTorrentID.BITFIELD.getValue()); // ID сообщения
    buffer.put(bitField);                         // само битовое поле
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeInterestedMessage() {
    ByteBuffer buffer = ByteBuffer.allocate(5);
    buffer.putInt(1);                               // длина сообщения
    buffer.put(BitTorrentID.INTERESTED.getValue()); // INTERESTED
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeHaveMessage(int pieceIndex) {
    ByteBuffer buffer = ByteBuffer.allocate(9);
    buffer.putInt(5); // длина сообщения
    buffer.put(BitTorrentID.HAVE.getValue()); // HAVE
    buffer.putInt(pieceIndex);
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeRequestMessage(int idx, int offset, int len) {
    ByteBuffer buffer = ByteBuffer.allocate(17); // 4 (length) + 1 (id) + 4 (index) + 4 (begin) + 4 (length)
    buffer.putInt(13); // длина полезной нагрузки
    buffer.put(BitTorrentID.REQUEST.getValue());
    buffer.putInt(idx);
    buffer.putInt(offset);
    buffer.putInt(len);
    buffer.flip();
    return buffer;
  }
  
  public static ByteBuffer makeKeepAliveMessage() {
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putInt(0); // keep-alive
    buffer.flip();
    return buffer;
  }
}
