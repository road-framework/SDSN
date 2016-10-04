import org.apache.axis2.AxisFault;

/**
 * TODO
 */
public class AusLawServiceProxyImpl implements AusLawServiceProxy {
    @Override
    public String inspectAccident(String info, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "done inspection";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
