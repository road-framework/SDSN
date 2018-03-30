package au.swin.ict.research.cs3.road.road4ws.message;

import au.edu.swin.ict.road.common.StatWriter;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.utills.ROADProperties;
import au.edu.swin.ict.serendip.message.SOAPFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.log4j.Logger;

/**
 * Class to handle sending SOAP messages.
 *
 * @author Malinda Kapuruge
 */
public class SOAPMessageSender {
    private static final Logger log = Logger.getLogger(SOAPMessageSender.class);
    private static int scTimeout = Integer.parseInt(ROADProperties.getInstance().getProperty("socketTimeout", "600000000"));
    private static int connectTimeout = Integer.parseInt(ROADProperties.getInstance().getProperty("connectionTimeout", "100000000"));

    public static ResponseMsg sendSOAPMsg(MessageWrapper mw, String eprStr, String action,
                                          ConfigurationContext configCtxt, boolean isResponseExpected,
                                          MultiThreadedHttpConnectionManager connManager) throws MessageDeliveryException {
//        ResponseMsg responseMsg = null;
        ServiceClient client = null;
//        SOAPEnvelope envelope = null;
        OperationClient operationClient = null;
        SOAPEnvelope request = (SOAPEnvelope) mw.getMessage();
        try {
            //Create a new config context
            //2407: We do not create new config context
            //String axis2HomeUrlStr = System.getenv("AXIS2_HOME");
            //ConfigurationContext configCtxt = ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2HomeUrlStr+"/conf/axis2.xml");

            //Create service client
            client = new ServiceClient(configCtxt, null);//null=service
            if (connManager != null) {
                client.getServiceContext().setProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER, connManager);
            }
            Options options = new Options();
            options.setAction(action);
            options.setSenderTransport(Constants.TRANSPORT_HTTP, configCtxt.getAxisConfiguration());

            options.setTo(new EndpointReference(eprStr));
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            options.setProperty(HTTPConstants.SO_TIMEOUT, scTimeout);
            options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, connectTimeout);
            client.setOptions(options);
            if (mw.getClassifier() != null) {
                addHeaders(request, mw);
            }

// add whatever you want to omElement here

//            client.addHeader(omElement);
//            client.addStringHeader(new QName("http://servicenetwork.com", "vsnId"), mw.getClassifier().getVsnId());
//            client.addStringHeader(new QName("http://servicenetwork.com", "processId"), mw.getClassifier().getProcessId());
//            client.addStringHeader(new QName("http://servicenetwork.com", "processInstanceId"), mw.getClassifier().getProcessInsId());

            //creating message context
            MessageContext outMsgCtx = new MessageContext();

            //Set the envelope

            outMsgCtx.setEnvelope(request);
            outMsgCtx.setConfigurationContext(configCtxt);//twice?


            //Create opclient from the svc client Ref:http://today.java.net/pub/a/today/2006/12/13/invoking-web-services-using-apache-axis2.html
            if (isResponseExpected) {
                operationClient = client.createClient(ServiceClient.ANON_OUT_IN_OP);
            } else {
                operationClient = client.createClient(ServiceClient.ANON_ROBUST_OUT_ONLY_OP);
            }

            operationClient.addMessageContext(outMsgCtx);

            //Execute
//            long startTime = System.currentTimeMillis();
//            operationClient.execute(true);
            //Process the response for two way interactions
            if (isResponseExpected) {
//                envelope = ;
                //Now we create our response
                // uncomment following if you need to avoid calling
                ResponseMsg responseMsg =
                        new ResponseMsg(request, true);
//
//                ResponseMsg responseMsg =
//                        new ResponseMsg(operationClient.getMessageContext("In").getEnvelope(), true);
//                responseMsg.setSoapAction(operationClient.getMessageContext("In").getSoapAction());
//                long endTime = System.currentTimeMillis();
//                StatWriter.writeResTime("Service", endTime - startTime);
                return responseMsg;
            }
        } catch (AxisFault e) {
            System.out.println(e.getMessage());
            // TODO Auto-generated catch block
            ResponseMsg responseMsg =
                    new ResponseMsg(SOAPFactory.createFaultEnvelope(request, e.getMessage()), true);
            log.error(e);
            return responseMsg;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e);
            e.printStackTrace();
        } finally {
            try {
                client.cleanup();
                client.cleanupTransport();
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return null;
    }

    private static void addHeaders(SOAPEnvelope envelope, MessageWrapper mw) {
        boolean isSOAP11 = true;
        String soapNamespaceURI = envelope.getNamespace().getNamespaceURI();

        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
                .equals(soapNamespaceURI)) {
            isSOAP11 = false;
        }

        org.apache.axiom.soap.SOAPFactory factory;
        if (isSOAP11) {
            factory = OMAbstractFactory.getSOAP11Factory();
        } else {
            factory = OMAbstractFactory.getSOAP12Factory();
        }
        OMNamespace ns = factory.createOMNamespace("http://servicenetwork.com", "snns");
        SOAPHeaderBlock h1 = factory.createSOAPHeaderBlock("vsnId", ns);
        h1.setText(mw.getClassifier().getVsnId());
        SOAPHeaderBlock h2 = factory.createSOAPHeaderBlock("processId", ns);
        h2.setText(mw.getClassifier().getProcessId());
        SOAPHeaderBlock h3 = factory.createSOAPHeaderBlock("processInstanceId", ns);
        h3.setText(mw.getClassifier().getProcessInsId());
        envelope.getHeader().addChild(h1);
        envelope.getHeader().addChild(h2);
        envelope.getHeader().addChild(h3);
    }
}