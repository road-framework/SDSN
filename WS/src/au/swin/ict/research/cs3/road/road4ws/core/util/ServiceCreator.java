package au.swin.ict.research.cs3.road.road4ws.core.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.receivers.RawXMLINOutMessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;

import javax.xml.namespace.QName;
import java.util.List;

public class ServiceCreator {

    public static AxisService createSampleRoleAsAService(String roleName,
                                                         List<String> operationNames) throws AxisFault {
        AxisService service = new AxisService(roleName);

        for (String operationName : operationNames) {
            QName opName = new QName(
                    operationName);
            AxisOperation axisOp = new InOutAxisOperation(opName);
            MessageReceiver messageReceiver = new RawXMLINOutMessageReceiver();

            axisOp.setMessageReceiver(messageReceiver);
            axisOp.setStyle(WSDLConstants.STYLE_RPC);

            service.addOperation(axisOp);
            service.mapActionToOperation(Constants.AXIS2_NAMESPACE_URI + "/"
                    + opName.getLocalPart(), axisOp);

        }

        return service;
    }

    public static AxisService createService(String serviceName) {
        javax.xml.namespace.QName serviceNameQ = new javax.xml.namespace.QName(
                serviceName);
        /* More TODO */

        return new AxisService(serviceNameQ.getLocalPart());
    }

    public static AxisOperation createOperation(String operationName) {
        javax.xml.namespace.QName opName = new javax.xml.namespace.QName(
                operationName);
        MessageReceiver messageReceiver = new RawXMLINOutMessageReceiver();
        AxisOperation axisOp = new OutInAxisOperation(opName);
        axisOp.setMessageReceiver(messageReceiver);
        axisOp.setStyle(WSDLConstants.STYLE_RPC);

        return axisOp;
    }

}
