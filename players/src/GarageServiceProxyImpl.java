import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class GarageServiceProxyImpl implements GarageServiceProxy {

    @Override
    public String placeRepairOrder(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "Werribee";
    }

    @Override
    public String assessRepair(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "AssessRepairResponse";
    }

    @Override
    public String doRepair(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "OrderRepairResponse";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
