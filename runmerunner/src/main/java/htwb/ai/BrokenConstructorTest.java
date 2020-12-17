package htwb.ai;

public class BrokenConstructorTest {

    public BrokenConstructorTest(){
        throw new NullPointerException();
    }
}
