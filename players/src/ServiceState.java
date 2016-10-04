import java.util.HashMap;

/**
 * TODO documentation
 */
public class ServiceState {

    public ServiceState(ROADProperties roadProperties) {
        this.roadProperties = roadProperties;
    }

    private ROADProperties roadProperties;
    private final HashMap<String, OperationRateLimiter> opRateLimiters =
            new HashMap<String, OperationRateLimiter>();

    public void addOperationRateLimier(String opName, OperationRateLimiter operationRateLimiter) {
        opRateLimiters.put(opName, operationRateLimiter);
    }

    public OperationRateLimiter getOperationRateLimier(String opName) {
        return opRateLimiters.get(opName);
    }
}
