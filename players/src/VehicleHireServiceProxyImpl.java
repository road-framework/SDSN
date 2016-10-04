import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class VehicleHireServiceProxyImpl implements VehicleHireServiceProxy {
    @Override
    public String rentVehicles(String content, long avgTime) throws AxisFault {
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
