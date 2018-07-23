import org.apache.axis2.AxisFault;

public class TourBookingProxyImpl implements TourBookingProxy {
    @Override
    public String bookTour(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "booked tour";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
