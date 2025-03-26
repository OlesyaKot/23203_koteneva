/**
 * The BrainFuck command, which is the loop start flag under pointer 
 */
public class StartLoop implements Command {
/**
 * Sets startLoopFlag.
 * 
 * @param context stores information about interpretation data
 */
  @Override
  public void execute(Context context) {
    context.addInStartPtr();
    context.addInTmpData('[');
    context.addNumLoop();
  }
}
