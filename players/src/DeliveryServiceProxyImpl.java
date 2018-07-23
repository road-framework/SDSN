import org.apache.axis2.AxisFault;

public class DeliveryServiceProxyImpl implements DeliveryServiceProxy {
    @Override
    public String deliver(String garageLocation, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "Delivered to " + garageLocation;
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
