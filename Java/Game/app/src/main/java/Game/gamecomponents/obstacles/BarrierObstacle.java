package Game.gamecomponents.obstacles;

import Game.gamecomponents.Obstacle;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BarrierObstacle implements Obstacle {
    private Rectangle barrier;
    private double height = 120;
    private double width = 30;

    public BarrierObstacle(){
      barrier = new Rectangle(width, height);
      barrier.setFill(Color.BLANCHEDALMOND);
      barrier.setStroke(Color.BLACK);  
    }

    @Override
    public void reset() {
        barrier.setTranslateX(0);
        barrier.setTranslateY(0);        
    }

    @Override 
    public Node getObstacleView() {
        return barrier;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override 
    public void setPosition(double X, double Y) {
        barrier.setTranslateX(X);
        barrier.setTranslateY(Y - height / 4);
    }
}
