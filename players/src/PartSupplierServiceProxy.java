import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface PartSupplierServiceProxy {
    public String orderParts(String content, long avgTime) throws AxisFault;
}
