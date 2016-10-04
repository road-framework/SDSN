/**
 * TODO documentation
 */
public class TestProxyImpl implements TestProxy {
    @Override
    public void test() {
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Test");
    }
}
