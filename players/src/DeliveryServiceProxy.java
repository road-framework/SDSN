import org.apache.axis2.AxisFault;

public interface DeliveryServiceProxy {
    public String deliver(String garageLocation, long avgTime) throws AxisFault;
}
