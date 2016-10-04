import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

/**
 * TODO
 */
public class FinanceDepService {
    private static Logger log = Logger.getLogger(HotelService.class.getName());

    public FinanceDepService() {
    }

    public String reimburse(String content) throws AxisFault {
        if (log.isInfoEnabled()) {
            log.info("reimburse( in FinanceDepService received >>>>>>>>> : " + content);
        }
        return "reimbursed";
    }
}
