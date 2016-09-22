package au.edu.swin.ict.serendip.test;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.EventCloud;
import au.edu.swin.ict.serendip.event.SerendipEventListener;
import org.apache.log4j.Logger;

public class TestEventCloud {
    private static Logger log = Logger.getLogger(TestEventCloud.class.getName());

    public static void main(String[] args) throws SerendipException {
        EventCloud ec = new EventCloud(null, null, null, null);
        ec.subscribe(new TempSerRelSubscriber(
                "(([Event1]OR[Event2])AND[Breakdown])"));
        // ec.subscribe(new TempSerRelSubscriber("[event1]AND[event2]", "002"));
        Classifier classifier1 = new Classifier();
        classifier1.setProcessInsId("001");
        Classifier classifier2 = new Classifier();
        classifier2.setProcessInsId("001");
        ec.addEvent(new EventRecord("Event1", classifier1));
        ec.addEvent(new EventRecord("Breakdown", classifier2));
        // ec.addEvent(new EventRecord("event3", "001"));
    }

}

class TempSerRelSubscriber extends SerendipEventListener {

    private static Logger log = Logger.getLogger(TempSerRelSubscriber.class.getName());

    public TempSerRelSubscriber(String eventPattern) {
        super();
        // TODO Auto-generated constructor stub
        this.addEventPattern(eventPattern);
    }

    @Override
    public void eventPatternMatched(String ep, Classifier classifier) {
        // TODO Auto-generated method stub
        log.debug("EP " + ep + " matched for pid=" + classifier);
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return "TempSerRelSubscriber";
    }

}
