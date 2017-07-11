package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.SerendipEventListener;

/**
 * A single instance of schedualing a scripting.
 *
 * @author Malinda
 */
public class AdaptationScriptSchedular extends SerendipEventListener {
    //static int counter = 0;
    private String id = null;
    private String script = null;
    private SerendipOrganizer org = null;
    private Classifier classifier;

    AdaptationScriptSchedular(String script, String onEventPattern, Classifier classifier, SerendipOrganizer org) {
        this.id = classifier.getProcessInsId() + "_" + onEventPattern;
        this.script = script;
        this.addEventPattern(onEventPattern);
        this.org = org;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return this.id;
    }

    @Override
    public void eventPatternMatched(String ep, Classifier classifier)
            throws SerendipException {
        // TODO Execute the script
        OrganiserMgtOpResult result = this.org.executeScript(this.script);
        if (false == result.getResult()) {
            //TODO: Drop a management message to the organizer

        }

    }

}
