package au.edu.swin.ict.road.composite.message.analyzer;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Term;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.xml.bindings.ResultMsgType;
import au.edu.swin.ict.road.xml.bindings.TaskType;
import org.apache.axiom.om.*;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is the default message analyzer implementation which makes use of XSLT
 * for conjunct and disjunct transformations.
 *
 * @author Saichander Kukunoor (saichanderreddy@gmail.com)
 */

public class XSLTAnalyzer implements MessageAnalyzer {

    // get the logger
    private static Logger log = Logger.getLogger(XSLTAnalyzer.class
                                                         .getName());
    public final static String MSG_ID_SEPERATOR = ".";
    public final static String MSG_ID_REQUEST = "Req";
    public final static String MSG_ID_RESPONSE = "Res";
    private static TransformerFactory transFact = TransformerFactory.newInstance();
    private static Map<String, Templates> cachedTemplatesMap = new ConcurrentHashMap<String, Templates>();
    private static final Lock lock = new ReentrantLock();
    private static XSLTAnalyzer instance = new XSLTAnalyzer();

    public static XSLTAnalyzer getInstance() {
        return instance;
    }

    private XSLTAnalyzer() {
    }

    /**
     * This method is used to merge multiple messages using the xslt
     * transformation provided by the TransformLogic object.
     *
     * @see au.edu.swin.ict.road.composite.message.analyzer.MessageAnalyzer#conjunct
     * (java.util.List,
     * au.edu.swin.ict.road.composite.message.analyzer.TransformLogic)
     */
    @Override
    public MessageWrapper conjunct(List<MessageWrapper> messages,
                                   TransformLogic tLogic) throws RuntimeException {
        MessageWrapper conjunctMessagewrapper = new MessageWrapper();
        // get the xslt file from TransformLogic logic and retrieve it from the
        // folder transformation in AXIS2_HOME
//		String xsltPath = System.getenv("AXIS2_HOME") + "/transformation/"
//				+ tLogic.getXsltFilePath().get(0);
        String transDir = tLogic.getRole().getComposite().getTransDir();
        String xsltFile = tLogic.getXsltFile();
        if (xsltFile == null) {
            xsltFile = tLogic.getTask().getSrcMsgs().getTransformation();
        }
        String xsltPath = transDir + xsltFile;
        // create TransformerFactory and then the Transfomer to perform xslt
        // transformation

        Templates cachedTemplates = cachedTemplatesMap.get(xsltPath);
        if (cachedTemplates == null) {
            try {
                lock.lock();      //TODO
                cachedTemplates = cachedTemplatesMap.get(xsltPath);
                if (cachedTemplates == null) {
                    try {
                        cachedTemplates = transFact.newTemplates(new StreamSource(xsltPath));
                    } catch (TransformerConfigurationException e1) {
                        throw new RuntimeException(e1);
                    }
                    cachedTemplatesMap.put(xsltPath, cachedTemplates);
                }
            } finally {
                lock.unlock();
            }
        }
        Transformer transformer = null;
        try {
            transformer = cachedTemplates.newTransformer();
        } catch (TransformerConfigurationException e1) {
            throw new RuntimeException(e1);
        }

        // log.info("XSLT analyzer for " + tLogic.getRole().getId() + ". No of messages ::: " + messages.size());
        // iterate over the list of messages passed to this method
        for (MessageWrapper message : messages) {
            // get the SOAP message, transform it to a DOM document and set
            // it as an attribute to the transformer.
            // This attribute will be used to reference this message in the
            // xslt.
            String xsltParamName = message.getMessageId();
            SOAPEnvelopeImpl env = (SOAPEnvelopeImpl) message.getMessage();

            Document document = ((Element) ElementHelper.importOMElement(env,
                                                                         OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory())).getOwnerDocument();
//
            transformer.setParameter(xsltParamName, document);           //TODO use OMSOurce(evn) with XPath working
        }
//            Writer sWriter = new StringWriter();
        // once all messages are set as attributes to the transormer
        // perform the xslt transformation
        OMDocument document = OMAbstractFactory.getOMFactory().createOMDocument();
        SAXResult omResult = document.getSAXResult();
        try {
            transformer.transform(new StreamSource(), omResult);
        } catch (Exception e1) {
            String msg = "Error transforming a message with xslt file : " +
                         tLogic.getTask().getSrcMsgs().getTransformation() + " :: " + e1.getMessage();
            log.error(msg);
            throw new RuntimeException(msg, e1);
        }


        OMElement omElement = document.getOMDocumentElement();
        XMLStreamReader reader = null;

        // If the OM element is not attached to a parser (builder), then the OM
        // is built and you cannot ask for XMLStreamReaderWithoutCaching.
        // This is probably a bug in OM.  You should be able to ask the OM whether
        // caching is supported.
        if (omElement.getBuilder() == null) {
            reader = omElement.getXMLStreamReader();
        } else {
            reader = omElement.getXMLStreamReaderWithoutCaching();
        }

        SOAPEnvelope conjunctSoapEnv = OMXMLBuilderFactory.createStAXSOAPModelBuilder(reader).getSOAPEnvelope();
//			conjunctSoapEnv.setNamespace(new OMNamespaceImpl("http://www.w3.org/2003/05/soap-envelope", "soapenv"));
//			log.info(">>>>>>>>>>> conjunct soap envelope ::: " + conjunctSoapEnv);
        // set the SOAP message and other required details to the final
        // message wrapper
        String taskId = tLogic.getTask().getId();
        conjunctMessagewrapper.setMessage(conjunctSoapEnv);
        conjunctMessagewrapper.setTaskId(taskId);
        conjunctMessagewrapper.setOperationName(tLogic.getOperationName());
        conjunctMessagewrapper.setDestinationPlayerBinding(tLogic.getRole()
                                                                 .getPlayerBinding());
        conjunctMessagewrapper.setMessageType(tLogic.getDeliveryType());
        conjunctMessagewrapper.setMessageId(taskId + ".OutMsg");
        conjunctMessagewrapper.setResponse(tLogic.isResponse());
        conjunctMessagewrapper.setSyncType(tLogic.getSyncType());


        return conjunctMessagewrapper;
    }

