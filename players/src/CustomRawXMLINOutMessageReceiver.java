import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.util.MessageContextBuilder;

import java.lang.reflect.Method;


public class CustomRawXMLINOutMessageReceiver extends AbstractMessageReceiver
        implements MessageReceiver {

    private MethodObject findOperation(AxisOperation op, Class<?> implClass) {
        MethodObject methodObj = (MethodObject) (op.getParameterValue("myMethod"));
        if (methodObj != null && methodObj.getMethod().getDeclaringClass() == implClass) return methodObj;

        String methodName = op.getName().getLocalPart();
        Method method;
        try {
            // Looking for a methodObj of the form "OMElement methodObj(OMElement)"
            method = implClass.getMethod(methodName, new Class[]{OMElement.class});
            methodObj = new MethodObject(method, false);
            if (method.getReturnType().equals(OMElement.class)) {
                try {
                    op.addParameter("myMethod", methodObj);
                } catch (AxisFault axisFault) {
                    // Do nothing here
                }
                return methodObj;
            }
        } catch (NoSuchMethodException e) {
            try {
                method = implClass.getMethod(methodName);
                methodObj = new MethodObject(method, true);
                if (method.getReturnType().equals(OMElement.class)) {
                    try {
                        op.addParameter("myMethod", methodObj);
                    } catch (AxisFault axisFault) {
                        // Do nothing here
                    }
                    return methodObj;
                }
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }

            // Fault through
        }

        return null;
    }

    /**
     * Invokes the bussiness logic invocation on the service implementation class
     *
     * @param msgContext    the incoming message context
     * @param newmsgContext the response message context
     * @throws AxisFault on invalid method (wrong signature) or behaviour (return null)
     */
    public void invokeBusinessLogic(MessageContext msgContext, MessageContext newmsgContext)
            throws AxisFault {
        try {

            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            // find the WebService method
            Class<?> implClass = obj.getClass();

            AxisOperation opDesc = msgContext.getAxisOperation();
            MethodObject methodObject = findOperation(opDesc, implClass);
            Method method = methodObject.getMethod();

            if (method == null) {
                throw new AxisFault(Messages.getMessage("methodDoesNotExistInOut",
                        opDesc.getName().toString()));
            }

            OMElement result = null;
            if (methodObject.isNoParameter()) {
                result = (OMElement) method.invoke(
                        obj);
            } else {
                result = (OMElement) method.invoke(
                        obj, new Object[]{msgContext.getEnvelope().getBody().getFirstElement()});
            }
            SOAPFactory fac = getSOAPFactory(msgContext);
            SOAPEnvelope envelope = fac.getDefaultEnvelope();

            if (result != null) {
                envelope.getBody().addChild(result);
            }

            newmsgContext.setEnvelope(envelope);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }


    public final void invokeBusinessLogic(MessageContext msgContext) throws AxisFault {
        MessageContext outMsgContext = MessageContextBuilder.createOutMessageContext(msgContext);
        outMsgContext.getOperationContext().addMessageContext(outMsgContext);

        invokeBusinessLogic(msgContext, outMsgContext);
        replicateState(msgContext);

        AxisEngine.send(outMsgContext);
    }
}
