package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.BaseManagementStateChangeListener;
import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.VSNInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.EventCloud;

/**
 * TODO
 */
public class VSNInstanceStateChangeListener extends BaseManagementStateChangeListener {

    private String destinationRole;
    private EventCloud eventCloud;

    public VSNInstanceStateChangeListener(String destinationRole, EventCloud eventCloud) {
        this.destinationRole = destinationRole;
        this.eventCloud = eventCloud;
    }

    @Override
    public void notify(ManagementState managementState) {
        VSNInstance vsnInstance = (VSNInstance) managementState;
        try {
            eventCloud.addEvent(
                    new EventRecord("eVSNInstanceStateChanged", vsnInstance.getClassifier(),
                                    ManagementState.STATE_ACTIVE, destinationRole));
        } catch (SerendipException e) {
            e.printStackTrace();
        }
    }
}

