package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.composite.rules.events.contract.MessageReceivedEvent;
import au.edu.swin.ict.road.xml.bindings.PlayerBindingType;
import au.edu.swin.ict.road.xml.bindings.PropertyType;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.message.SOAPFactory;
import org.apache.axiom.soap.SOAPEnvelope;

/**
 * TODO
 */
public abstract class BasedActions {

    protected Composite composite;

    public BasedActions(Composite composite) {
        this.composite = composite;
    }

    public void publishEvent(String eventId, RoleServiceMessage msg) {
        try {
            composite.getSerendipEngine().getEventCloud().addEvent(new EventRecord(eventId, msg.getClassifier()));
        } catch (SerendipException e) {
            e.printStackTrace();
        }
    }

    public void publishEvent(String eventId, MessageReceivedEvent msg) {
        msg.triggerEvent(eventId);
    }

    public void publishEvent(String eventId, MessageReceivedEvent msg, String target) {
        msg.triggerEvent(eventId, target);
    }

    public void publishSNState(StateRecord stateRecord, String scope, RoleServiceMessage msg) {
        putStateRecord(stateRecord.getStateId(), scope, stateRecord, msg.getClassifier());
    }

    public void publishSNState(StateRecord stateRecord, String scope, MessageReceivedEvent msg) {
        putStateRecord(stateRecord.getStateId(), scope, stateRecord, msg.getMessageWrapper().getClassifier());
    }

    public StateRecord getSNState(String stateId, String scope, RoleServiceMessage msg) {
        return getStateRecord(stateId, scope, msg.getClassifier());
    }

    public StateRecord getSNState(String stateId, String scope, MessageReceivedEvent msg) {
        return getStateRecord(stateId, scope, msg.getMessageWrapper().getClassifier());
    }

    public void SendToOrganizer(Object message, String opName, Classifier classifier) {
        String orgBinding = composite.getSmcBinding().getOrganiserBinding();
        SMCDataExtractor dataExtractor = new SMCDataExtractor(composite.getSmcBinding());
        PlayerBindingType binding = dataExtractor.getServiceBinding(orgBinding);
        MessageWrapper mwRequest = new MessageWrapper(message, opName, false);
        mwRequest.setDestinationPlayerBinding(binding.getEndpoint());
        mwRequest.setClassifier(classifier);
        composite.getDefaultPushMessageListener().sendMessage(mwRequest);
    }

    public void SendToOrganizer(StateRecord stateRecord, String opName) {
        String orgBinding = composite.getSmcBinding().getOrganiserBinding();
        SMCDataExtractor dataExtractor = new SMCDataExtractor(composite.getSmcBinding());
        PlayerBindingType binding = dataExtractor.getServiceBinding(orgBinding);
        String SOAPVersion = "SOAP12";
        String namespace = "http://www.swin.edu.au/ict/road/smc";
        for (PropertyType propertyType : binding.getProperty()) {
            if ("SOAPVersion".equalsIgnoreCase(propertyType.getName())) {
                SOAPVersion = propertyType.getValue().trim();
            } else if ("namespace".equalsIgnoreCase(propertyType.getName())) {
                namespace = propertyType.getValue().trim();
            }
        }
        SOAPEnvelope soapEnvelope = SOAPFactory.createSOAPEnvelope(SOAPVersion, namespace, stateRecord, opName);
        MessageWrapper mwRequest = new MessageWrapper(soapEnvelope, opName, false);
        mwRequest.setDestinationPlayerBinding(binding.getEndpoint());
        composite.getDefaultPushMessageListener().sendMessage(mwRequest);
    }

    public void SendToOrganizer(StateRecord[] stateRecords, String opName) {
    }

    public void SendToOrganizer(EventRecord eventRecord, String opName) {
    }

    public void SendToOrganizer(EventRecord[] eventRecords, String opName) {
    }

    public void SendToOrganizer(StateRecord[] stateRecords, EventRecord[] eventRecords, String opName) {

    }

    private void putStateRecord(String stateId, String scope, StateRecord stateRecord, Classifier classifier) {
        if ("instance".equals(scope)) {
            composite.getServiceNetworkState().
                    getVsnState(classifier.getVsnId()).getProcessState(classifier.getProcessId()).
                    getProcessInstanceState(classifier.getProcessInsId()).putInCache(stateId, stateRecord);
        } else if ("process".equals(scope)) {
            composite.getServiceNetworkState().getVsnState(classifier.getVsnId()).
                    getProcessState(classifier.getProcessId()).putInCache(stateId, stateRecord);
        } else if ("vsn".equals(scope)) {
            composite.getServiceNetworkState().
                    getVsnState(classifier.getVsnId()).putInCache(stateId, stateRecord);
        } else if ("sn".equals(scope)) {
            composite.getServiceNetworkState().putInCache(stateId, stateRecord);
        }
    }

    private StateRecord getStateRecord(String stateId, String scope, Classifier classifier) {
        if ("instance".equals(scope)) {
            return composite.getServiceNetworkState().
                    getVsnState(classifier.getVsnId()).getProcessState(classifier.getProcessId()).
                    getProcessInstanceState(classifier.getProcessInsId()).retrieveFromCache(stateId);
        } else if ("process".equals(scope)) {
            return composite.getServiceNetworkState().getVsnState(classifier.getVsnId()).
                    getProcessState(classifier.getProcessId()).retrieveFromCache(stateId);
        } else if ("vsn".equals(scope)) {
            return composite.getServiceNetworkState().
                    getVsnState(classifier.getVsnId()).retrieveFromCache(stateId);
        } else if ("sn".equals(scope)) {
            return composite.getServiceNetworkState().retrieveFromCache(stateId);
        }
        return null;
    }

    public void TerminateVSNInstance(Classifier classifier) {
        EventRecord eventRecord = new EventRecord("eTerminated", classifier, ManagementState.STATE_ACTIVE);
        eventRecord.setPlace("MM");
        try {
            composite.getSerendipEngine().getEventCloud().addEvent(eventRecord);
        } catch (SerendipException e) {
            e.printStackTrace();
        }
        ProcessInstance processInstance =
                composite.getSerendipEngine().getProcessInstance(classifier);
        processInstance.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
        processInstance.terminateOnBackground();
    }

    public void ChangeVSNInstanceState(Classifier classifier, String state) {
        ProcessInstance processInstance =
                composite.getSerendipEngine().getProcessInstance(classifier);
        processInstance.getMgtState().setState(state);
    }

    public void SubscribeToVSNInstanceState(Classifier classifier, String place, String regulationType) {
        ProcessInstance processInstance =
                composite.getSerendipEngine().getProcessInstance(classifier);
        processInstance.getMgtState().subscribe(
                new VSNInstanceStateChangeListener(place, composite.getSerendipEngine().getEventCloud()));
    }
}
