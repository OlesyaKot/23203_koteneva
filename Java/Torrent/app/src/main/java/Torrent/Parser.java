package Torrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;

public class Parser {
  public static void parse(TorrentContext context, String path) throws IOException, NoSuchAlgorithmException {
    File torrentFile = new File(path);
        
    byte[] torrentBytes = Files.readAllBytes(torrentFile.toPath());
        
    try (FileInputStream fis = new FileInputStream(torrentFile)) {
      BDecoder decoder = new BDecoder(fis);
      BEValue decoded = decoder.bdecode();
      Map<String, BEValue> root = decoded.getMap();
      Map<String, BEValue> info = root.get("info").getMap();

      String fileName = info.get("name").getString();
      long totalLength = info.get("length").getLong();
      long pieceLength = info.get("piece length").getLong();
            
      byte[] pieces = info.get("pieces").getBytes();
      int pieceCount = pieces.length / 20;

      String[] parts = path.split("/");
      context.setFileName(new String(parts[0] + "/" + fileName));
      context.setFileSize(totalLength);
      context.setPieceLength(pieceLength);
      context.setPieces(pieces);
      context.setNumOfPieces(pieceCount);

      byte[] encodedInfo = getEncodedInfo(torrentBytes);
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] infoHash = digest.digest(encodedInfo);
            
      context.setInfoHash(infoHash);
      context.setPeerID(generatePeerID());

      System.out.println("File name: " + fileName);
      System.out.println("Total size: " + totalLength + " bytes");
      System.out.println("Piece length: " + pieceLength + " bytes");
      System.out.println("Number of pieces: " + pieceCount);
      System.out.println("Info hash: " + bytesToHex(infoHash));
      }
  }

    private static byte[] getEncodedInfo(byte[] torrentBytes) throws IOException {
        String marker = "4:info";
        int start = indexOf(torrentBytes, marker.getBytes(StandardCharsets.UTF_8));
        if (start == -1) {
            throw new IOException("Invalid torrent file: info section not found");
        }
        
        start = lastIndexOf(torrentBytes, (byte)'d', start);
        if (start == -1) {
            throw new IOException("Invalid torrent file: info dictionary not found");
        }
        
        int depth = 1;
        for (int i = start + 1; i < torrentBytes.length; i++) {
            if (torrentBytes[i] == 'd') {
                depth++;
            } else if (torrentBytes[i] == 'e') {
                depth--;
                if (depth == 0) {
                    return Arrays.copyOfRange(torrentBytes, start, i + 1);
                }
            }
        }
        throw new IOException("Invalid torrent file: unclosed info dictionary");
    }

    private static int indexOf(byte[] array, byte[] target) {
        for (int i = 0; i < array.length - target.length + 1; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private static int lastIndexOf(byte[] array, byte target, int before) {
        for (int i = before; i >= 0; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private static byte[] generatePeerID() {
        byte[] peerId = new byte[20];
        for (int i = 0; i < peerId.length; i++) {
            peerId[i] = (byte) ('A' + (int) (Math.random() * 26));
        }
        return peerId;
    }
}