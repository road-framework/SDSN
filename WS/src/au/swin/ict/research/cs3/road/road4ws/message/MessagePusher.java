package au.swin.ict.research.cs3.road.road4ws.message;

import au.edu.swin.ict.road.composite.IRole;
import au.edu.swin.ict.road.composite.listeners.RolePushMessageListener;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.MessageWrapper.SyncType;
import au.edu.swin.ict.road.composite.utills.ROADProperties;
import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.log4j.Logger;

/**
 * Pushes message to a player specified by the endpoint. Collect the response
 * message if any and returns it back to roadfactory
 *
 * @author Malinda Kapuruge
 */
public class MessagePusher implements RolePushMessageListener {
    private static final Logger log = Logger.getLogger(MessagePusher.class);
    private ConfigurationContext configurationContext = null;
    private IdleConnectionTimeoutThread idleConnectionTimeoutThread;
    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;
    private String id;

    public MessagePusher(ConfigurationContext configurationContext, String id) {
        this.id = id;
        String axis2HomeUrlStr = System.getenv("AXIS2_HOME");
        try {
            this.configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2HomeUrlStr + "/conf/axis2.xml");
            init();

        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }//configurationContext;
    }

    public MessagePusher(ConfigurationContext configurationContext, String id, boolean multiThread) {
        this.id = id;
        this.configurationContext = configurationContext;
        init();
    }

    private void init() {
        MultiThreadedHttpConnectionManager connManager = (MultiThreadedHttpConnectionManager) configurationContext.getProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER);
        ROADProperties roadProperties = ROADProperties.getInstance();
        if (connManager == null) {
            connManager = new MultiThreadedHttpConnectionManager();
            configurationContext.setProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER, connManager);
            connManager.setParams(new HttpConnectionManagerParams());
        }
        connManager.getParams().setMaxTotalConnections(Integer.parseInt(roadProperties.getProperty("maxTotalConnections", "100")));
        connManager.getParams().setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, Integer.parseInt(roadProperties.getProperty("maxConnectionsPerHost", "100")));
        idleConnectionTimeoutThread = new IdleConnectionTimeoutThread();
        this.multiThreadedHttpConnectionManager = connManager;
        idleConnectionTimeoutThread.setName("Http_Idle_Connection_Timeout_Thread");
        idleConnectionTimeoutThread.setConnectionTimeout(Integer.parseInt(roadProperties.getProperty("idleConnectionTimeout", "30000")));
        idleConnectionTimeoutThread.setTimeoutInterval(Integer.parseInt(roadProperties.getProperty("idleTimeoutInterval", "30000")));
        idleConnectionTimeoutThread.addConnectionManager(connManager);
        idleConnectionTimeoutThread.start();
    }

    @Override
    public void pushMessageRecieved(final IRole role, MessageWrapper mw) {
        //We have predefined number of push attepmts
        int pushAttempts = ROADConstants.ROAD4WS_DEFAULT_PUSH_ATTEMPTS;
        if (log.isInfoEnabled()) {
            log.info("A message need to be pushed as there is one in the queue of Role="
                    + role.getName());
        }
        //Get the message that need to pushed
//        MessageWrapper mw = role.getNextPushMessage();

        // Quit if this is a false alarm
        if (null == mw) {
            if (log.isInfoEnabled()) {
                log.info("No more messages in " + role.getName() + " queue");
            }
            // break;
            return;
        }

        // If this is as a response, well.. let the message reliever to handle
        if (mw.isResponse()) {// DO we still need this check????
            // break;
            return;
        }

        // Get the epr from the Role and the operation
        String playerBinding = role.getPlayerBinding().replaceAll("(\\r|\\n)", "").trim();
        if (log.isDebugEnabled()) {
            log.debug("player binding " + playerBinding);
        }
        String eprStr = playerBinding + "/" + mw.getOperationName();
        if (log.isDebugEnabled()) {
            log.debug("eprStr " + eprStr);
        }
        //Unwrap
        Object msg = mw.getMessage();

        if (mw.getSyncType() == SyncType.OUT) {
            try {
                SOAPMessageSender.sendSOAPMsg(mw, eprStr, null, configurationContext, false, multiThreadedHttpConnectionManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (null != msg) {
            int attempt = 1;
            boolean success = false;
            //We use SOAP message delivery
            SOAPMessageSender messageSender = new SOAPMessageSender();
            // Try to send three times if not success
            while (!success && attempt < pushAttempts) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("[Attempt =" + attempt
                                + "]Pushing message of Role =" + role.getName()
                                + ",to end point =" + eprStr + ",with action ="
                                + mw.getOperationName());
                    }
                    attempt++;
                    //This is where we send the message
//					ResponseMsg responseMsg = messageSender
//							.sendSOAPMsg(
//									msg,
//									eprStr,
//									(String) mw
//											.getProperty(ROADConstants.ROAD4WS_MW_SOAP_ACTION));
                    ResponseMsg responseMsg = SOAPMessageSender.sendSOAPMsg(mw, eprStr, null, configurationContext, true, multiThreadedHttpConnectionManager);
                    // If there is no exception that means we have delivered the message
                    success = responseMsg.isSuccessDelivery();
                    // If there is a response route it to the roadfactory
                    if ((success)
                            && (null != responseMsg.getEnvelope())) {
                        // TODO route the SOAP Envelope to the roadfactory
                        if (log.isInfoEnabled()) {
                            log.info("Message delivered to " + eprStr);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("There is a response"
                                    + responseMsg.getEnvelope().toString());
                        }
                        //We will remember the operation name
                        String opName = responseMsg.getEnvelope().getSOAPBodyFirstElementLocalName();//
                        //uncomment if no servcie call - overhead check
//                        MessageWrapper smwResponse = new MessageWrapper(
//                                responseMsg.getEnvelope(), opName + "Response", true);
                        MessageWrapper smwResponse = new MessageWrapper(
                                responseMsg.getEnvelope(), opName, true);
                        smwResponse.setFault(responseMsg.getEnvelope().hasFault());
                        // Set the SOAP Action
                        smwResponse.setClassifier(mw.getClassifier());   //TODO Indika
                        smwResponse.setClientID(mw.getClientID());
                        smwResponse.setProperty(
                                ROADConstants.ROAD4WS_MW_SOAP_ACTION,
                                responseMsg.getSoapAction());
                        // Set originated role
                        smwResponse.setOriginRole(role);
                        //Set the correlation that of the request
                        smwResponse.setCorrelationId(mw.getCorrelationId());
                        if (log.isDebugEnabled()) {
                            log.debug("Correlation id set ." + smwResponse.getCorrelationId() + " " + smwResponse.getClientID());
                        }
                        //Do we still need this?
                        smwResponse.setProperty(
                                ROADConstants.ROAD4WS_MW_IS_RESPONSE,
                                ROADConstants.ROAD4WS_MW_RESPONSE);
                        smwResponse.setTaskId(mw.getTaskId());
                        // Drop message to the composite
                        role.putMessage(smwResponse);
                        if (log.isDebugEnabled()) {
                            log.debug("Response message successfully dropped back to ." + role.getId());
                        }

                    } else {
                        if (log.isInfoEnabled()) {
                            log.info("There is no response");
                        }
                    }
                } catch (MessageDeliveryException e) {
                    success = false;
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }// eof while loop (!success && attempt < pushAttempts)

            if (!success) {
                if (log.isDebugEnabled()) {
                    log.debug("Message delivery permanently failed");
                }


            } else {
                // return true;
            }

        } else {
            log.error("Message is NULL. Cannot be pushed to " + eprStr);
        }

    }

    @Override
    public MessageWrapper sendReceiveMessage(MessageWrapper mw) {
        // Get the epr from the Role and the operation
        String playerBinding = mw.getDestinationPlayerBinding().replaceAll("(\\r|\\n)", "").trim();
        if (log.isDebugEnabled()) {
            log.debug("player binding " + playerBinding);
        }
        String eprStr = playerBinding + "/" + mw.getOperationName();
        if (log.isDebugEnabled()) {
            log.debug("eprStr " + eprStr);
        }
        try {
            ResponseMsg responseMsg =
                    SOAPMessageSender.sendSOAPMsg(mw, eprStr, null, configurationContext, true, multiThreadedHttpConnectionManager);
            boolean success = responseMsg.isSuccessDelivery();
            // If there is a response route it to the roadfactory
            if ((success) && (null != responseMsg.getEnvelope())) {
                // TODO route the SOAP Envelope to the roadfactory
                if (log.isInfoEnabled()) {
                    log.info("Message delivered to " + eprStr);
                }
                if (log.isDebugEnabled()) {
                    log.debug("There is a response"
                            + responseMsg.getEnvelope().toString());
                }
                //We will remember the operation name
                String opName = responseMsg.getEnvelope().getSOAPBodyFirstElementLocalName();//
                MessageWrapper smwResponse = new MessageWrapper(
                        responseMsg.getEnvelope(), opName, true);
                smwResponse.setFault(responseMsg.getEnvelope().hasFault());
                // Set the SOAP Action
                smwResponse.setClassifier(mw.getClassifier());   //TODO Indika
                smwResponse.setClientID(mw.getClientID());
                smwResponse.setProperty(
                        ROADConstants.ROAD4WS_MW_SOAP_ACTION,
                        responseMsg.getSoapAction());
                // Set originated role
                //Set the correlation that of the request
                smwResponse.setCorrelationId(mw.getCorrelationId());
                smwResponse.setProperty(
                        ROADConstants.ROAD4WS_MW_IS_RESPONSE,
                        ROADConstants.ROAD4WS_MW_RESPONSE);
                smwResponse.setTaskId(mw.getTaskId());
                return smwResponse;
            }
        } catch (MessageDeliveryException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sendMessage(MessageWrapper messageWrapper) {
        // Get the epr from the Role and the operation
        String playerBinding = messageWrapper.getDestinationPlayerBinding().replaceAll("(\\r|\\n)", "").trim();
        if (log.isDebugEnabled()) {
            log.debug("player binding " + playerBinding);
        }
        String eprStr = playerBinding + "/" + messageWrapper.getOperationName();
        if (log.isDebugEnabled()) {
            log.debug("eprStr " + eprStr);
        }
        try {
            SOAPMessageSender.sendSOAPMsg(messageWrapper, eprStr, null, configurationContext, false, multiThreadedHttpConnectionManager);

        } catch (MessageDeliveryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void shutDown() {
        if (idleConnectionTimeoutThread != null) {
            idleConnectionTimeoutThread.shutdown();
        }
    }
}
