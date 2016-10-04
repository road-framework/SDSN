import org.apache.axis2.AxisFault;

/**
 * TODO
 */
public interface AusLawServiceProxy {
    public String inspectAccident(String info,long avgTime) throws AxisFault;
}
