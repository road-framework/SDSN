import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class HotelServiceProxyImpl implements HotelServiceProxy {
    @Override
    public String rentRooms(String content, long avgTime) throws AxisFault {
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
