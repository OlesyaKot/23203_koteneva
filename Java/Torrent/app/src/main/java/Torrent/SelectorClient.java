package Torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SelectorClient {

  public class Peer {
    private String host;
    private int port;

    public Peer(String host, int port) {
      this.host = host;
      this.port = port;
    }
    
    public String getHost() {
      return host;
    }

    public int getPort() {
      return port;
    }
  }

  private ExecutorService threadPool;
  private DownloadManager downloadManager;
  private TorrentContext context;
  private Selector selector;
  private SocketChannel clientChannel;
  private ClientPeerProccess session;
  private final long RETRY_TO_CONNECT = 5000;
  private List<Peer> syncList = Collections.synchronizedList(new ArrayList<>());
  
  public SelectorClient(List<String> peers, ExecutorService threadPool, DownloadManager downloadManager, TorrentContext context) throws IOException{
    this.threadPool = threadPool;
    this.downloadManager = downloadManager;
    this.context = context;

    for (String peer : peers) {
      String[] parts = peer.split(":", 2); 
      if (parts.length != 2) {
        System.out.println("Wrong format of peer's address: " + peer);
        continue;
      }
      syncList.add(new Peer(parts[0], Integer.parseInt(parts[1])));
    }

    selector = Selector.open(); // TODO: throws IOException
  }
  
  private boolean isDisconnect = false;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition disconnectCondition = lock.newCondition();

  public void start() throws IOException {
    new Thread(() -> {

      lock.lock();
      while (true) {
        for (int i = 0; i < syncList.size(); ) {
          try {
            if (tryToConnect(syncList.get(i).getHost(), syncList.get(i).getPort())) {
              if (!session.onConnected()) {
                // хз надо ли что-то делать тут, переподключаться???
              }
              syncList.remove(syncList.get(i));
            } else {
              ++i;
            }
          } catch (IOException e) {

          }
        }
        
        lock.unlock();
        try{
          Thread.sleep(RETRY_TO_CONNECT);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return;
        }

        lock.lock();
        if (syncList.isEmpty()) {
          try {
            isDisconnect = false;
            while (!isDisconnect) {
              disconnectCondition.await();
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
            return;
          }
        }
      }
    }).start();

    listen();
  }

  private boolean tryToConnect(String host, int port) throws IOException {
    try {
      clientChannel = SocketChannel.open();
      clientChannel.configureBlocking(false);
      boolean connected = clientChannel.connect(new InetSocketAddress(host, port));

      if (!connected) {
        System.out.println("Client: connecting to " + host + ":" + port + "...");
        if (clientChannel.finishConnect()) {
          System.out.println("Client: connected to peer: " + host + ":" + port);
          session = new ClientPeerProccess(clientChannel, downloadManager, host, port);
          clientChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ, session);
          selector.wakeup();
          return true;
        }
      } else {
        System.out.println("Client: connected immediately to " + host + ":" + port);
        clientChannel.finishConnect();
        session = new ClientPeerProccess(clientChannel, downloadManager, host, port);
        clientChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ, session);
        selector.wakeup();
        return true;
      }
    } catch (IOException e) {
      System.out.println("Client: can't connect to " + host + ":" + port + ". Retrying in 5 sec...");
      return false;
    }
    return false;
  }
  
  private int i = 0;

  private void listen() throws IOException{
    while (true) {
      int numKeys = selector.select();
      if (numKeys == 0)
        continue;

      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> iter = selectedKeys.iterator();
      while (iter.hasNext()) {
        SelectionKey key = iter.next();

        if (key.isReadable()) {
          i++;
          System.out.println("CLIENT: get readble messsage " + i);
          readFromServer(key);
          System.out.println("CLIENT: finish read messsage " + i);
        }
        iter.remove();
      }
    }
  }

  private void readFromServer(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ClientPeerProccess session = (ClientPeerProccess) key.attachment();
    ByteBuffer buffer = ByteBuffer.allocate(context.getPieceLength() + 13); // фиксированный размер 13 - это под заголовок
    // System.out.println("Client: size message:" + buffer.remaining() + " len context.getPieceLength()" + context.getPieceLength() );

    try {
      int bytesRead = clientChannel.read(buffer);
      // System.out.println(" bytesRead " + bytesRead);
      if (bytesRead == -1) {
        System.out.println("Client: server close the connection");
        key.cancel();        
        clientChannel.close();

        lock.lock();
        try {
          isDisconnect = true;
          syncList.add(new Peer(session.getHost(), session.getPort()));
          disconnectCondition.signalAll(); // будим все потоки, которые ждут
        } finally {
          lock.unlock();
        } 
        return;
      }

      buffer.flip(); // Готовим буфер к чтению
      threadPool.submit(() -> {

        try {
        if (!session.processing(buffer)) {
          System.out.println("Client: server disconnected 2");
          try{
            key.cancel();
            clientChannel.close();
    
            lock.lock();
            try {
              isDisconnect = true;
              syncList.add(new Peer(session.getHost(), session.getPort()));
              disconnectCondition.signalAll(); // будим все потоки, которые ждут
            } finally {
              lock.unlock();
            } 
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        } 
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
