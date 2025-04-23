package Game.managers;

import Game.GameContext;
import Game.Screen;
import Game.ScreenSwitcher;
import Game.ScreenType;
import org.reflections.Reflections;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import javafx.stage.Stage;

public class ScreenManager implements ScreenSwitcher {
  private final GameContext context = new GameContext();
  private final Stage stage;
  private final Map<String, Screen> screens = new HashMap<>();
  private Screen currentScreen;

  public ScreenManager(String screenPack, Stage stage) throws ManagerException {
    this.stage = stage;
    registerScreen(screenPack);
  }

  private void registerScreen(String screenPack) throws ManagerException { // java reflection работает только с .class файлами
    Reflections reflection = new Reflections(screenPack);
    Set<Class<? extends Screen>> screenClasses = reflection.getSubTypesOf(Screen.class);

    for (Class<? extends Screen> c : screenClasses) {
      if (!c.getSimpleName().endsWith("Screen"))
        continue;
      String screenName = c.getSimpleName()
        .replace("Screen", "")
        .toLowerCase();
      try {
        screens.put(screenName, c.getDeclaredConstructor(GameContext.class).newInstance(context));
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new ManagerException();
      }
    }
  }
    
  @Override
  public void switchScreen(ScreenType screen) {
    if (currentScreen != null) {
      currentScreen.onHide();
    }

    currentScreen = screens.get(screen.name().toLowerCase());

    if (!currentScreen.isInit()) {
      currentScreen.onCreate(this);
    }

    currentScreen.onShow();
    stage.setScene(currentScreen.getScene());
  }

  public void restart() {
    for (String s : screens.keySet()) {
      screens.get(s).reset();  
    }
  }
}




























       // getSubTypesOf(Class<T> type) принимает класс или интерфейс (Screen.class) и возвращает все классы, которые являются его подклассами или реализуют его.
