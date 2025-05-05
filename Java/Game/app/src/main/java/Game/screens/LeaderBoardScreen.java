package Game.screens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import Game.GameContext;
import Game.Screen;
import Game.ScreenSwitcher;
import Game.ScreenType;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LeaderBoardScreen implements Screen {
  private GameContext context;
  private Scene scene;
  private boolean init = false;
  private ObservableList<Double> scores = FXCollections.observableArrayList(); // ???
  private ObservableList<Label> scoresLabel = FXCollections.observableArrayList(); // ???
  private int MAX_NUM_SCORE = 10;

  public LeaderBoardScreen(GameContext context) {
    this.context = context;
    loadScores();
  }
  
  VBox table = new VBox(10);
  VBox root = new VBox();

  @Override
  public void onCreate(ScreenSwitcher switcher) {
    Button back = new Button("BACK");  
    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), back);
    scaleUp.setToX(1.1);
    scaleUp.setToY(1.1);

    back.setOnAction(e -> {
      switcher.switchScreen(ScreenType.WELCOME);
    });
    back.setPrefSize(200, 80);

    Label titleLabel = new Label("Leader score:");
    titleLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
  
    updateTableOnScene();

    VBox.setVgrow(table, Priority.ALWAYS);

    root.getChildren().addAll(titleLabel, table, back);

    root.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);

    Image image = new Image(getClass().getResource("/images/fortable.jpg").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(false);

    StackPane stackPane = new StackPane();

    imageView.fitWidthProperty().bind(stackPane.widthProperty());
    imageView.fitHeightProperty().bind(stackPane.heightProperty());

    stackPane.getChildren().addAll(imageView, root);
    stackPane.setAlignment(Pos.CENTER);

    scene = new Scene(stackPane, 500, 500);

    init = true;
  }

  @Override
  public void onShow() {

  }
  
  private void updateTable() {
    if (context.getScore() == 0)
      return;
    if (scores.size() == 0) {
      scores.add(context.getScore());
    }
    
    for (Double num : scores) {
      if (context.getScore() != num) {
       double minScore = Collections.min(scores);
        if (scores.size() < MAX_NUM_SCORE) {
          scores.add(context.getScore());
        } else {
          if (context.getScore() > minScore) {
            scores.remove(Double.valueOf(minScore));
            scores.add(context.getScore());
          }
        }
      }
      break;
    }
    scores.sort(Comparator.reverseOrder());
    saveScores();
  }
  
  private void loadScores() {
    File file = new File("scores.txt");

    if (!file.exists()) {
      try (InputStream inputStream = LeaderBoardScreen.class.getClassLoader().getResourceAsStream("scores.txt")) {
        if (inputStream == null) {
          return;
        }
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new IllegalAccessError(e.getMessage()); // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!
      }
    }
    
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String number = scanner.nextLine().trim();
        if (!number.isEmpty()) {
          double score = Double.parseDouble(number); 
          scores.add(score);
        }
      }
    } catch (FileNotFoundException e) {
      throw new IllegalAccessError(e.getMessage());
    }
  }
  
  private void saveScores() {
    try (FileWriter writer = new FileWriter("scores.txt")) {
      for (Double score : scores) {
        writer.write(score + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateTableOnScene() {
    table.getChildren().removeAll(scoresLabel); 
    scoresLabel.clear(); 

    int rank = 1;
    for (Double score : scores) {
      Label label = new Label(rank + " - " + score);
      label.setStyle(" -fx-font-size: 25px;");
      if (rank <= 3) {
        label.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: rgb(177, 91, 20);");
      }
      table.getChildren().add(label);
      scoresLabel.add(label);
      rank++;
    }
  }

  @Override
  public void onHide() {

  }
  
  @Override
  public void reset() {
    updateTable();
    updateTableOnScene();
    context.setFinalScore(0);
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
