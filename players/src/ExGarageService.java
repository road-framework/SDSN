import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ExGarageService {

    private static Logger log = Logger.getLogger(ExGarageService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public ExGarageService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("orderRepair", new OperationRateLimiter("ExGarageService","orderRepairr", roadProperties));
        opRateLimiters.put("doRepair", new OperationRateLimiter("ExGarageService", "doRepair", roadProperties));
        opRateLimiters.put("assessRepair", new OperationRateLimiter("ExGarageService", "assessRepair", roadProperties));
    }

    public String orderRepair(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("orderRepair");
        if (log.isInfoEnabled()) {
            log.info("placeRepairOrder in ExGarageService received >>>>>>>>> : " + content);
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
            log.info("getGarageLocation in ExGarageService received >>>>>>>>> : " + content);
        }
        String location = "Location is Werribee";
        if (log.isInfoEnabled()) {
            log.info(location);
        }
        return location;
    }

    public String assessRepair(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("assessRepair");
        if (log.isInfoEnabled()) {
            log.info("assessRepair in ExGarageService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for assessRepair : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        GarageServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new GarageServiceProxyImpl(), GarageServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.assessRepair(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "AssessRepairResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String doRepair(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("doRepair");
        if (log.isInfoEnabled()) {
            log.info("doRepair in ExGarageService received >>>>>>>>> : " + content);
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
            log.info("payRepair in ExGarageService received >>>>>>>>> : " + content);
            log.info("GR paid for repair service");
        }
    }

//	public void setInsuranceFacts(Facts facts)
//	{
//		log.debug("*********************************************");
//		log.debug("facts received by CASEOFFICER back end service: " + facts);
//		log.debug("*********************************************");
//	}
//	
//	public Facts getInsuranceFacts()
//	{
//		log.debug("*********************************************");
//		log.debug("facts sent by CASEOFFICER back end service ");
//		log.debug("*********************************************");
//		return new Facts();
//	}

}
