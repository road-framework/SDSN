import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LocalGrocersService {
    private static Logger log = Logger.getLogger(TowServiceSeq.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public LocalGrocersService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("tow", new OperationRateLimiter("LocalGrocersService", "orderGroceries", roadProperties));
    }

    public String orderGroceries(String groceries) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("tow");
        if (log.isInfoEnabled()) {
            log.info("rentEquipment in LocalGrocersService received >>>>>>>>> : " + groceries);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for tow : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GroceryProviderProxy proxy = rateLimier.getLimiter().newProxy(
                new GroceryProviderProxyImpl(), GroceryProviderProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.orderGroceries(groceries, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            e.printStackTrace();
        }
        rateLimier.refill();
        return result;
    }
}
