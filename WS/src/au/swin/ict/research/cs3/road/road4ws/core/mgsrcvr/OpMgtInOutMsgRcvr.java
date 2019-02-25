package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.edu.swin.ict.road.common.IOperationalManagerRole;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

/**
 * TODO: Use this in the organizer deployer
 *
 * @author Malinda Kapuruge
 */
public class OpMgtInOutMsgRcvr extends org.apache.axis2.rpc.receivers.RPCMessageReceiver {
    private static final Logger log = Logger.getLogger(OpMgtInOutMsgRcvr.class);
    private IOperationalManagerRole operationalManagerRole;

    public OpMgtInOutMsgRcvr(IOperationalManagerRole operationalManagerRole) {
        this.operationalManagerRole = operationalManagerRole;
    }

    public void invokeBusinessLogic(MessageContext msgContext,
                                    MessageContext newmsgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("OoMgtInOutMsgRcvr invokeBusinessLogic");
        }
        Object obj = this.getTheImplementationObject(msgContext);
        if (null != obj) {
            IOperationalManagerRole managerRole = (IOperationalManagerRole) obj;
            if (log.isInfoEnabled()) {
                log.info("[ROAD4WS]The operational manager has been found for ");
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("[ROAD4WS]The operational manager object is NULL");
            }
            throw new AxisFault("[ROAD4WS]Configuration error: Cannot find the operational managerobject");
        }

        super.invokeBusinessLogic(msgContext, newmsgContext);
    }

    /*Overriding the parent method*/
    protected Object makeNewServiceObject(MessageContext msgContext) throws AxisFault {
        return this.operationalManagerRole;

    }
}
