package Game;

import Game.managers.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private ScreenManager screenManager;

  @Override
  public void start(Stage stage) {
    try{
      screenManager = new ScreenManager("Game.screens", stage);
    } catch (ManagerException e) {
      System.out.println("ScreenManager exception " + e.getMessage());
      return;
    }
    screenManager.switchScreen(ScreenType.WELCOME);
    stage.setTitle("Subway Surf 2D");
    stage.show();
  }

  
  public static void main(String[] args) {
    launch(args);
  }
}
