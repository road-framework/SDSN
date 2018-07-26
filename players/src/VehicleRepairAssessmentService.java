import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

/**
 * TODO
 */
public class VehicleRepairAssessmentService {
    private static Logger log = Logger.getLogger(VehicleRepairAssessmentService.class.getName());

    public String assessRepair(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("assessRepair in VehicleRepairAssessmentService  received >>>>>>>>> : " + content);
        }
        String result = "AssessRepairResponse";
        // The requirement is to create an artificial delay = average response time
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        return result;
    }
}
