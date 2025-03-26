import java.io.IOException;

/** 
 * Interface for BrainFuck's commands
 */
public interface Command {
  public void execute(Context context) throws IOException;
}
