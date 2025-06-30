package Torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class SelectorServer {
  private int PORT;
  private Selector selector;
  private TorrentContext context;
  private DownloadManager downloadManager;
  private ExecutorService threadPool;
 
  public SelectorServer(TorrentContext context, DownloadManager downloadManager, ExecutorService threadPool, int port) throws IOException {
    this.context = context;
    this.downloadManager = downloadManager;
    this.threadPool = threadPool;
    this.PORT = port;
    selector = Selector.open();
    
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.bind(new InetSocketAddress("127.0.0.1", PORT));
    serverChannel.configureBlocking(false);
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  public void start() throws IOException {
    while (true) {
      selector.select();

      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> iter = selectedKeys.iterator();

      while (iter.hasNext()) {
        SelectionKey key = iter.next();

        if (key.isAcceptable()) {
          acceptConnection(key);
        } else if (key.isReadable()) {
          readDataFromPeer(key);
        }
        iter.remove();
      }
    }
  }

  private void acceptConnection(SelectionKey key) throws IOException{
    ServerSocketChannel srv = (ServerSocketChannel) key.channel();

    try{
      SocketChannel client = srv.accept();

      if (client == null) {
        System.out.println("Server: cannot accept connection");
        return;
      }

      client.configureBlocking(false);
      ServerPeerProccess session = new ServerPeerProccess(client, downloadManager);
      session.setChannel(client);
  
      client.register(selector, SelectionKey.OP_READ, session);
      System.out.println("Server: accepted new connection from " + client.getRemoteAddress());
      downloadManager.addConnectedPeer(client, session); 
      session.onConnected();    
    } catch (IOException e) {
      throw e;
    }
  }

  private void readDataFromPeer(SelectionKey key){
    SocketChannel client = (SocketChannel) key.channel();    
    ServerPeerProccess session = (ServerPeerProccess) key.attachment();

    ByteBuffer buffer = ByteBuffer.allocate((int)context.getPieceLength());
    try{
      int bytesRead = client.read(buffer); 

      if (bytesRead == -1) {
        System.out.println("Server: client disconnected 1");
        downloadManager.removeConnectedPeer(client);
        client.close();                    
        key.cancel();
        return;
      }

      buffer.flip();
      threadPool.submit(() -> {
        if (!session.processing(buffer)) {
          System.out.println("Server: client disconnected 2");
          downloadManager.removeConnectedPeer(client);

          try{
            client.close();                  
          } catch (IOException e) {
            e.printStackTrace();
          }
          key.cancel();
        }
      });

    } catch (IOException e) {
      try {
        downloadManager.removeConnectedPeer(client);
        key.cancel();
        client.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
   }
  }
}
