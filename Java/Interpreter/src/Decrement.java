/**
 * The BrainFuck's command that decrases element under pointer
 */
public class Decrement implements Command {
/**
 * Decrases element under pointer in interpreter data array
 * 
 * @param context stores information about interpretation data
 */
  @Override
  public void execute(Context context) {
    context.decrement();
    if (context.getNumLoop() != 0) {
      context.addInTmpData('-');
    }
  }
}
