/**
 * The BrainFuck's command that increases pointer 
 */
public class Next implements Command {
/**
 * Increases pointer in interpreter data array
 * 
 * @param context stores information about interpretation data
 */
  @Override
  public void execute(Context context) {
    context.next();
    
    if (context.getNumLoop() != 0) {
      context.addInTmpData('>');
    }
  }   
}