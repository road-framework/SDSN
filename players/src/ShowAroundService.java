import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ShowAroundService {
    private static Logger log = Logger.getLogger(ShowAroundService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public ShowAroundService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("bookTour", new OperationRateLimiter("ShowAroundService", "bookTour", roadProperties));
    }

    public String bookTour(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("bookTour");
        if (log.isInfoEnabled()) {
            log.info("bookTour in ShowAroundService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for bookTour  : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        TourBookingProxy proxy = rateLimier.getLimiter().newProxy(
                new TourBookingProxyImpl(), TourBookingProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.bookTour(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            e.printStackTrace();
            result = "Booked Tour";
        }
        rateLimier.refill();
        return result;
    }
}
