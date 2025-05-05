package Game.screens;

import Game.GameContext;
import Game.Screen;
import Game.ScreenSwitcher;
import Game.ScreenType;

import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class WelcomeScreen implements Screen {
  private Scene scene;
  private boolean init = false;

  public WelcomeScreen(GameContext context) {
  }

  @Override
  public void onCreate(ScreenSwitcher switcher) {

    Button start = new Button("START GAME");
    Button exit = new Button("EXIT");
    Button score = new Button("LEADER BOARD");
    List<Button> buttons = List.of(start, score ,exit);
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

    score.setOnAction(e -> {
      switcher.switchScreen(ScreenType.LEADERBOARD);
    });

    exit.setOnAction(e -> {
      Platform.exit();                                    
    });
        
    start.setPrefSize(200, 80);
    score.setPrefSize(200, 80);
    exit.setPrefSize(200, 80);
    
    Label welcomeWords = new Label("WELCOME TO THE");
    Label gameName = new Label("'2D GAME'");

    welcomeWords.setStyle(
      "-fx-font-size: 40px;" +        
      "-fx-text-fill: black;" +         
      "-fx-padding: 20px;" +           
      "-fx-font-weight: bold;"
    );
    gameName.setStyle(
      "-fx-font-size: 50px;" +          
      "-fx-text-fill: black;" +          
      "-fx-padding: 20px;" +
      "-fx-font-weight: bold;"
    );
 
    VBox menu = new VBox(20, welcomeWords, gameName, start, score, exit);
    menu.setAlignment(Pos.CENTER);
    menu.setStyle("-fx-spacing: 30;");

    Image image = new Image(getClass().getResource("/images/background.jpg").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(false);

    StackPane stackPane = new StackPane();

    imageView.fitWidthProperty().bind(stackPane.widthProperty());
    imageView.fitHeightProperty().bind(stackPane.heightProperty());

    stackPane.getChildren().addAll(imageView, menu);
    stackPane.setAlignment(Pos.CENTER); 

    scene = new Scene(stackPane, 1800, 1000);
    init = true;
  }

  @Override
  public void onShow() {

  }

  @Override
  public void reset() {
      
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
