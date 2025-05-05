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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameoverScreen implements Screen {
  private GameContext context;
  private Scene scene;
  private boolean init = false;

  public GameoverScreen(GameContext context) {
    this.context = context;
  }

  Label scorehWords;

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
    finishWords.setStyle("-fx-font-size: 50px;" + 
                         "-fx-text-fill: black;" + 
                         "-fx-padding: 20px;" +
                         "-fx-font-weight: bold;");
    scorehWords = new Label("with score: " + context.getScore());
    scorehWords.setStyle( "-fx-font-size: 30px;" + 
                          "-fx-text-fill: black;" + 
                          "-fx-padding: 20px;");

    VBox menu = new VBox(50);
    menu.setAlignment(Pos.CENTER);
    menu.getChildren().addAll(finishWords, scorehWords, restart, exit);

    Image image = new Image(getClass().getResource("/images/gameover.png").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(false);

    StackPane stackPane = new StackPane();

    imageView.fitWidthProperty().bind(stackPane.widthProperty());
    imageView.fitHeightProperty().bind(stackPane.heightProperty());

    stackPane.getChildren().addAll(imageView, menu);
    stackPane.setAlignment(Pos.CENTER);

    scene = new Scene(stackPane, 500, 500);

    init = true;
  }
    
  @Override
  public void reset() {

  }

  @Override
  public void onShow() {
    scorehWords.setText("with score: " + context.getScore());
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
