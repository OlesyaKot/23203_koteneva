package Game;

import javafx.scene.Scene;

public interface Screen {
  public void onCreate(ScreenSwitcher switcher);
  
  public void onShow();
  
  public void onHide();
  
  public void reset();
  
  public Scene getScene();
  
  public boolean isInit();
}
