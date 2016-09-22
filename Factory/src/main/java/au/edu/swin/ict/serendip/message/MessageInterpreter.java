package au.edu.swin.ict.serendip.message;

import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.core.SerendipException;

/**
 * @author Malinda
 * @deprecated
 */
public abstract class MessageInterpreter {
    protected SerendipEngine engine = null;

    public MessageInterpreter(SerendipEngine engine) {
        this.engine = engine;
    }

    public abstract EventRecord[] interpretMessage(Message msg)
    throws SerendipException;

    protected boolean isAmbiguous(String eventPattern) {
        // Ambiguous
        if (eventPattern.contains("OR")) {
            return true;
        }

        return false;
    }

}
