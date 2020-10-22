package htwb.ai;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ali Aslan & Muhammed Ali Geldi
 */
class RunMeRunnerTest {

    private RunMeRunner runMeRunner;

    @BeforeEach
    void setUp() {
        runMeRunner = new RunMeRunner();
    }

    /**
     * Tests the behaviour of the program if a non existent class is given to it.
     */
    @Test
    void classNotFoundExceptionTest() {
        String result = runMeRunner.analyzeClass("blub");
        assertTrue(result.contains("Error: java.lang.ClassNotFoundException: blub"));
    }

    /**
     * Tests the behaviour of the program if an interface is given to it (because there is no constructor).
     */
    @Test
    void noSuchMethodExceptionTest() {
        String result = runMeRunner.analyzeClass("java.io.Serializable");
        assertTrue(result.contains("Error: java.lang.NoSuchMethodException: java.io.Serializable.<init>()"));
    }

    /**
     * Tests the behaviour of the program if a non instantiable class is given to it.
     */
    @Test
    void notInstantiableExceptionTest() {
        String result = runMeRunner.analyzeClass("java.lang.Number");
        assertTrue(result.contains("Error: java.lang.InstantiationException"));
    }

    /**
     * Tests the behaviour of the program if the test class is somehow private (e.g. package private).
     */
    @Test
    void illegalAccessExceptionTest() {
        String result = runMeRunner.analyzeClass("htwb.ai.ai.PrivateTestClass");
        System.out.println(result);
        assertTrue(result.contains("Error: java.lang.IllegalAccessException"));
    }

    /**
     * Tests the behaviour of the program if the test classes' constructor throws an exception.
     */
    @Test
    void invocationTargetExceptionTest() {
        String result = runMeRunner.analyzeClass("htwb.ai.BrokenConstructorTest");
        assertTrue(result.contains("Error: java.lang.reflect.InvocationTargetException"));
    }

    /**
     * Tests the behaviour of the program if a proper test class is given to it.
     * The following exceptions get tested via the MeanTestMethods test class:
     * IllegalAccessException: if the method is not public (private or package private outside)
     * InvocationTargetException: if the method throws an exception
     * IllegalArgumentException: if the method has a different number of parameters
     */
    @Test
    void testWithMeanTestMethodsClass() {
        String result = runMeRunner.analyzeClass("htwb.ai.MeanTestMethods");
        assertEquals("Analyzed class 'htwb.ai.MeanTestMethods':\n" +
                "Methods without @RunMe:\n" +
                "\ttestNoRM22\n" +
                "\ttestWithoutRM\n" +
                "Methods with @RunMe:\n" +
                "\tfindMe1\n" +
                "\tfindMe2\n" +
                "\tfindMe3\n" +
                "\tfindMe4\n" +
                "\tfindMe5\n" +
                "\tfindMe6\n" +
                "\tfindMe7\n" +
                "not invocable:\n" +
                "\tfindMe3: IllegalAccessException\n" +
                "\tfindMe6: InvocationTargetException\n" +
                "\tfindMe7: IllegalArgumentException\n", result);
        //TEST PASSES IN IDE, MVN CLEAN PACKAGE FAILS
        //Reason: Randomly appearing "Class)" in the result string???
    }

}