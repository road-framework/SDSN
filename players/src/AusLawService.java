import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 */
public class AusLawService {

    private static Logger log = Logger.getLogger(AusLawService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public AusLawService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("inspectAccident", new OperationRateLimiter("AusLawService ", "inspectAccident", roadProperties));
    }

    public String inspectAccident(String info) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("inspectAccident");
        if (log.isInfoEnabled()) {
            log.info("inspectAccident in AusLawService  received >>>>>>>>> : " + info);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for inspectAccident : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        AusLawServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new AusLawServiceProxyImpl(), AusLawServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.inspectAccident(info, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done the inspection";
            // The requirement is to create an artificial delay = average response time
        }
        rateLimier.refill();
        return result;
    }

}
