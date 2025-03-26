/**
 * The BrainFuck's command that outputs ASCII code under pointer
 */
public class OutputBytes implements Command {
/**
 * Outputs ASCII code under pointer in interpreter data array
 * 
 * @param context stores information about interpretation data
 */
  @Override
  public void execute(Context context) {
    context.outputBytes();
  }   
}
