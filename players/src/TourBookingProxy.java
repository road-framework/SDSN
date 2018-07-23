import org.apache.axis2.AxisFault;

public interface TourBookingProxy {
    public String bookTour(String content, long avgTime) throws AxisFault;
}
