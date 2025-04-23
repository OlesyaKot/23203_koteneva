package Game.gamecomponents.obstacles;

import Game.gamecomponents.Obstacle;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class PitObstacle implements Obstacle {
    private Ellipse pit;
    private double radiusY = 40;
    private double radiusX = 35;

    public PitObstacle() {
        pit = new Ellipse(radiusX, radiusY);

        pit.setFill(Color.DARKGRAY);
        pit.setStroke(Color.BLACK);
        pit.setStrokeWidth(1);
    }
    
    @Override
    public void reset() {
        pit.setCenterX(0);
        pit.setCenterY(0);   
    }
    
    @Override
    public Node getObstacleView() {
        return pit;
    }

    @Override
    public double getHeight() {
        return radiusY;
    }

    @Override 
    public void setPosition(double X, double Y) {
        pit.setTranslateX(X);
        pit.setTranslateY(Y + 2 * radiusY / 3);
    }
}
