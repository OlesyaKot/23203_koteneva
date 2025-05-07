package Game.gamecomponents;

import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Player {
  private boolean isMoving = false;
  private boolean canMoveUp = true;
  private boolean canMoveDown = true;
  private boolean isJumping = false;
  private final double jumpHeight = 90;
  private final double destY = 250;
  private double startPlayerX = 700;
  private double startPlayerY = 490;
  private Rectangle player;

  public Player() {
    player = new Rectangle(25, 25, javafx.scene.paint.Color.BLUE);
    player.setTranslateX(startPlayerX);
    player.setTranslateY(startPlayerY);
  }

  private final Duration moveDuration = Duration.millis(280);

  public void movePlayerY(int idx) {
    isMoving = true;

    TranslateTransition transition = new TranslateTransition(moveDuration, player);
    transition.setToY(player.getTranslateY() + destY * idx);
    transition.setOnFinished(e -> {
      if (Math.abs(player.getTranslateY() - startPlayerY) < 0.1) {
        setCanMoveUp(true);
        setCanMoveDown(true);
      }
      isMoving = false;
    });
    transition.play();
  }

  private Duration jumpDuration = Duration.millis(230);

  public void jump(int idx) {
    isJumping = true;
    final double translateY = player.getTranslateY();

    TranslateTransition jumpUp = new TranslateTransition(jumpDuration, player);
    jumpUp.setToY(translateY - jumpHeight * idx);

    TranslateTransition fallDown = new TranslateTransition(jumpDuration, player);
    fallDown.setToY(translateY);

    jumpUp.setOnFinished(e -> {
      fallDown.play();
    });

    fallDown.setOnFinished(e -> {
      canMoveUp = translateY >= startPlayerY;
      canMoveDown = translateY <= startPlayerY;
      isJumping = false;
    });
    jumpUp.play();
  }

  public Rectangle getPlayer() {
    return player;
  }

  public boolean getCanMoveUp() {
    return canMoveUp;
  }

  public boolean getCanMoveDown() {
    return canMoveDown;
  }

  public boolean getIsJump() {
    return isJumping;
  }

  public boolean getIsMoving() {
    return isMoving;
  }

  public double getPlayerWidth() {
    return player.getWidth();
  }

  public double getPlayerHeight() {
    return player.getHeight();
  }

  public void setCanMoveUp(boolean flag) {
    canMoveUp = flag;
  }

  public void setCanMoveDown(boolean flag) {
    canMoveDown = flag;
  }

  // public void changeJumpSpeed() {
  //   jumpDuration = Duration.millis(300);
  // }
}
