package Torrent;

public class TorrentContext{
  private String fileName;
  private long fileSize;
  private long pieceLength;
  private int numOfPieces;
  private byte[] infoHash;
  private byte[] pieces;
  private byte[] peerID;

  public void setFileName(String name) {
    fileName = name;
  }
  
  public void setFileSize(long size) {
    if (size > 0)
      fileSize = size;
  }
  
  public void setPieceLength(long length) {
    if (length > 0)
      pieceLength = length;
  }
  
  public void setNumOfPieces(int num) {
    if (num > 0)
      numOfPieces = num;
  }
  
  public void setPieces(byte[] pieces) {
    this.pieces = pieces;
  }

  public void setInfoHash(byte[] infoHash) {
    this.infoHash = infoHash;
  }

  public void setPeerID(byte[] peerID) {
    this.peerID = peerID;
  }

  public String getFileName() {
    return fileName;
  }
  
  public long getFileSize() {
    return fileSize;
  }

  public int getNumOfPieces() {
    return numOfPieces;
  }  

  public int getPieceLength() {
    return (int)pieceLength;
  }
  
  public byte[] getInfoHash() {
    return infoHash;
  }
  
  public byte[] getPieces() {
    return pieces;
  }

  public byte[] getPeerID() {
    return peerID;
  }
}
