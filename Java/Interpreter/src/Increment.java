/**
 * The BrainFuck's command that increases element under pointer   
 */
public class Increment implements Command {
/**
  * Increases element under pointer in interpreter data array
  * 
  * @param context stores information about interpretation data
  */
    @Override
    public void execute(Context context) {
      context.increment();
      if (context.getNumLoop() != 0) {
        context.addInTmpData('+');
      }
    }
}
