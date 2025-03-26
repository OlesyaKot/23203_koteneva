import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class BrainFackTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private final String[] path = new String[] { "config.commands" };

    @Before
    public void setUpOutput() {
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setOut(originalSystemOut);
        System.setIn(originalSystemIn);
        outputStream.reset();
    }

    @Test
    public void simpleTest1() throws IOException, BadCommandException {
        String inputStr = "++++++++++[>+>+++>+++++++>++++++++++<<<<-]>>>++.>+.+++++++..+++.<<++.>>++++++++.--------.+++.------.--------.<<+.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Hello world!";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void simpleTest2() throws IOException, BadCommandException {
        String inputStr = "+++++[>++[>++++>+++-+<<-]+++++<-]>+++++++++++++++++++++++++++++++++++++++++++++++++++++.>.>++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = ":xb";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void multTest() throws IOException, BadCommandException {
        String inputStr = ">++++[-<++>]<++++++++++++++++++++++++++++++++++++++++++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "8";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void errorTest1() throws IOException, BadCommandException {
        String inputStr = "33333+++++[>++[>++++>+++-+<<-]+++++<-]>+++++++++++++++++++++++++++++++++++++++++++++++++++++.>.>++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Wrong input symbol 3. If is not from BrainFuck.\n";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void errorTest2() throws IOException, BadCommandException {
        String inputStr = "+++++[>++[>++++>+++-+<<-]+++++<-]>+++++++++++++F++++++++++++++++++++++++++++++++++++++++.>.>++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Wrong input symbol F. If is not from BrainFuck.\n";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void errorTest3() throws IOException, BadCommandException {
        String inputStr = "+++++[>++[>++++>+++-+<<-]+++++<-]>++++++++++]++++++++++++++++++++++++++++++++++++++++.>.>++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Wrong input - too many symbols ].\n";

        assertEquals(expectedOutput, outputStream.toString());
    }
        
    @Test
    public void errorTest4() throws IOException, BadCommandException {
        String inputStr = "+++++[>++[>++++>+++-+<<-]+++++<-]>++++++++++<<<<<<++++++++++++++++++++++++++++++++++++++++.>.>++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Wrong input - too many symbols <.\n";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void errorTest5() throws IOException, BadCommandException {
        String inputStr = ".+++++[>++[>++++>+++-+<<-]+++++<-]>+++++++++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(path);

        String expectedOutput = "Wrong input - trying to print data, that doesn't exist. Check input symbols.\n";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void errorTest6() throws IOException, BadCommandException {
        String inputStr = ".+++++[>++[>++++>+++-+<<-]+++++<-]>+++++++++++++++.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());

        System.setIn(inputStream);

        BrainFack.main(new String[] { "config.comands" });

        String expectedOutput = "Problem with factory commands configuration file.\n";

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void classesTest() throws IOException {
        Context context = new Context();
        Increment inc = new Increment();
        Decrement dec = new Decrement();
        Next next = new Next();
        Back back = new Back();
        OutputBytes outputBytes = new OutputBytes();
        ReadAndStore ras = new ReadAndStore();
        StartLoop sl = new StartLoop();
        EndLoop el = new EndLoop();

        inc.execute(context);
        assertEquals(0, context.getCurrPtr());
        assertEquals(1, context.getCurrElemOfInterpretData());
        assertEquals(0, context.getTmpDataSize());

        for (int i = 0; i < 10; ++i) {
            inc.execute(context);
        }

        assertEquals(11, context.getCurrElemOfInterpretData());
        dec.execute(context);
        assertEquals(10, context.getCurrElemOfInterpretData());
        next.execute(context);
        inc.execute(context);
        assertEquals(1, context.getCurrPtr());
        assertEquals(1, context.getCurrElemOfInterpretData());
        back.execute(context);
        assertEquals(0, context.getCurrPtr());
        assertEquals(10, context.getCurrElemOfInterpretData());

        for (int i = 0; i < 50; ++i) {
            inc.execute(context);
        }

        assertEquals(60, context.getCurrElemOfInterpretData());
        outputBytes.execute(context);
        String expectedOutput = "<";
        assertEquals(expectedOutput, outputStream.toString());

        dec.execute(context);
        outputBytes.execute(context);
        expectedOutput = "<;";
        assertEquals(expectedOutput, outputStream.toString());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            el.execute(context);
        });
        assertEquals("Wrong input - too many symbols ].", exception.getMessage());

        sl.execute(context);
        assertEquals(1, context.getNumLoop());
        assertEquals(1, context.getTmpDataSize());
        assertEquals('[', context.getElemOfTmpData(0));
        for (int i = 0; i < 10; ++i) {
            dec.execute(context);
        }
        assertEquals(11, context.getTmpDataSize());
        assertEquals('-', context.getElemOfTmpData(4));

        String inputStr = "A";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());
        System.setIn(inputStream);
        Context context2 = new Context();
        ras.execute(context2);
        assertEquals(65, context2.getCurrElemOfInterpretData());
        inc.execute(context2);
        expectedOutput = "<;\n>B";
        outputBytes.execute(context2);
        assertEquals(expectedOutput, outputStream.toString());
    }
    
    @Test
    public void FactoryTest() throws IOException, BadCommandException {
        Factory factory = Factory.getInstance();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.configFactory("aecasdscazsd");
        });
        assertEquals("Problem with factory commands configuration file.", exception.getMessage());

        factory.configFactory(path[0]);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getCommand("k");
        });
        assertEquals("Wrong input symbol k. If is not from BrainFuck.", exception.getMessage());

        Object newObject = factory.getCommand("+");
        assertEquals(Increment.class, newObject.getClass());
        newObject = factory.getCommand("[");
        assertEquals(StartLoop.class, newObject.getClass());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getCommand("[]");
        });
        assertEquals("Wrong input symbol []. If is not from BrainFuck.", exception.getMessage());       
    }
}
