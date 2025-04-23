package Game.screens;

import java.util.List;

import Game.GameContext;
import Game.Screen;
import Game.ScreenSwitcher;
import Game.ScreenType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PauseScreen implements Screen {
    private GameContext context;
    private Scene scene;
    private boolean init = false;

    public PauseScreen(GameContext context) {
        this.context = context;
    }

  @Override
  public void onCreate(ScreenSwitcher switcher) {

    Button start = new Button("RESUME GAME");
    Button exit = new Button("EXIT GAME");

    List<Button> buttons = List.of(start, exit);

    buttons.forEach(button -> {
      ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
      scaleUp.setToX(1.1);
      scaleUp.setToY(1.1);

      ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
      scaleDown.setToX(1);
      scaleDown.setToY(1);

      button.setOnMouseEntered(e -> scaleUp.play());
      button.setOnMouseExited(e -> scaleDown.play());
    });

    start.setOnAction(e -> {
      switcher.switchScreen(ScreenType.GAME);
    });
    exit.setOnAction(e -> {
      switcher.restart();
      switcher.switchScreen(ScreenType.WELCOME);
    });

    start.setPrefSize(200, 80);
    exit.setPrefSize(200, 80);

    VBox menu = new VBox(20);
    menu.setAlignment(Pos.CENTER);
    menu.getChildren().addAll(start, exit);
    scene = new Scene(menu, 500, 500);

    init = true;
  }
    
  @Override
  public void reset() {
    
  }


  @Override
  public void onShow() {
        
  }

  @Override
  public void onHide() {

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
