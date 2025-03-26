import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains all information about interpret data 
 * and does all internal semantic operations of BrainFuck
 */
public class Context {
  private int currPtr = 0;
  private int numLoop = 0;
  private ArrayList<Character> data = new ArrayList<>();
  private ArrayList<Integer> interpretData = new ArrayList<>();
  private Factory thisFactory;
  private static final Logger logger = LogManager.getLogger(Context.class);

  public void setData(String inputData, Factory factory) {
    for (char c : inputData.toCharArray()) {
      data.add(c);
    }
    thisFactory = factory;
    logger.trace("Input data was accepted for processing successful.");
  }
  
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  public void readAndStore() throws IOException {
    System.out.println();
    System.out.print(">");
    try {
      defaultFillArray();
      int inputUserSymbol = reader.readLine().charAt(0);
      interpretData.set(currPtr, inputUserSymbol);
    } catch (IOException e) {
      throw e;
    }
    logger.trace("User symbol was successful load with command ReadAndStore.");
  }

  public void end() throws IOException {
    reader.close();
  }
  
  public void increment() {
    defaultFillArray();
    int elem = interpretData.get(currPtr) + 1;
    interpretData.set(currPtr, elem);
  }
  
  public void decrement() {
    defaultFillArray();
    int elem = interpretData.get(currPtr) - 1;
    interpretData.set(currPtr, elem);
  }
  
  private void defaultFillArray() {
    if (interpretData.size() < currPtr + 1) {
      for (int i = interpretData.size(); i < currPtr + 1; ++i) {
        interpretData.add(0);
      }
    }
  }
  
  public void next() {
    currPtr++;
  }
  
  public void back() {
    currPtr--;
    if (currPtr < 0) {
      logger.trace("Command Back is not done. Current pointer became less than 0.");
      throw new IllegalArgumentException("Wrong input - too many symbols <.");
    }
  }
  
  public void outputBytes() {
    if (interpretData.size() == 0 || interpretData.size() <= currPtr) {
      logger.trace("Command Back is not done because of interpretData's size is 0 or less than current pointer.");
      throw new IllegalArgumentException(
          "Wrong input - trying to print data, that doesn't exist. Check input symbols.");
    }
    System.out.print((char) (interpretData.get(currPtr) % 256));
  }
  
  public void addNumLoop() {
    numLoop++;
    logger.trace("Number of loop increased.");
  }
  
  private ArrayList<Integer> loopStartPtr = new ArrayList<>();
  private ArrayList<Integer> loopEndPtr = new ArrayList<>();
  
  public void addInEndPtr(int c) {
    loopEndPtr.add(c);
  }
  
  private ArrayList<Character> tmpData = new ArrayList<>();
  
  public void addInTmpData(char c) {
    tmpData.add(c);
  }
  
  public void addInStartPtr() {
    loopStartPtr.add(tmpData.size());
  }
  
  public ArrayList<Character> getData() {
    return data;
  }
  
  public Factory getFactory() {
    return thisFactory;
  }
  
  public int getCurrPtr() {
    return currPtr;
  }
  
  public int getNumLoop() {
    return numLoop;
  }
  
  public int getTmpDataSize() {
    return tmpData.size();
  }
  
  public int getEndPtrSize() {
    return loopEndPtr.size();
  }
  
  public int getStartPtrSize() {
    return loopStartPtr.size();
  }
  
  public int getCurrElemOfInterpretData() {
    return interpretData.get(currPtr);
  }
  
  public int getElemOfEndPtr(int idx) {
    return loopEndPtr.get(idx);
  }
  
  public int getElemOfStartPtr(int idx) {
    return loopStartPtr.get(idx);
  }
  
  public char getElemOfTmpData(int idx) {
    return tmpData.get(idx);
  }
  
  public void setNumLoop(int size) {
    numLoop = size;
  }
  
  public void clearLoopArrays() {
    loopStartPtr.clear();
    loopEndPtr.clear();
    tmpData.clear();
    logger.trace("Sizes of loopStartPtr, loopEndPtr and tmpData are 0 now.");           
  }
}
