import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The BrainFuck's command that indicates end of the loop.
 */
public class EndLoop implements Command {
  private static final Logger logger = LogManager.getLogger(BrainFack.class);
   
  /**
     * Calls a function to execute the loop.
     * 
     * @param context stores information about interpretation data
     * @throws BadCommandException
     * @throws IOException
     */
  @Override
  public void execute(Context context) throws BadCommandException, IOException {
    if (context.getTmpDataSize() == 0) {
      logger.trace("Wrong input - too many symbols ].");
      throw new IllegalArgumentException("Wrong input - too many symbols ].");
    }
        
    if(context.getStartPtrSize() != context.getEndPtrSize()){
      context.addInEndPtr(context.getTmpDataSize());
      context.addInTmpData(']');
    }
 
    int currLoopIdx = context.getNumLoop();
    context.setNumLoop(0);

    try{
      loopInterpretData(currLoopIdx, context);
    } catch (BadCommandException | IOException e){
      throw e;
    }
     
    if (context.getStartPtrSize() != context.getEndPtrSize()) {
      context.setNumLoop(currLoopIdx - 1);
    }

    if (currLoopIdx == 1) {
      context.clearLoopArrays();
      context.setNumLoop(0);
    }
  }
  /**
     * This method transforms the interpreted data array according to the loop.
     * 
     * @param currLoopIdx 
     * @param context stores information about interpretation data
     * @throws BadCommandException
     * @throws IOException
     */
  public void loopInterpretData(int currLoopIdx, Context context) throws BadCommandException, IOException {
    int currStartPos = context.getElemOfStartPtr(currLoopIdx - 1);

    Factory factory = context.getFactory();
    while (context.getCurrElemOfInterpretData() != 0) {
      for (int i = currStartPos + 1; i != context.getElemOfEndPtr((context.getStartPtrSize() - currLoopIdx)); ++i) {
        try {
          Object neededCommand = factory.getCommand(String.valueOf(context.getElemOfTmpData(i)));
          Command command = (Command) neededCommand;

          if (command.getClass() == EndLoop.class) {
            continue;
          }

          if (command.getClass() == StartLoop.class) {
            currLoopIdx++;
            loopInterpretData(currLoopIdx, context);
            i = context.getElemOfEndPtr(context.getEndPtrSize() - currLoopIdx);
            currLoopIdx--;
            continue;
          }

          command.execute(context);
        } catch (IllegalArgumentException | BadCommandException | IOException e) {
          throw e;
        }
      }
    }
  }
}
