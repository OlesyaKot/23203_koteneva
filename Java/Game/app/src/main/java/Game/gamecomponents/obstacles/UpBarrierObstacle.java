package Game.gamecomponents.obstacles;

import Game.gamecomponents.Obstacle;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UpBarrierObstacle implements Obstacle {
  private Rectangle upBarrier;
  private double height = 120;
  private double width = 30;

  public UpBarrierObstacle(){
    upBarrier = new Rectangle(width, height);
    upBarrier.setFill(Color.rgb(134, 38, 91, 0.97));
    upBarrier.setStroke(Color.BLACK);  
  }

  @Override
  public void reset() {
    upBarrier.setTranslateX(0);
    upBarrier.setTranslateY(0);        
  }

  @Override 
  public Node getObstacleView() {
    return upBarrier;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override 
  public void setPosition(double X, double Y) {
    upBarrier.setTranslateX(X);
    upBarrier.setTranslateY(Y - height + height / 5);
  }
}
