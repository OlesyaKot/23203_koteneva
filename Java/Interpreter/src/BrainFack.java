import java.util.Scanner;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
 * BraiNFuck interpretator
 */
public class BrainFack {
  private static String inputData;
  private static final Logger logger = LogManager.getLogger(BrainFack.class);

  /** 
   * The main function for BrainFuck Interpretator.
   * 
   * @param args the path to the configuration file with BrainFuck's commands
   */
  public static void main(String[] args) {
    
    if (args.length != 1) {
      System.out.println("Not invalid number of arguments.");
      logger.info("Wrong path too the commands configuration file.");
      return;
    }

    logger.info("Interpretation starts successful!");

    try (Scanner console = new Scanner(System.in)) {
      Factory factory = Factory.getInstance();
      factory.configFactory(args[0]);

      inputData = console.nextLine();
      Interpreter interpret = new Interpreter();
      interpret.interpreter(inputData, factory);
    } catch (IOException e){ 
      System.out.println("Input/output problem " + e.getMessage());
    } catch (BadCommandException e) {
      System.out.println("Problem with classes/methods/parameters " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    logger.info("Interpretetion done");
  }
}
