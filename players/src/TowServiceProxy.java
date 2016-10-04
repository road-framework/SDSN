import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface TowServiceProxy {

    public TowReturn tow(String pickupLocation, String garageLocation, long avgTime) throws AxisFault;

    public String pickUp(String pickupLocation, long avgTime) throws AxisFault;

    public String deliver(String garageLocation, long avgTime) throws AxisFault;
}
