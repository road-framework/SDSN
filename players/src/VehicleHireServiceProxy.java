import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface VehicleHireServiceProxy {
    public String rentVehicles(String content, long avgTime) throws AxisFault;
}
