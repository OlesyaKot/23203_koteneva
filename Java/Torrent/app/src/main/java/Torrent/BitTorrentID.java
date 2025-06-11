package Torrent;

public enum BitTorrentID {
  INTERESTED((byte) 2),
  NOT_INTERESTED((byte) 3),
  HAVE((byte) 4),
  BITFIELD((byte) 5),
  REQUEST((byte) 6),
  PIECE((byte) 7);

  private final byte value;

  BitTorrentID(byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

  // Статический метод для поиска типа по байту
  public static BitTorrentID fromByte(byte value) {
    for (BitTorrentID type : values()) {
      if (type.value == value) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown message type: " + value);
  }
}
