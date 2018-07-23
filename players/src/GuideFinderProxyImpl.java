import org.apache.axis2.AxisFault;

public class GuideFinderProxyImpl implements GuideFinderProxy {
    @Override
    public String findGuide(String creteria, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "Indika";
    }

    @Override
    public String bookGuide(String bookingInfo, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "BookedGuide";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
