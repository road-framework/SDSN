import org.apache.axis2.AxisFault;

public interface GuideFinderProxy {

    public String findGuide(String creteria, long avgTime) throws AxisFault;

    public String bookGuide(String bookingInfo, long avgTime) throws AxisFault;
}
