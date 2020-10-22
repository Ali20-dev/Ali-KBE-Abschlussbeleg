package htwb.ai;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Ali Aslan & Muhammed Ali Geldi
 */
public class RunMeRunner {

    private static String printUsage() {
        return "Usage: java -jar runmerunner-ALIS.jar className";
    }

    /**
     * Analyzes a class on which methods have a @RunMe annotation. Also runs those methods.
     *
     * @param className The name of the class, including the path like "java.lang.Number"
     * @return The string containing the results of the analysis.
     */
    public String analyzeClass(String className) {
        Class runMeClass;
        try {
            runMeClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return "Error: " + e.toString() + "\n" + printUsage();
        }

        Object instaceOfClass;
        try {
            instaceOfClass = runMeClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return "Error: " + e.toString() + "\n" + printUsage();
        }

        Method[] methods = runMeClass.getDeclaredMethods();
        List<String> methodListWithRunMe = new ArrayList<>();
        List<String> methodListWithoutRunMe = new ArrayList<>();
        Map<String, String> methodsNotInvocable = new TreeMap<>();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            boolean runMeFound = false;
            for (Annotation annotation : annotations) {
                if (annotation.toString().equals("@htwb.ai.RunMe()")) {
//                    String[] cleanedUpMethod = method.getName().split("\\.");
//                    String actualMethodName = cleanedUpMethod[cleanedUpMethod.length - 1];
                    String actualMethodName = method.getName();
                    methodListWithRunMe.add(method.getName());
                    runMeFound = true;
                    try {
                        Object result = method.invoke(instaceOfClass);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        methodsNotInvocable.put(actualMethodName, e.getClass().getSimpleName());
                    }
                }
            }
            if (!runMeFound) {
                methodListWithoutRunMe.add(method.getName());
            }
        }
        /*Without sorting, the getDeclaredMethods() function randomly changed the order of Methods it returns in the
        Array. It was working correctly on MAC but not on Windows and Linux. ???*/
        methodListWithoutRunMe.sort(String.CASE_INSENSITIVE_ORDER);
        methodListWithRunMe.sort(String.CASE_INSENSITIVE_ORDER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nAnalyzed class '").append(className).append("':\n");
        stringBuilder.append("Methods without @RunMe:\n");
        for (String method : methodListWithoutRunMe) {
            stringBuilder.append("\t").append(method).append("\n");
        }
        stringBuilder.append("Methods with @RunMe:\n");
        for (String method : methodListWithRunMe) {
            stringBuilder.append("\t").append(method).append("\n");
        }
        stringBuilder.append("not invocable:\n");
        methodsNotInvocable.forEach((method, errorMessage) -> {
            stringBuilder.append("\t").append(method).append(": ").append(errorMessage).append("\n");
        });

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: Wrong number of arguments!");
            System.out.println(printUsage());
            return;
        }
        RunMeRunner runMeRunner = new RunMeRunner();
        String result = runMeRunner.analyzeClass(args[0]);
        System.out.println(result);
    }
}