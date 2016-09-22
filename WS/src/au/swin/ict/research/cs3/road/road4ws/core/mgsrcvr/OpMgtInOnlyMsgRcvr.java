package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.edu.swin.ict.road.common.IOperationalManagerRole;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

/**
 * The Message receiver to support MEP=In Only
 *
 * @author Malinda Kapuruge
 */
public class OpMgtInOnlyMsgRcvr extends org.apache.axis2.rpc.receivers.RPCInOnlyMessageReceiver {

    private static final Logger log = Logger.getLogger(OpMgtInOnlyMsgRcvr.class);
    private IOperationalManagerRole operationalManagerRole = null;

    public OpMgtInOnlyMsgRcvr(IOperationalManagerRole operationalManagerRole) {
        this.operationalManagerRole = operationalManagerRole;
    }

    /**
     * Invoke the operationalManagerRole object.
     * Tip: We pass a reference of operationalManagerRole to the super via msg context
     */
    public void invokeBusinessLogic(MessageContext msgContext) throws AxisFault {


        if (log.isDebugEnabled()) {
            log.debug("OpMgtInOutMsgRcvr invokeBusinessLogic");
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
            throw new AxisFault("[ROAD4WS]Configuration error: Cannot find the operational manager object");
        }

        super.invokeBusinessLogic(msgContext);


    }

    /**
     * We do not create a new one. The operationalManagerRole has to remain the same. Cannot have two operationalManagerRole objects for the same composition.
     */
    protected Object makeNewServiceObject(MessageContext msgContext) throws AxisFault {
        return this.operationalManagerRole;

    }
}
