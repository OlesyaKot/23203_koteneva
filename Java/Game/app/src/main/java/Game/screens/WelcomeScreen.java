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
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class WelcomeScreen implements Screen {
    private GameContext context;
    private Scene scene;
    private boolean init = false;

    public WelcomeScreen(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCreate(ScreenSwitcher switcher) {

        Button start = new Button("START GAME");
        Button exit = new Button("EXIT");
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
            Platform.exit();                                         // TODO: add saving current result (use Alert, json)
        });
        
        start.setPrefSize(200, 80);
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

      VBox menu = new VBox(20);
      menu.setAlignment(Pos.CENTER);
      
      menu.getChildren().addAll(welcomeWords, gameName, start, exit);
      menu.setStyle("-fx-background-color:rgb(198, 238, 203);");
      scene = new Scene(menu, 1800, 1000);
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
