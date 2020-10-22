package htwb.ai;

/**
 * Simple class for testing purposes of our RunMeRunner.
 */
public class MeanTestMethods {

    @RunMe
    public void findMe1() {
        System.out.print("findMe1 called!");
    }

    @RunMe
    protected void findMe2() {

    }

    @RunMe
    private void findMe3() {

    }

    @RunMe
    public static void findMe4() {

    }

    @RunMe
    void findMe5() {

    }

    @RunMe
    public void findMe6() {
        throw new NullPointerException();
    }

    @RunMe
    public void findMe7(String param) {

    }

    public void testWithoutRM() {

    }

    private void testNoRM22() {

    }

}