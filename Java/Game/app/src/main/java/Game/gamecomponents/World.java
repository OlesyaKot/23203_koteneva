package Game.gamecomponents;

import java.util.LinkedList;

import Game.managers.ObstacleManager;
import javafx.scene.layout.Pane;

public class World {
  private final ObstacleManager manager = new ObstacleManager();
  private final LinkedList<Chunk> activeChunks = new LinkedList<>();
  private final int NUMOFACTIVECHUNK = 3;
  private double cnt = 0;
  private Pane movingScene/* = new Pane()*/;
  private Collision collision;

  public Pane initWorld(Player player) {
    movingScene = new Pane();

    collision = new Collision(player);
    movingScene.setTranslateX(0);
    movingScene.setTranslateY(0);
    movingScene.setPrefSize(1900, 1020);
    for (int i = 0; i < NUMOFACTIVECHUNK; ++i) {
      activeChunks.add(new Chunk(manager, cnt, movingScene, 2));
      cnt++;
    }
    return movingScene;
  }

  public boolean update(double offsetX) {
    for (Chunk chunk : activeChunks) {
      if (collision.checkCollision(chunk, offsetX)) {
        return false;
      }
    }

    Chunk firstChunk = activeChunks.get(0);
    if (-movingScene.getTranslateX() > firstChunk.getTranslateX(movingScene)) {
      firstChunk.removeFromMovingScene(movingScene);
      activeChunks.remove(0);
      activeChunks.add(new Chunk(manager, cnt, movingScene, 1));
      cnt++;
    }
    return true;
  }

  public void reset() {
    for (Chunk c : activeChunks) {
      c.removeFromMovingScene(movingScene);
    }
    activeChunks.clear();
    movingScene = null;
    collision = null;
    cnt = 0;
  }
}
