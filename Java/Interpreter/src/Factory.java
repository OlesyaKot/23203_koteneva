import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generates objects by the requested name, 
 * if information about this class is available in the configuration file.
 */
public class Factory {
  private static class Singleton {
    private static final Factory INSTANCE = new Factory();
  }

  private Factory() {}

  public static Factory getInstance() {
    return Singleton.INSTANCE;
  }

  private Map<String, Class<?>> commandMap = new HashMap<>();
  private Properties properties = new Properties(); 
  private static final Logger logger = LogManager.getLogger(Factory.class);

  /**
   * Passes information about the paths to the BrainFuck commands to the factory 
   * 
   * @param path path to configuration file
   * @throws IOException
   */
  public void configFactory(String path) throws IOException {
    ClassLoader classLoader = Factory.class.getClassLoader();
  
    try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
      properties.load(inputStream);
    } catch (IOException | NullPointerException e) {
     logger.trace("Problem with factory commands configuration file.");
     throw new IllegalArgumentException("Problem with factory commands configuration file.");
    }
    logger.trace("Factory created successful");
  }
  
  /**
   * Returns a BrainFuck command object corresponding to the externally requested string. 
   * If the factory does not have information about the requested command, it throws an exception.
   * 
   * @param s requested BrainFuck command as a String
   * @return BrainFuck command object 
   * @throws BadCommandException
   */
  public Object getCommand(String s) throws BadCommandException {
    Object newObject;
    try {
      if (!commandMap.containsKey(s)) {
        String neededCommand = properties.getProperty(s);
        if (neededCommand == null) {
          throw new IllegalArgumentException("Wrong input symbol " + s + ". If is not from BrainFuck.");
        }
  
        Class<?> newCommand = Class.forName(neededCommand);
        commandMap.put(s, newCommand);
      }
      
      newObject = commandMap.get(s).getConstructor().newInstance();
    } catch (ClassNotFoundException | NoSuchMethodException |
        InstantiationException | IllegalAccessException | InvocationTargetException e) {
        logger.trace("Problem with BrainFuck's commands classes.");
        throw new BadCommandException();
    }
  
    return newObject;
  }
}
