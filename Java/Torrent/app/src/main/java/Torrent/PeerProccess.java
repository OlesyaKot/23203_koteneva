package Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface PeerProccess {
  boolean onConnected(); 
  boolean processing(ByteBuffer buff) throws IOException; 
}
