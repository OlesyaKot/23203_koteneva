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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameoverScreen implements Screen {
  private GameContext context;
  private Scene scene;
  private boolean init = false;

  public GameoverScreen(GameContext context) {
    this.context = context;
  }

  @Override
  public void onCreate(ScreenSwitcher switcher) {
    Button restart = new Button("RESTART");
    Button exit = new Button("EXIT");

    List<Button> buttons = List.of(restart, exit);
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

    restart.setOnAction(e -> {
      switcher.restart();
      switcher.switchScreen(ScreenType.GAME); 
    });

    exit.setOnAction(e -> {
      switcher.restart();
      switcher.switchScreen(ScreenType.WELCOME); 
    });
        
    exit.setPrefSize(200, 80);
    restart.setPrefSize(200, 80);    

    Label finishWords = new Label("GAME OVER!!!");
    finishWords.setStyle(
      "-fx-font-size: 50px;" +          // Размер шрифта
      "-fx-text-fill: black;" +          // Цвет текста
      "-fx-padding: 20px;" +           // Внутренний отступ
      "-fx-font-weight: bold;"
    );

    VBox menu = new VBox(50);
    menu.setAlignment(Pos.CENTER);
    menu.getChildren().addAll(finishWords, restart, exit);
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
