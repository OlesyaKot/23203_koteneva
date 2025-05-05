package Game.gamecomponents.obstacles;

import Game.gamecomponents.Obstacle;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class BrushObstacle implements Obstacle {
  private Group tree;
  private Ellipse leaf;
  private Rectangle trunk;
  private double height = 50;
  private double width = 30;

  public BrushObstacle() {
    trunk = new Rectangle(-width / 2, height, width, height * 2);
    trunk.setFill(Color.BROWN);
    trunk.setStroke(Color.BLACK);

    leaf = new Ellipse(0, height - trunk.getHeight() / 3, width + 15, height + 12);
    leaf.setFill(Color.GREENYELLOW);
    leaf.setStroke(Color.BLACK);

    tree = new Group();
    tree.getChildren().addAll(trunk, leaf);
    }

    @Override
    public void reset() {
      tree.setTranslateX(0);
      tree.setTranslateY(0);        
    }

    @Override 
    public Node getObstacleView() {
      return tree;
    }

    @Override
    public double getHeight() {
      return height;
    }

    @Override 
    public void setPosition(double X, double Y) {
      tree.setTranslateX(X);
      tree.setTranslateY(Y - height);
    }
}
