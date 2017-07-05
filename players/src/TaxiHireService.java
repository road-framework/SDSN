import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import java.util.Timer;


/**
 * TODO
 */
public class TaxiHireService {

    private static Logger log = Logger.getLogger(HotelService.class.getName());
    private Timer timer = new Timer(true);

    public TaxiHireService() {
    }

    public String orderTaxi(String content) throws AxisFault {
        MessageContext cMessageContext =
                MessageContext.getCurrentMessageContext();
        Classifier classifier = ClassifierFactory.createClassifier(cMessageContext.getEnvelope());
        if (log.isInfoEnabled()) {
            log.info(classifier.toString());
        }
        if (log.isInfoEnabled()) {
            log.info("rentVehicle in TaxiHireService received >>>>>>>>> : " + content);
        }
        String result = "Taxi order has been accepted.";
        if (log.isInfoEnabled()) {
            log.info(result);
        }
        timer.schedule(new TaxiTimerTask(classifier), 100);
        return result;
    }
}
