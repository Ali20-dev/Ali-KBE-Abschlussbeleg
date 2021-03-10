package htwb.ai;

/**
 * Simple class for testing purposes of our RunMeRunner.
 */
public class MeanTestMethods {

    // public method
    @RunMe
    public void findMe1() {
        System.out.print("findMe1 called!");
    }

    // protected method
    @RunMe
    protected void findMe2() {

    }

    // private method -> IllegalAccessReflection
    @RunMe
    private void findMe3() {

    }

    // static method
    @RunMe
    public static void findMe4() {

    }

    // package private method
    @RunMe
    void findMe5() {

    }

    // method that throws exception -> trigger InvocationTargetException
    @RunMe
    public void findMe6() {
        throw new NullPointerException();
    }

    // method with params -> trigger IllegalArgumentException
    @RunMe
    public void findMe7(String param) {

    }

    public void testWithoutRMPublic() {

    }

    private void testWithoutRMPrivate() {

    }

}