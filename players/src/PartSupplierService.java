import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */
public class PartSupplierService {

    private static Logger log = Logger.getLogger(InGarageService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public PartSupplierService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("orderParts", new OperationRateLimiter("PartSupplierService", "orderParts", roadProperties));
    }

    public String orderParts(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("orderParts");
        if (log.isInfoEnabled()) {
            log.info("orderParts in PartSupplierService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for orderParts : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        PartSupplierServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new PartSupplierServiceProxyImpl(), PartSupplierServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.orderParts(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "OrderPartsResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }
}
