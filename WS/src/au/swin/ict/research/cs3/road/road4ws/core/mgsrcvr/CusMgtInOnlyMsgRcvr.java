package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.swin.ict.research.cs3.road.road4ws.core.util.IControlPlaneAPI;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

/**
 * The Message receiver to support MEP=In Only
 *
 * @author Malinda Kapuruge
 */
public class CusMgtInOnlyMsgRcvr extends org.apache.axis2.rpc.receivers.RPCInOnlyMessageReceiver {

    private static final Logger log = Logger.getLogger(CusMgtInOnlyMsgRcvr.class);
    private IControlPlaneAPI controlPlaneAPI = null;

    public CusMgtInOnlyMsgRcvr(IControlPlaneAPI controlPlaneAPI) {
        this.controlPlaneAPI = controlPlaneAPI;
    }

    /**
     * Invoke the controlPlaneAPI object.
     * Tip: We pass a reference of controlPlaneAPI to the super via msg context
     */
    public void invokeBusinessLogic(MessageContext msgContext) throws AxisFault {


        if (log.isDebugEnabled()) {
            log.debug("CusMgtInOnlyMsgRcvr invokeBusinessLogic");
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
            throw new AxisFault("[ROAD4WS]Configuration error: Cannot find the ControlPlane manager object");
        }

        super.invokeBusinessLogic(msgContext);


    }

    /**
     * We do not create a new one. The controlPlaneAPI has to remain the same. Cannot have two controlPlaneAPI objects for the same composition.
     */
    protected Object makeNewServiceObject(MessageContext msgContext) throws AxisFault {
        return this.controlPlaneAPI;

    }
}
