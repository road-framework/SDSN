package au.swin.ict.research.cs3.road.road4ws.roadmodule;

import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;

public class TenantDispatcher extends AbstractHandler {

    public InvocationResponse invoke(MessageContext msgContext)
            throws AxisFault {
        EndpointReference epr = msgContext.getTo();
        if (epr != null) {
            String toAddress = epr.getAddress();
            if (toAddress != null) {
                int startIndex = toAddress.indexOf("/t/");
                if (startIndex > 0) {
                    String parts[] = toAddress.split("/t/");
                    String tenantDomain = parts[1].substring(0, parts[1].indexOf("/"));
                    String suffix = parts[1].substring(parts[1].indexOf("/"));
                    String newAddress = parts[0] + suffix;
                    msgContext
                            .setProperty(ROADConstants.ROAD4WS_PROCESS_GROUP, tenantDomain);
                    EndpointReference newEpr = new EndpointReference(newAddress);
                    msgContext.setTo(newEpr);
                    msgContext.getOptions().setTo(newEpr);
                }
            }
        }

        return InvocationResponse.CONTINUE;
    }
}
