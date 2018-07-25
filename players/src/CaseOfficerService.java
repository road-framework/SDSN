import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.service.Lifecycle;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CaseOfficerService implements Lifecycle {

    private static Logger log = Logger.getLogger(CaseOfficerService.class.getName());
    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters = new HashMap<String, OperationRateLimiter>();

    public CaseOfficerService() {
        roadProperties = ROADProperties.getInstance("players.properties");
        opRateLimiters.put("analyze", new OperationRateLimiter("CaseOfficerService", "analyze", roadProperties));
        opRateLimiters.put("payTC", new OperationRateLimiter("CaseOfficerService", "payTC", roadProperties));
        opRateLimiters.put("payGC", new OperationRateLimiter("CaseOfficerService", "payGC", roadProperties));
        opRateLimiters.put("payPS", new OperationRateLimiter("CaseOfficerService", "payPS", roadProperties));
        opRateLimiters.put("payHC", new OperationRateLimiter("CaseOfficerService", "payHC", roadProperties));
        opRateLimiters.put("payVC", new OperationRateLimiter("CaseOfficerService", "payVC", roadProperties));
        opRateLimiters.put("payTX", new OperationRateLimiter("CaseOfficerService", "payTX", roadProperties));
        opRateLimiters.put("payLF", new OperationRateLimiter("CaseOfficerService", "payLF", roadProperties));
    }

    public AnalyzeReturn analyze(String memId, String complainDetails) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("analyze");
        if (log.isInfoEnabled()) {
            log.info("analyze in CaseOfficerService received >>>>>>>>> : " + memId
                    + " :::: " + complainDetails);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for Analyse : " + rateLimier.getThreshold();
            if (log.isInfoEnabled()) {
                log.info(msg);
            }
            throw new AxisFault(msg);
        }
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        AnalyzeReturn order;
        try {
            order = proxy.analyze(memId, complainDetails, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            order = new AnalyzeReturn();
            order.setOrderTow(complainDetails);
            order.setOrderRepair("OrderRepairRequest");
            order.setOrderLegalAid("OrderLegalAidRequest");
            order.setOrderLegalAid("OrderOrderTaxReqest");
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(order.getOrderRepair() + " " + order.getOrderTow());
        }
        rateLimier.refill();
        return order;
    }

    public String payTC(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payTC");
        if (log.isInfoEnabled()) {
            log.info("payTC in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payTC : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        String result;
        try {
            result = proxy.payTC(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "PayTowResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payGC(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payGC");
        if (log.isInfoEnabled()) {
            log.info("payGC in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payGC : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payGC(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "PayRepairResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payPS(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payPS");
        if (log.isInfoEnabled()) {
            log.info("payPS in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payPS : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payPS(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "PayPastsResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payAS(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("payAS in CaseOfficerService received >>>>>>>>> : " + content);
        }
        String result = "payASResponse";

        if (log.isInfoEnabled()) {
            log.info(result);
        }
        return result;
    }

    public String payHC(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payHC");
        if (log.isInfoEnabled()) {
            log.info("payHC in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payHC : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payHC(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "PayHotelRentResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payVC(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payVC");
        if (log.isInfoEnabled()) {
            log.info("payVC in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payVC : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payVC(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "PayVehicleRentResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payTX(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payTX");
        if (log.isInfoEnabled()) {
            log.info("payTX in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payTX : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payTX(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "payTXResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    public String payLF(String content) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("payLF");
        if (log.isInfoEnabled()) {
            log.info("payLF in CaseOfficerService received >>>>>>>>> : " + content);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for payLF : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.payLF(content, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "payLFResponse";
            // The requirement is to create an artificial delay = average response time
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }
    public String sendGuideList(String guideList) throws AxisFault {
        OperationRateLimiter rateLimier = opRateLimiters.get("SendGuideList");
        if (log.isInfoEnabled()) {
            log.info("SendGuideList in CaseOfficerService received >>>>>>>>> : " + guideList);
        }
        if (!rateLimier.tryConsume()) {
            String msg = "Capacity limit has reached for SendGuideList : " + rateLimier.getThreshold();
            log.error(msg);
            throw new AxisFault(msg);
        }
        String result;
        CaseOfficerProxy proxy = rateLimier.getLimiter().newProxy(
                new CaseOfficerProxyImpl(), CaseOfficerProxy.class, rateLimier.getAverageResponseTime(),
                TimeUnit.MILLISECONDS);
        try {
            result = proxy.sendGuideList(guideList, rateLimier.getAverageResponseTime());
        } catch (UncheckedTimeoutException e) {
            result = "sendGuideListResponse";
            e.printStackTrace();
        }
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        rateLimier.refill();
        return result;
    }

    @Override
    public void init(ServiceContext serviceContext) throws AxisFault {
//        log.info("Initialize CaseOfficerService");
//        ROADProperties  roadProperties = ROADProperties.getInstance("players.properties");
//        ServiceState serviceState = new ServiceState(roadProperties);
//        serviceState.addOperationRateLimier("analyse", new OperationRateLimiter("CaseOfficerService","analyse", roadProperties));
//        serviceState.addOperationRateLimier("payTT", new OperationRateLimiter("CaseOfficerService","payTT", roadProperties));
//        serviceState.addOperationRateLimier("payGR", new OperationRateLimiter("CaseOfficerService","payGR", roadProperties));
//        serviceState.addOperationRateLimier("payPS", new OperationRateLimiter("CaseOfficerService","payPS", roadProperties));
//        serviceState.addOperationRateLimier("payHC", new OperationRateLimiter("CaseOfficerService","payHC", roadProperties));
//        serviceState.addOperationRateLimier("payVC", new OperationRateLimiter("CaseOfficerService","payVC", roadProperties));
//        serviceContext.setProperty("CaseOfficerService", serviceState);
    }

    @Override
    public void destroy(ServiceContext serviceContext) {
//        serviceContext.removeProperty("CaseOfficerService");
    }
}
