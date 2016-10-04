import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class MarkTowService {
    private static Logger log = Logger.getLogger(MarkTowService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public MarkTowService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("tow", new OperationRateLimiter("MarkTowService", "tow", roadProperties));
    }

    public TowReturn tow(String pickupLocation, String garageLocation) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("tow");
        if (log.isInfoEnabled()) {
            log.info("Tow in MarkTowService received >>>>>>>>> : " + pickupLocation
                    + " :::::: " + garageLocation);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for tow : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        TowReturn result;
        TowServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new TowServiceProxyImpl(), TowServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.tow(pickupLocation, garageLocation, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = new TowReturn();
            result.setOrderTowResponse("MarkTowService-OrderTowResponse");
            result.setSendGRLocationResponse("MarkTowService-SendGRLocationResponse");
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result.getOrderTowResponse() + " " + result.getSendGRLocationResponse());
        }
        rateLimier.refill();
        return result;
    }
}
