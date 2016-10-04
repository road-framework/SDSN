import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */
public class VehicleHireService {
    private static Logger log = Logger.getLogger(HotelService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public VehicleHireService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("rentVehicle", new OperationRateLimiter("VehicleHireService", "rentVehicle", roadProperties));
    }

    public String rentVehicle(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("rentVehicle");
        if (log.isInfoEnabled()) {
            log.info("rentVehicle in VehicleHireService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for rentVehicle : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        VehicleHireServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new VehicleHireServiceProxyImpl(), VehicleHireServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.rentVehicles(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String endVehicleRent(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("endVehicleRent in VehicleHireService received >>>>>>>>> : " + content);
        }
        return "done";
    }
}
