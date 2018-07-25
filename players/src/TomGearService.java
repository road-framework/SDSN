import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TomGearService {
    private static Logger log = Logger.getLogger(TowServiceSeq.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public TomGearService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("rentAndDeliverEquipment", new OperationRateLimiter("TomGearService", "rentAndDeliverEquipment", roadProperties));
    }

    public String rentAndDeliverEquipment(String equipmentRequirements) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("rentAndDeliverEquipment");
        if (log.isInfoEnabled()) {
            log.info("rentEquipment in TomGearService received >>>>>>>>> : " + equipmentRequirements);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for tow : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        EquipmentProviderProxy proxy = rateLimier.getLimiter().newProxy(
                new EquipmentProviderProxyImpl(), EquipmentProviderProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.rentEquipment(equipmentRequirements, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            e.printStackTrace();
        }
        rateLimier.refill();
        return result;
    }
}
