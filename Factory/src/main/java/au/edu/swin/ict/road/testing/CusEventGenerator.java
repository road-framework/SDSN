package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.IEvent;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

public class CusEventGenerator {
    private static CusEventGenerator ourInstance = new CusEventGenerator();

    public static CusEventGenerator getInstance() {
        return ourInstance;
    }

    private CusEventGenerator() {
    }

    public List<IEvent> generateEventsForROSAS(OMElement featureConf) {
        List<IEvent> iEvents = new ArrayList<>();
        return iEvents;
    }
}
