package Game.gamecomponents;

import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class Collision {
  private Player player;
  private Node playerNode;

  public Collision(Player player){
    this.player = player;
    playerNode = player.getPlayer();
  }

  private double offsetX = 0.0;

  public boolean checkCollision(Chunk chunk, double offsetX){
    List<Obstacle> currObstacle = chunk.getObstacles();
    this.offsetX = offsetX;
    boolean isCollision = false;
    for (Obstacle o : currObstacle) {
      Node oView = o.getObstacleView();
      if (oView instanceof Rectangle rectangle){
        isCollision = rectangleCollision(rectangle);
      }
      if (oView instanceof Ellipse ellipse) {
        isCollision = ellipseCollision(ellipse);
      }
      if (oView instanceof Group group) {
        isCollision = groupCollision(group);
      }
      if (isCollision) {
        return true;
      }
    }
    return false;
  }

  private boolean ellipseCollision(Ellipse ellipse) {
    double pX = playerNode.getTranslateX() - offsetX;
    double pY = playerNode.getTranslateY();

    double centerX = ellipse.getTranslateX() + ellipse.getCenterX();
    double centerY = ellipse.getTranslateY() + ellipse.getCenterY();
    double radiusX = ellipse.getRadiusX();
    double radiusY = ellipse.getRadiusY();

    double closestX = clamp(pX, pX + player.getPlayerWidth(), centerX);
    double closestY = clamp(pY, pY + player.getPlayerHeight(), centerY);

    double dx = closestX - centerX;
    double dy = closestY - centerY;
    return (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1.0;
  }

  private double clamp(double min, double max, double value) {
    return Math.max(min, Math.min(max, value));
  }
  
  private boolean rectangleCollision(Rectangle rectangle) {
    double oX = rectangle.getTranslateX();
    double oY = rectangle.getTranslateY();

    double pX = playerNode.getTranslateX() - offsetX;
    double pY = playerNode.getTranslateY();

    return pX < oX + rectangle.getWidth() && 
           pX + player.getPlayerWidth() > oX &&
           pY < oY + rectangle.getHeight() &&
           pY + player.getPlayerHeight() > oY;   
  }
  
  private boolean groupCollision(Group group) { 
    double pX = playerNode.getTranslateX();
    double pY = playerNode.getTranslateY();

    for (Node part : group.getChildren()) {
      if (part instanceof Rectangle rect) {
        Bounds rectBounds = rect.localToScene(rect.getBoundsInLocal());
        double rectX = rectBounds.getMinX();
        double rectY = rectBounds.getMinY();
        double rectWidth = rectBounds.getWidth();
        double rectHeight = rectBounds.getHeight();

        if (pX < rectX + rectWidth && pX + player.getPlayerWidth() > rectX &&
            pY < rectY + rectHeight && pY + player.getPlayerHeight() > rectY) {
          return true;
        }
      }

      if (part instanceof Ellipse ellipse) {
        Point2D ellipseBounds = ellipse.localToScene(ellipse.getCenterX(), ellipse.getCenterY());
        double centerX = ellipseBounds.getX();
        double centerY = ellipseBounds.getY();
        double radiusX = ellipse.getRadiusX();
        double radiusY = ellipse.getRadiusY();

        double closestX = clamp(pX, pX + player.getPlayerWidth(), centerX);
        double closestY = clamp(pY, pY + player.getPlayerHeight(), centerY);

        double dx = closestX - centerX;
        double dy = closestY - centerY;
        if ((dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1.0) {
          return true;
        }
      }
    }
    return false;
  }
}
