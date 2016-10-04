import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class TowServicePar2 {
    private static Logger log = Logger.getLogger(TowServicePar2.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public TowServicePar2() {
        roadProperties = ROADProperties.getInstance("players.properties");
//        opRateLimiters.put("tow", new OperationRateLimiter("TowServicePar1","tow", roadProperties));
        opRateLimiters.put("pickUp", new OperationRateLimiter("TowServicePar2", "pickUp", roadProperties));
        opRateLimiters.put("deliver", new OperationRateLimiter("TowServicePar2", "deliver", roadProperties));
    }

    public TowReturn tow(String pickupLocation, String garageLocation) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("tow");
        if (log.isInfoEnabled()) {
            log.info("Tow in TowServicePar2 received >>>>>>>>> : " + pickupLocation
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
            result.setOrderTowResponse("OrderTowResponse");
            result.setSendGRLocationResponse("SendGRLocationResponse");
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result.getOrderTowResponse() + " " + result.getSendGRLocationResponse());
        }
        rateLimier.refill();
        return result;
    }

    public String pickUp(String pickupLocation) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("pickUp");
        if (log.isInfoEnabled()) {
            log.info("PickUp in TowServicePar2 received >>>>>>>>> : " + pickupLocation);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for pickUp : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        TowServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new TowServiceProxyImpl(), TowServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.pickUp(pickupLocation, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            // The requirement is to create an artificial delay = average response time
        }
        rateLimier.refill();
        return result;
    }

    public String deliver(String garageLocation) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("deliver");
        if (log.isInfoEnabled()) {
            log.info("Deliver in TowServicePar2 received >>>>>>>>> : " + garageLocation);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for tow : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        TowServiceProxy proxy = rateLimier.getLimiter().newProxy(
                new TowServiceProxyImpl(), TowServiceProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.deliver(garageLocation, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "done";
            // The requirement is to create an artificial delay = average response time
        }
        rateLimier.refill();
        return result;
    }

    public void payTow(String content) {
        if (log.isInfoEnabled()) {
            log.info("payTow in TowServicePar2 received >>>>>>>>> : " + content);
            log.debug("TC paid for towing service");
        }
    }

    public void setInsuranceFacts(PFacts facts) {
        log.info("TowServicePar2 no of facts received = " + facts.getFact().length);
        for (PFact fact : facts.getFact()) {
            log.info(fact.getName() + " : " + fact.getSource()
                    + " : " + fact.getIdentifier().getIdentifierKey() + " : "
                    + fact.getIdentifier().getIdentifierValue());
            PFactAttribute[] attributes = fact.getAttributes().getAttribute();
            for (PFactAttribute attribute : attributes) {
                log.info("Key : Value >>>> " + attribute.getAttributeKey()
                        + " : " + attribute.getAttributeValue());
            }
        }
    }

    public PFacts getInsuranceFacts() {
        log.info("*********************************************");
        log.info("facts sent by CASEOFFICER back end service ");
        log.info("*********************************************");
        return new PFacts();
    }
}
