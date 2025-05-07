package Game.screens;

import Game.GameContext;
import Game.Screen;
import Game.ScreenSwitcher;
import Game.ScreenType;
import Game.gamecomponents.*;
import javafx.scene.Scene;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GameScreen implements Screen {
  private ScreenSwitcher switcher;
  private Scene scene;
  private boolean init = false;
  private GameContext context;
  private World world = new World();
  
  Player player;
  private Pane movingScene;
  private Pane gameWorld;

  public GameScreen(GameContext context) {
    this.context = context;
  }

  private double INITIALSPEED = 0.7;
  private double INITIALMOVE = 0.4;
  private double move = INITIALMOVE;
  private double speed = INITIALSPEED;
  private Text scoreText;

  @Override
  public void onCreate(ScreenSwitcher switcher) {
    this.switcher = switcher;
    player = new Player();
    movingScene = world.initWorld(player, move);

    gameWorld = new Pane();
    gameWorld.getChildren().addAll(movingScene, player.getPlayer());

    scoreText = new Text();
    scoreText.setLayoutX(50); 
    scoreText.setLayoutY(30);  
    scoreText.setStyle("-fx-font-size: 25px;" +
                       "-fx-font-weight: bold; " +
                       "-fx-text-fill: black; "); 
                       
    gameWorld.getChildren().add(scoreText);

    scene = new Scene(gameWorld, 500, 500);
    init = true;
  }
    
  void setInputKeys() {
    scene.setOnKeyPressed(e -> {
      if (player.getIsMoving())
        return;
      switch (e.getCode()) {
        case ESCAPE -> {
          switcher.switchScreen(ScreenType.PAUSE);   
        }
        case W, UP -> {
          if (player.getCanMoveUp()) {
            player.movePlayerY(-1);
            player.setCanMoveUp(false);
            player.setCanMoveDown(true);
          }
        }
        case S, DOWN -> {
          if (player.getCanMoveDown()) {
            player.movePlayerY(1);
            player.setCanMoveUp(true);
            player.setCanMoveDown(false);
          }
        }
        case SPACE -> {
          player.setCanMoveUp(false);
          player.setCanMoveDown(false);
          if (!player.getIsJump())
            player.jump(1);
        }
        case SHIFT -> {
          player.setCanMoveUp(false);
          player.setCanMoveDown(false);
          if (!player.getIsJump())
            player.jump(-1);
        }
        default -> { 
        }
      }
    });
  }

  private AnimationTimer gameLoop;
  private double savedSpeed = 0.0;
  private double savedMove = 0.0;

  @Override
  public void onShow() {
    if (savedSpeed != 0.0 && savedMove != 0.0) {
      speed = savedSpeed;
      move = savedMove;
    }

    gameLoop = new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (speed < 1.0) {
          speed += 0.00001;
          move += 0.00008;
        }
        context.addInFinalScore(0.01);
        scoreText.setText("Score: " + context.getScore());

        double offsetX = movingScene.getTranslateX() - move;
        movingScene.setTranslateX(offsetX);

        if (!world.update(offsetX, move)) {
          switcher.switchScreen(ScreenType.GAMEOVER);   
        }    
      }
    };
    gameLoop.start();
    setInputKeys();
  }
    
  @Override
  public void onHide() {
    savedSpeed = speed;
    savedMove = move;
    gameLoop.stop();
  }
    
  @Override
  public void reset() {
    gameLoop.stop();
    world.reset();
    move = INITIALMOVE;
    speed = INITIALSPEED;
    init = false;
    savedMove = 0.0;
    savedSpeed = 0.0;
    
    gameWorld.getChildren().clear();
    gameWorld = null;
  }

  @Override
  public Scene getScene() {
    return scene;
  }

  @Override
  public boolean isInit() {
    return init;
  }
}
