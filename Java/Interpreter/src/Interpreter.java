import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
class BadCommandException extends RuntimeException {
}

/**
 * Transforms data at the top level. Calls BrainFuck commands. 
 */
public class Interpreter {

  private Context context = new Context();
  private static final Logger logger = LogManager.getLogger(Interpreter.class);

  /**
 * Interprets input data 
 * @param inputData
 * @param factory
 * @throws BadCommandException
 * @throws IOException
 */
  public void interpreter(String inputData, Factory factory)  throws BadCommandException, IOException {
    context.setData(inputData, factory);
    loopInterpret(factory);
    context.end();
  }
    
  /**
 * A loop that calls BrainFuck commands to transform the data.
 * @param factory
 * @throws BadCommandException
 * @throws IOException
 */    
  void loopInterpret(Factory factory) throws BadCommandException, IOException {
    ArrayList<Character> data = new ArrayList<>();
    data = context.getData();
    for (int i = 0; i < data.size(); ++i) {
      Object neededCommand = factory.getCommand(String.valueOf(data.get(i)));
      Command command = (Command) neededCommand;
      command.execute(context);
      logger.trace("Command {} done successful", command);
    } 
  }
}

