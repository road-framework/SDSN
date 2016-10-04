import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class TowServiceProxyImpl implements TowServiceProxy {

    @Override
    public TowReturn tow(String pickupLocation, String garageLocation, long avgTime) throws AxisFault {
        sleep(avgTime);
        TowReturn result = new TowReturn();
        result.setOrderTowResponse("OrderTowResponse");
        result.setSendGRLocationResponse("SendGRLocationResponse");
        return result;
    }

    @Override
    public String pickUp(String pickupLocation, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "done";
    }

    @Override
    public String deliver(String garageLocation, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "done";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
