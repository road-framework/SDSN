import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class InGarageService {
    private static Logger log = Logger.getLogger(InGarageService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public InGarageService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("orderRepair", new OperationRateLimiter("InGarageService", "orderRepair", roadProperties));
        opRateLimiters.put("doRepair", new OperationRateLimiter("InGarageService", "doRepair", roadProperties));
    }

    public String orderRepair(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("orderRepair");
        if (log.isInfoEnabled()) {
            log.info("placeRepairOrder in InGarageService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for placeRepairOrder : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GarageServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new GarageServiceProxyImpl(), GarageServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.placeRepairOrder(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "Location is Werribee";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String getGarageLocation(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("getGarageLocation in InGarageService received >>>>>>>>> : " + content);
        }
        String location = "Location is Werribee";
        if (log.isInfoEnabled()) {
            log.info(location);
        }
        return location;
    }

    public String receiveExternalAssessment(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("receiveExternalAssessment in InGarageService received >>>>>>>>> : " + content);
        }
        String result = "ReceiveExternalAssessmentResponse";
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        return result;
    }

    public String assessRepair(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("assessRepair in InGarageService received >>>>>>>>> : " + content);
        }
        String result = "AssessRepairResponse";
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        return result;
    }

    public String doRepair(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("doRepair");
        if (log.isInfoEnabled()) {
            log.info("doRepair in InGarageService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for doRepair : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GarageServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new GarageServiceProxyImpl(), GarageServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.doRepair(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "OrderRepairResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public void payRepair(String content) {
        if (log.isInfoEnabled()) {
            log.info("payRepair in InGarageService received >>>>>>>>> : " + content);
            log.info("GR paid for repair service");
        }
    }
}
