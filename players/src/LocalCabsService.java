import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LocalCabsService {
    private static Logger log = Logger.getLogger(GuideByLocalsService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public LocalCabsService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("bookTaxi", new OperationRateLimiter("LocalCabsService", "bookTaxi", roadProperties));
    }

    public String bookTaxi(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("bookTaxi");
        if (log.isInfoEnabled()) {
            log.info("bookTaxi in LocalCabsService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for bookTaxi  : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        TaxiBookingProxy proxy = rateLimier.getLimiter().newProxy(
                new TaxiBookingProxyImpl(), TaxiBookingProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.bookTaxi(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            e.printStackTrace();
            result = "Booked Taxi";
        }
        rateLimier.refill();
        return result;
    }
}
