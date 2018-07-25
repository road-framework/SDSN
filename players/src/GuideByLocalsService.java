import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GuideByLocalsService {
    private static Logger log = Logger.getLogger(GuideByLocalsService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public GuideByLocalsService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("findGuide", new OperationRateLimiter("GuideByLocalsService ", "findGuide", roadProperties));
        opRateLimiters.put("bookGuide", new OperationRateLimiter("GuideByLocalsService ", "bookGuide", roadProperties));
    }

    public String findGuide(String criteria) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("findGuide");
        if (log.isInfoEnabled()) {
            log.info("findGuide in GuideByLocalsService  received >>>>>>>>> : " + criteria);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for findGuide  : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GuideFinderProxy proxy = rateLimier.getLimiter().newProxy(
                new GuideFinderProxyImpl(), GuideFinderProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.findGuide(criteria, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            e.printStackTrace();
            result = "Indika";
        }
        rateLimier.refill();
        return result;
    }

    public String bookGuide(String bookingInfo) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("bookGuide");
        if (log.isInfoEnabled()) {
            log.info("bookGuide in GuideByLocalsService  received >>>>>>>>> : " + bookingInfo);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for bookGuide  : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GuideFinderProxy proxy = rateLimier.getLimiter().newProxy(
                new GuideFinderProxyImpl(), GuideFinderProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.bookGuide(bookingInfo, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            e.printStackTrace();
            result = "BookedGuide";
        }
        rateLimier.refill();
        return result;
    }
}
