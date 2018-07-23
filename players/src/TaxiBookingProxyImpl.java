import org.apache.axis2.AxisFault;

public class TaxiBookingProxyImpl implements TaxiBookingProxy {
    @Override
    public String bookTaxi(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "booked taxi";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
