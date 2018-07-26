import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

public class EuroBikeRentalService {
    private static Logger log = Logger.getLogger(EuroBikeRentalService.class.getName());

    public String rentBike(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("rentBike in EuroBikeRentalService  received >>>>>>>>> : " + content);
        }
        String result = "rented a bike for " + content;
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        return result;
    }
}