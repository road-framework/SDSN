import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface GarageServiceProxy {

    public String placeRepairOrder(String content, long avgTime) throws AxisFault;

    public String assessRepair(String content, long avgTime) throws AxisFault;

    public String doRepair(String content, long avgTime) throws AxisFault;
}
