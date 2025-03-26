/**
 * The BrainFuck's command that decreases pointer
 */
public class Back implements Command {
/**
 * Decreases pointer in interpreter data array
 * 
 * @param context stores information about interpretation data
 */
  @Override
  public void execute(Context context) {
    context.back();

    if (context.getNumLoop() != 0) {
      context.addInTmpData('<');
    }
  }   
}