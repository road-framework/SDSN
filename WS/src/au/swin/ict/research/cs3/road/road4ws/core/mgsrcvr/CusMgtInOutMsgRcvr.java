package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.swin.ict.research.cs3.road.road4ws.core.util.IControlPlaneAPI;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

/**
 * TODO: Use this in the organizer deployer
 *
 * @author Malinda Kapuruge
 */
public class CusMgtInOutMsgRcvr extends org.apache.axis2.rpc.receivers.RPCMessageReceiver {
    private static final Logger log = Logger.getLogger(CusMgtInOutMsgRcvr.class);
    private IControlPlaneAPI controlPlaneAPI;

    public CusMgtInOutMsgRcvr(IControlPlaneAPI operationalManagerRole) {
        this.controlPlaneAPI = operationalManagerRole;
    }

    public void invokeBusinessLogic(MessageContext msgContext,
                                    MessageContext newmsgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("CusMgtInOutMsgRcvr invokeBusinessLogic");
        }
        Object obj = this.getTheImplementationObject(msgContext);
        if (null != obj) {
            IControlPlaneAPI managerRole = (IControlPlaneAPI) obj;
            if (log.isInfoEnabled()) {
                log.info("[ROAD4WS]The ControlPlane manager has been found for ");
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("[ROAD4WS]The ControlPlane manager object is NULL");
            }
            throw new AxisFault("[ROAD4WS]Configuration error: Cannot find the ControlPlane object");
        }

        super.invokeBusinessLogic(msgContext, newmsgContext);
    }

    /*Overriding the parent method*/
    protected Object makeNewServiceObject(MessageContext msgContext) throws AxisFault {
        return this.controlPlaneAPI;

    }
}
