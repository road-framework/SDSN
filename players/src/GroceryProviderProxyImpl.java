import org.apache.axis2.AxisFault;

public class GroceryProviderProxyImpl implements GroceryProviderProxy {
    @Override
    public String orderGroceries(String groceries, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "grocery order accepted";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
