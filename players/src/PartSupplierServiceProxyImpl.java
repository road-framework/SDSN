import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class PartSupplierServiceProxyImpl implements PartSupplierServiceProxy {

    @Override
    public String orderParts(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "OrderPartsResponse";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
