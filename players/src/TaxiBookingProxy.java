import org.apache.axis2.AxisFault;

public interface TaxiBookingProxy {
    public String bookTaxi(String content, long avgTime) throws AxisFault;
}
