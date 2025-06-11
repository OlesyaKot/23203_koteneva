package Torrent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

  private static List<String> peers = new ArrayList<>();
  private static int serverPort;
  private static TorrentContext context = new TorrentContext();
  private static DownloadManager downloadManager;

  public static void main(String[] args) throws IOException, Exception, NoSuchAlgorithmException{
    if (args.length < 3) { 
      System.out.println("Not enough arguments");
      return;
    }  
    
    serverPort = Integer.parseInt(args[1]);

    for (int i = 2; i < args.length; ++i) {
      peers.add(args[i]);
    }
    
    Parser.parse(context, args[0]);
      
    downloadManager = new DownloadManager(context);

    ExecutorService threadPool = Executors.newFixedThreadPool(4); // 4 рабочих потока

    // Запускаем сервер в отдельном потоке
    SelectorServer server = new SelectorServer(context, downloadManager, threadPool, serverPort);
    new Thread(() -> {
      try {
        server.start();
      } catch (IOException e) {
        System.out.println("Server stopped with error: " + e.getMessage());
      }
    }).start();
    // Ждём немного, чтобы сервер успел запуститься
    Thread.sleep(200);

    SelectorClient client = new SelectorClient(peers, threadPool, downloadManager, context);
    client.start();

    threadPool.shutdown();
  }
}
