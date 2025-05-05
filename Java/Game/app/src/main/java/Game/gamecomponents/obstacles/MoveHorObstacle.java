package Game.gamecomponents.obstacles;

import Game.gamecomponents.Obstacle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MoveHorObstacle implements Obstacle {
    private Rectangle moveBarrier;
    private double height = 100;
    private double width = 30;

    private Timeline animation;
    private double startX = 0.0;
    private double endX = 50.0;

    public MoveHorObstacle() {
        moveBarrier = new Rectangle(width, height);
        moveBarrier.setFill(Color.DARKORANGE);
        moveBarrier.setStroke(Color.BLACK);
        initAnimation();
    }

    private void initAnimation() {
        animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setAutoReverse(true);
    }
    private void startAnimation() {
        if (animation.getStatus() != Animation.Status.RUNNING) {
            animation.play();
        }
    }

    private void stopAnimation() {
        if (animation.getStatus() == Animation.Status.RUNNING) {
            animation.stop();
        }
    }

    @Override
    public void reset() {
        stopAnimation();
        moveBarrier.setTranslateX(0);
        moveBarrier.setTranslateY(0);
    }

    @Override
    public Node getObstacleView() {
        return moveBarrier;
    }

    @Override
    public double getHeight() {
        return height;
    }
    @Override
    public void setPosition(double X, double Y) {
        moveBarrier.setTranslateX(X);
        moveBarrier.setTranslateY(Y - height / 3);
        setupAnimation();
        startAnimation();
    }

    private void setupAnimation() {
        KeyValue kvStart = new KeyValue(moveBarrier.layoutXProperty(), startX);
        KeyValue kvEnd = new KeyValue(moveBarrier.layoutXProperty(), endX);
        KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvStart);
        KeyFrame kfEnd = new KeyFrame(Duration.seconds(1), kvEnd);

        animation.getKeyFrames().setAll(kfStart, kfEnd);
    }
}