import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HotelService {

    private static Logger log = Logger.getLogger(HotelService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public HotelService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("rentRoom", new OperationRateLimiter("HotelService", "rentRoom", roadProperties));
    }

    public String rentRoom(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("rentRoom");
        if (log.isInfoEnabled()) {
            log.info("rentRoom in HotelService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for rentRoom : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        HotelServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new HotelServiceProxyImpl(), HotelServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.rentRooms(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            // The requirement is to create an artificial delay = average response time
        }
        rateLimier.refill();
        return result;
    }

    public String endRoomRent(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("endRoomRent in HotelService received >>>>>>>>> : " + content);
        }
        return "done";
    }
}
