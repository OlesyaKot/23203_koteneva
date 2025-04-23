package Game.gamecomponents;

import javafx.scene.Node;

public interface Obstacle {

    void reset();

    Node getObstacleView();

    double getHeight();

    void setPosition(double X, double Y);
}