    /**
     * This method is used to split a single message passed to it into multiple
     * messages using the xslt provided by TransformLogic object.
     *
     * @see au.edu.swin.ict.road.composite.message.analyzer.MessageAnalyzer#disjunct
     * (au.edu.swin.ict.road.composite.message.MessageWrapper,
     * au.edu.swin.ict.road.composite.message.analyzer.TransformLogic)
     */
    @Override
    public List<MessageWrapper> disjunct(MessageWrapper msgToDisjunct,
                                         final TransformLogic tLogic) throws RuntimeException {
        // get the conposite, role and the list of xslts used for the
        // transformations
        Composite smc = tLogic.getRole().getComposite();
        List<ResultMsgType> resultMsgTypes = tLogic.getTask().getResultMsgs().getResultMsg();
        Role role = tLogic.getRole();
        // get the list of tasks in this role
        TaskType currentTask = tLogic.getTask();
        // the list which will hold all the messages generated from the
        // transformations
//        List<MessageWrapper> messages = new ArrayList<MessageWrapper>();
        // get the SOAP message from the message passed to this method
        SOAPEnvelopeImpl env = (SOAPEnvelopeImpl) msgToDisjunct.getMessage();
//        try {
//        OMSource omSource = new OMSource(env);   //TODO   XPath does not work
        Document document = ((Element) ElementHelper.importOMElement(env,
                                                                     OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory())).getOwnerDocument();
        for (ResultMsgType resultMsg : resultMsgTypes) {
            String xsltPathOri = resultMsg.getTransformation();
            if (xsltPathOri == null || "".equals(xsltPathOri)) {
                role.putSchedulerQueueMessage(msgToDisjunct.cloneMessageWrapper(role, resultMsg));
                continue;
            }
            // the transformation result would be set to this message object
            MessageWrapper aMessageWrapper = new MessageWrapper();
            // create the transformer using the one of the xslts. It
            // retrives the
            // xslt from the transformation folder in AXIS2_HOME
            String transDir = tLogic.getRole().getComposite().getTransDir();
            String xsltPath = transDir + xsltPathOri;
            Templates cachedTemplates = cachedTemplatesMap.get(xsltPath);
            if (cachedTemplates == null) {
                try {
                    lock.lock();      //TODO
                    cachedTemplates = cachedTemplatesMap.get(xsltPath);
                    if (cachedTemplates == null) {
                        try {
                            cachedTemplates = transFact.newTemplates(new StreamSource(xsltPath));
                        } catch (TransformerConfigurationException e) {
                            throw new RuntimeException(e);
                        }
                        cachedTemplatesMap.put(xsltPath, cachedTemplates);
                    }
                } finally {
                    lock.unlock();
                }
            }
            Transformer transformer = null;
            try {
                transformer = cachedTemplates.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            }
            transformer.setParameter(currentTask.getId() + ".doneMsg", document);
            OMDocument document2 = OMAbstractFactory.getOMFactory().createOMDocument();
            SAXResult omResult = document2.getSAXResult();
            try {
                transformer
                        .transform(new StreamSource(), omResult);
            } catch (Exception e) {
                String msg = "Error transforming a message with xslt file : " +
                             tLogic.getTask().getSrcMsgs().getTransformation() + " :: " + e.getMessage();
                log.error(msg);
                throw new RuntimeException(msg, e);
            }

            XMLStreamReader reader;

            // If the OM element is not attached to a parser (builder), then the OM
            // is built and you cannot ask for XMLStreamReaderWithoutCaching.
            // This is probably a bug in OM.  You should be able to ask the OM whether
            // caching is supported.
            OMElement omElement = document2.getOMDocumentElement();
            if (omElement.getBuilder() == null) {
                reader = omElement.getXMLStreamReader();
            } else {
                reader = omElement.getXMLStreamReaderWithoutCaching();
            }
            SOAPEnvelope disjunctSoapEnv = OMXMLBuilderFactory.createStAXSOAPModelBuilder(reader).getSOAPEnvelope();
//                SOAPEnvelope disjunctSoapEnv = (SOAPEnvelope) omResult.getRootElement();

//				log.info("Disjunct soap envelope: " + disjunctSoapEnv);
            aMessageWrapper.setMessage(disjunctSoapEnv);
            aMessageWrapper.setOriginRole(role);

            String contractId = resultMsg.getContractId();
            String interactiveTermId = resultMsg.getTermId();
            boolean isResponse = resultMsg.isIsResponse();

            // get the destination contract details using the contract id
            // and set it to the result message wrapper
            Contract contract = smc.getContractMap().get(contractId);
            aMessageWrapper.setDestinationContract(contract);
            // get the operation name using the termId and set it to the
            // result message wrapper
            Term term = contract.getTermById(interactiveTermId);
            if (null == term) {
                throw new RuntimeException("Cannot find term " + interactiveTermId + " in contract " + contract.getId());
            }
            aMessageWrapper.setOperationName(term.getOperation().getName());
            // set the response flag
            aMessageWrapper.setResponse(isResponse);
            // create the message id and set it to the result message
            // wrapper
            String msgId = contractId + MSG_ID_SEPERATOR
                           + interactiveTermId + MSG_ID_SEPERATOR;
            msgId += isResponse ? MSG_ID_RESPONSE : MSG_ID_REQUEST;
            aMessageWrapper.setMessageId(msgId);

            //Set the correlation id that of the message subjected to disjunct
            if (msgToDisjunct.getCorrelationId() != null) {
                aMessageWrapper.setCorrelationId(msgToDisjunct.getCorrelationId());
            }
            aMessageWrapper.setClassifier(msgToDisjunct.getClassifier());    //TODO INDIKA
            aMessageWrapper.setClientID(msgToDisjunct.getClientID());
            // add it to the list of result messages
            role.putSchedulerQueueMessage(aMessageWrapper);
//                messages.add(aMessageWrapper);
        }


        return null;
    }

