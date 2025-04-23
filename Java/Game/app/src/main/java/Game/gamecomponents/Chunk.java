package Game.gamecomponents;

import java.util.ArrayList;
import java.util.List;

import Game.managers.ObstacleManager;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Chunk {
  private final int NUM_LINES = 3;
  private ObstacleManager manager;
  private final double roadHeight = 720;
  private final double lineHeight = roadHeight / 3;
  private double[] linesCenterY = new double[NUM_LINES];
  private double chunkNumber = 0;

  private double startX = 0;
  private double endX = 0;

  private Group chunkGroup = new Group();
  private Group roadMarkingsGroup = new Group();

  public Chunk(ObstacleManager manager, double cnt, Pane movingScene, int shift) {
    this.chunkNumber = cnt;
    this.manager = manager;
    createBackGround(movingScene, shift);
    createRoadMarkings(movingScene.getPrefHeight());
    movingScene.getChildren().add(chunkGroup);
    createObstacles(movingScene);
  }

  Rectangle background;
  Rectangle road;
  Rectangle leftShoulder;
  Rectangle rightShoulder;

  public void createBackGround(Pane movingScene, int shift) {
    this.startX = movingScene.getPrefWidth() * chunkNumber;
    this.endX = startX + movingScene.getPrefWidth() + shift;

    background = new Rectangle(startX, 0, endX - startX, movingScene.getPrefHeight());
    background.setFill(Color.SKYBLUE);

    double roadY = (movingScene.getPrefHeight() - roadHeight) / 2; // set the road in the middle of the scene
    road = new Rectangle(startX - 1, roadY, endX - startX, roadHeight);
    road.setFill(Color.GRAY);

    for (int j = 0; j < NUM_LINES; j++) {
        linesCenterY[j] = roadY + (j * lineHeight) + lineHeight / 2;
    }

    leftShoulder = new Rectangle(startX, roadY - 30, endX - startX, 30); //grass
    rightShoulder = new Rectangle(startX, roadY + roadHeight, endX - startX, 30);
    leftShoulder.setFill(Color.GREEN);
    rightShoulder.setFill(Color.GREEN);
    chunkGroup.getChildren().addAll(background, road, leftShoulder, rightShoulder, roadMarkingsGroup);
  }
    
  private void createRoadMarkings(double sceneHeight) {
    double roadY = (sceneHeight - roadHeight) / 2;
    int num_line = 40;

    for (int i = 0; i < num_line; i++) {
      for (int j = 1; j < NUM_LINES; j++) {
        Rectangle line = new Rectangle(startX + i * 50, roadY + j * lineHeight, 30, 5);
        line.setFill(Color.WHITE);
        roadMarkingsGroup.getChildren().add(line);
      }
    }
  }

  private List<Obstacle> currObstacle = new ArrayList<>();
    
  public void createObstacles(Pane movingScene) {
    int obstacleCount = 4 + (int) (Math.random() * 4);
    for (int i = 0; i < obstacleCount; ++i) {
      Obstacle obstacle = manager.createRandObstacle(linesCenterY, startX, endX);
      movingScene.getChildren().add(obstacle.getObstacleView());
      currObstacle.add(obstacle);
    } 
  }
    
  public double getTranslateX(Pane movingScene) {
    return movingScene.getPrefWidth() * (chunkNumber + 1);
  }
    
  public List<Obstacle> getObstacles() {
    return currObstacle;
  }
  
  public void removeFromMovingScene(Pane movingScene) {
    // movingScene.getChildren().remove(chunkGroup);
    // currObstacle.clear();
  }
}


// Chunk - сегмент мира 
// Каждый чанк содержит:
//     Препятствия (Obstacle).
//     Декорации (фоны, здания).
//     Методы для обновления и отрисовки.

