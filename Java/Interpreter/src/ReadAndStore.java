import java.io.IOException;
/**
 * The BrainFuck's command that reads char and stores ASCII under ptr
 */
public class ReadAndStore implements Command {
  /**
 * Reads char and stores ASCII under ptr in interpreter data array
 * 
 * @param context stores information about interpretation data
 * @throws IOException
 */
  @Override
  public void execute(Context context) throws IOException{
    if (context.getNumLoop() != 0) {
      context.addInTmpData(',');
    }
    context.readAndStore();
  }
}
