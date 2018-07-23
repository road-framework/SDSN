import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MacDeliveryService {
    private static Logger log = Logger.getLogger(TowServicePar.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public MacDeliveryService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("deliver", new OperationRateLimiter("MacDeliveryService", "deliver", roadProperties));
    }

    public String deliver(String garageLocation) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("deliver");
        if (log.isInfoEnabled()) {
            log.info("Deliver in MacDeliveryService received >>>>>>>>> : " + garageLocation);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for tow : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        DeliveryServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new DeliveryServiceProxyImpl(), DeliveryServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.deliver(garageLocation, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "Delivered to " + garageLocation;
            e.printStackTrace();
        }
        rateLimier.refill();
        return result;
    }
}
