package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.IOrganiserRole;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.SerendipEventListener;

/**
 * The event listener class for organizr
 * The organizer can subscribe to interested event patterns. For each and every event pattern, an instance of this class need to be created.
 *
 * @author Malinda
 * @see SerendipOrganizerImpl
 */
public class OrganizerEventListener extends SerendipEventListener {
    static int counter = 0;
    private String id = null;
    private IOrganiserRole orgRole = null;

    public OrganizerEventListener(String ep, Classifier classifier, IOrganiserRole orgRole) {
        this.id = "orgListener" + counter++;
        this.addEventPattern(ep);
        this.orgRole = orgRole;
        setClassifier(classifier);
    }

    @Override
    public void eventPatternMatched(String ep, Classifier classifier1)
            throws SerendipException {
        MessageWrapper mw = new MessageWrapper();
        mw.setMessage("Event pattern " + ep + " matched for" + classifier1);
//        ((OrganiserRole) this.orgRole).sendToOrganiser(mw);
    }

    @Override
    public String getId() {
        return this.id;
    }

}