    @Override
    public MessageWrapper transform(MessageWrapper oldMessage, final TransformLogic tLogic) throws RuntimeException {
        Role role = tLogic.getRole();
        ResultMsgType resultMsg = tLogic.getResultMsgType();
        MessageWrapper newMW;
        if (resultMsg != null) {
            newMW = oldMessage.cloneMessageWrapper(role, resultMsg);
        } else {
            newMW = oldMessage.cloneMessageWrapper(role, tLogic);
        }
        // get the list of tasks in this role
        SOAPEnvelopeImpl env = (SOAPEnvelopeImpl) oldMessage.getMessage();
        Document document = ((Element) ElementHelper.importOMElement(env,
                                                                     OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory())).getOwnerDocument();
        String xsltPathOri = tLogic.getXsltFile();
        if (xsltPathOri == null || "".equals(xsltPathOri)) {
            return newMW;
        }

        String transDir = role.getComposite().getTransDir();
        String xsltPath = transDir + xsltPathOri;
        Templates cachedTemplates = cachedTemplatesMap.get(xsltPath);
        if (cachedTemplates == null) {
            try {
                lock.lock();      //TODO
                cachedTemplates = cachedTemplatesMap.get(xsltPath);
                if (cachedTemplates == null) {
                    try {
                        cachedTemplates = transFact.newTemplates(new StreamSource(xsltPath));
                    } catch (TransformerConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                    cachedTemplatesMap.put(xsltPath, cachedTemplates);
                }
            } finally {
                lock.unlock();
            }
        }
        Transformer transformer = null;
        try {
            transformer = cachedTemplates.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setParameter(oldMessage.getTaskId() + ".doneMsg", document);
        OMDocument document2 = OMAbstractFactory.getOMFactory().createOMDocument();
        SAXResult omResult = document2.getSAXResult();
        try {
            transformer
                    .transform(new StreamSource(), omResult);
        } catch (Exception e) {
            String msg = "Error transforming a message with xslt file : " +
                         tLogic.getTask().getSrcMsgs().getTransformation() + " :: " + e.getMessage();
            log.error(msg);
            throw new RuntimeException(msg, e);
        }

        XMLStreamReader reader;

        // If the OM element is not attached to a parser (builder), then the OM
        // is built and you cannot ask for XMLStreamReaderWithoutCaching.
        // This is probably a bug in OM.  You should be able to ask the OM whether
        // caching is supported.
        OMElement omElement = document2.getOMDocumentElement();
        if (omElement.getBuilder() == null) {
            reader = omElement.getXMLStreamReader();
        } else {
            reader = omElement.getXMLStreamReaderWithoutCaching();
        }
        SOAPEnvelope disjunctSoapEnv = OMXMLBuilderFactory.createStAXSOAPModelBuilder(reader).getSOAPEnvelope();
        newMW.setMessage(disjunctSoapEnv);
        return newMW;
    }

    public static InputStream getStreamSource(Object o) {

        if (o == null) {
            throw new RuntimeException("Cannot convert null to a StreamSource");

        } else if (o instanceof OMElement) {
            OMElement omElement = (OMElement) o;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                omElement.serialize(baos);
                return new ByteArrayInputStream(baos.toByteArray());
            } catch (XMLStreamException e) {
                throw new RuntimeException("Error converting to a StreamSource", e);
            }

        } else if (o instanceof OMText) {
            DataHandler dataHandler = (DataHandler) ((OMText) o).getDataHandler();
            if (dataHandler != null) {
                try {
                    return dataHandler.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException("Error in reading content as a stream ");
                }
            }
        } else {

            throw new RuntimeException("Cannot convert object to a StreamSource");
        }
        return null;
    }
}
