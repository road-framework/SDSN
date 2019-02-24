package au.edu.swin.ict.serendip.core;

/**
 * TODO:
 * 1. Commandline interface for the Process Instance view
 */

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.composition.Composition;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.core.log.LogWriter;
import au.edu.swin.ict.serendip.event.EventCloud;
import au.edu.swin.ict.serendip.event.EventEngineSubscriber;
import au.edu.swin.ict.serendip.parser.XMLCompositionParser;
import au.edu.swin.ict.serendip.verficiation.SerendipVerificationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SerendipEngine {
    private static Logger logger = Logger.getLogger(SerendipEngine.class);
    private EventCloud eventCloud = null;
    private XMLCompositionParser parser = null;
    private Map<String, VSNDefinition> processes = new HashMap<String, VSNDefinition>();
    // Engine maintains its own list for living process instances
    // Engine maintain a list of roles that need to be notified when the tasks
    // become doable
    private Hashtable<String, EventEngineSubscriber> subscribers = new Hashtable<String, EventEngineSubscriber>();
    private AtomicInteger instanceIdNum = new AtomicInteger(11);
    private ModelProviderFactory modelFactory = null;
    // TODO not in use?

    private Composition composition = null;
    private LogWriter logwriter = null;
//    private SerendipOrganizer serOrg = null;

    /**
     * @param composite the road composite
     * @throws SerendipVerificationException
     */
    public SerendipEngine(Composite composite) throws SerendipException {
        logger.info("Creating the synchronization engine");
        this.composition = new Composition(composite);
        //Create the event cloud
        this.eventCloud = new EventCloud(composite, this, composite.getEventCloudPool(), composite.getTimeOutTimer());
        // Create ModelProviderFactory
        this.modelFactory = new ModelProviderFactory(this);
//        this.serOrg = new SerendipOrganizerImpl(this, composite);

        //Then we verify the models
//		try {
//			this.modelFactory.verifyModels();
//		} catch (SerendipVerificationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new SerendipException(e.getMessage());
//		}
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    /**
     * Start a process instance of a given process definition Id
     *
     * @param defId
     * @throws SerendipException
     */
    public ProcessInstance startProcessInstance(Classifier classifier, String defId)
            throws SerendipException {

        ProcessInstance pi = this.modelFactory.getNewProcessInstance(classifier, defId);
        pi.initializeSubscriptions();
        pi.setCurrentStatus(ProcessInstance.status.active);
        VSNDefinition group = processes.get(classifier.getVsnId());
        group.getProcessDefinition(classifier.getProcessId()).addProcessInstance(pi);
        if (isInForLogEnable()) {
            this.writeLogMessage("PI", "Starting a new process instance, pid:" + pi.getId());
        }
        return pi;
    }

    public ProcessInstance startProcessInstanceV2(Classifier classifier, String defId)
            throws SerendipException {

        ProcessInstance pi = this.modelFactory.getNewProcessInstanceV2(classifier, defId);
        VSNDefinition group = processes.get(classifier.getVsnId());
        ProcessDefinition processDefinition = group.getProcessDefinition(classifier.getProcessId());
        boolean success = processDefinition.addProcessInstance(pi);
        if (success) {
            classifier.setProcessInsId(pi.getId());
//            System.out.println("Starting a new process instance, pid:" + pi.getId());
            if (isInForLogEnable()) {
                this.writeLogMessage("PI", "Starting a new process instance, pid:" + pi.getId());
            }
            return pi;
        } else {
            System.out.println("Unable to state a new process instance for the process " +
                    defId + " as it is " + processDefinition.getMgtState().getState());
            return null;
        }
    }

    public void replaceProcessInstance(ProcessInstance newPi, Classifier classifier) {
        ProcessInstance oldPi = this.getProcessInstance(classifier);
        this.removeProcessInstance(oldPi.getClassifier());
        newPi.setpId(classifier.getProcessInsId());
        newPi.setClassifier(oldPi.getClassifier());
        VSNDefinition group = processes.get(classifier.getVsnId());
        group.getProcessDefinition(classifier.getProcessId()).addProcessInstance(newPi);
        newPi.setCurrentStatus(ProcessInstance.status.active);//May be redundant but no harm
    }

    public void pauseAllProcessInstances() {

//		for(ProcessInstance pi: this.processInstanceCollection.values()){
//			pi.currentStatus = ProcessInstance.status.PAUSED;
//		}
//        this.serOrg.updateStateofAllProcessInstances(ProcessInstance.status.paused.toString());
        //this.writeLogMessage("PI", "Paused all instances");
    }

    public void resumeAllProcessInstances() {
        if (isInForLogEnable()) {
            this.writeLogMessage("PI", "Resume all instances");
        }
//		for(ProcessInstance pi: this.processInstanceCollection.values()){
//			pi.currentStatus = ProcessInstance.status.ACTIVE;
//		}
//        this.serOrg.updateStateofAllProcessInstances(ProcessInstance.status.active.toString());
    }

    public String startProcessForEvent(Classifier classifier, EventRecord e) throws SerendipException {
        String pId = e.getClassifier().getProcessInsId();
        if ((null == pId) || (pId.equals("")) || (pId.equals("null"))) {

            //This is an event without a process instance.
            //We need to check if this is a process pre-condition event for PD
            String defId = this.composition.getPDforCoS(classifier, e.getEventId());
            if (null == defId) {
                throw new SerendipException("Event " + e.getEventId() + " is not associated with a pid. Event is not a pre-condition for a new instance too");
            }
            //If it is then we create a new process instance
            ProcessInstance pi = this.startProcessInstance(classifier, defId);
            //Allocate the pid of the instance with the event. So that this event belong to the created process instance
            e.getClassifier().setProcessInsId(pi.getId());

            return pi.getId();

        } else {
            //event has a pid. So use the same pid.
            return e.getClassifier().getProcessInsId();
        }
    }

    /**
     * This is the interface for external components to add new events to the engine.
     * The events added will be subjected to following operations.
     * 1. If the event DOES NOT contain a pid, the event can 'potentially' be an initial event.
     * 2. The engine check if any of the process definitions have e as the CoS (Condition of Start)
     * 3. If NO, an exception is thrown
     * 4. If YES, then a new process is enacted based on the event. And the event get associated with the new pid.
     * 5. If the event DOES contain a pid, the event is simply passed to the event cloud
     *
     * @param e
     * @return the pid (process instance id)
     * @throws SerendipException
     */
    public String addEvent(EventRecord e) throws SerendipException {
        if (isInForLogEnable()) {
            logger.info("Event fired (" + e.getEventId() + "," + e.getClassifier().getProcessInsId() + ")");
            this.writeLogMessage("EVENT", "Fired " + e.getEventId() + "," + e.getClassifier().getProcessInsId());
        }
        //If there is no pid associated with the event record
//		if((null == e.getPid()) || (e.getPid().equals(""))|| (e.getPid().equals("null")) ){
//			
//			//This is an event without a process instance. 
//			//We need to check if this is a process pre-condition event for PD
//			String defId = this.composition.getPDforCoS(e.getEventId());
//			if(null == defId){
//				throw new SerendipException("Event "+e.getEventId()+" is not associated with a pid. Event is not a pre-condition for a new instance too");
//			}
//			//If it is then we create a new process instance
//			ProcessInstance pi = this.startProcessInstance(defId);
//			logger.info("Created a new process instance "+pi.getPId());
//			//Allocate the pid of the instance with the event. So that this event belong to the created process instance
//			e.setPid(pi.getId());
//			//Drop the event to the event cloud
//			this.eventCloud.addEvent(e);
//			//TESTING
//			//this.eventCloud.printEventListerners();
//		}else{//the pid is present -> An event targeting an existing process instance.
//			//Simply drop the event to the event cloud
//			
//		}
        this.eventCloud.addEvent(e);
        return e.getClassifier().getProcessInsId();
    }

    public void expireEvent(String eventId, String pId) {
        this.eventCloud.expireEvent(eventId, pId);
    }

    public void expireEvents(String pId) {
        this.eventCloud.expireEvent(pId);
    }

    public void expireProcessInstanceOnBackground(String pId) {
        this.eventCloud.expireProcessInstanceOnBackground(pId);
    }

    // Getters and Setters
    public XMLCompositionParser getParser() {
        return parser;
    }

    public EventCloud getEventCloud() {
        return eventCloud;
    }

    /**
     * Initialize the logger for logging
     */
    private void initLogger() {
        logger.setLevel(Level.DEBUG);
    }

    public void printEventCloud() {
        this.eventCloud.printEventListerners();
    }

    //	public void dropMessage(Message m) throws SerendipException {
//		// logger.debug("Dropping message "+m.getId() +" "+m.getPid());
//		String msgPid = m.getPid();// Need a more sophisticated method to
//									// identify the process id
//		ProcessInstance pInstance = this.processInstanceCollection.get(msgPid);
//		//pInstance.interpretMessage(m);
//	}
    public Hashtable<String, EventEngineSubscriber> getSubscribers() {
        return subscribers;
    }

    public void subscribe(EventEngineSubscriber subscriber) {
        //this.writeLogMessage("[INIT]", "Role "+subscriber.getId()+" is subscribed to engine");
        logger.info(subscriber.getId() + " subscribed to the engine");
        this.subscribers.put(subscriber.getId(), subscriber);
    }

    /**
     * Checkout existing instance model. This model can be altered at runtime to
     * deviate it to accommodate the runtime requirements. See the API for more
     * details on how to change. Events may be added accordingly.
     *
     * @param classifier
     * @return
     */
    public ProcessInstance getProcessInstance(Classifier classifier) {
        VSNDefinition group = processes.get(classifier.getVsnId());
        ProcessInstance pi =
                group.getProcessDefinition(classifier.getProcessId()).getProcessInstance(
                        classifier.getProcessInsId());
        if (null == pi) {
            pi = group.getProcessDefinition(
                    classifier.getProcessId()).getCompletedProcessInstance(classifier.getProcessInsId());
        }
        if (null == pi) {
            logger.error("No such process instance with pId=" + classifier.getProcessInsId());
        }
        return pi;
    }

    public void removeProcessInstance(Classifier classifier) {
        VSNDefinition group = processes.get(classifier.getVsnId());
        ProcessInstance piBkup = group.getProcessDefinition(
                classifier.getProcessId()).removeProcessInstance(classifier.getProcessInsId());
        if (null == piBkup) {
//            this.completedInstanceCollection.put(piBkup.getPId(), piBkup);
//        } else {
            //error
            logger.error("No such Live process instance. May be already aborted=" + classifier.getProcessInsId());
        }
    }

    public Map<String, ProcessInstance> getLiveProcessInstances() {
        Map<String, ProcessInstance> instanceMap = new HashMap<String, ProcessInstance>();
        for (VSNDefinition group : processes.values()) {
            for (ProcessDefinition def : group.getAllProcessDefinitions()) {
                for (ProcessInstance pi : def.getAllProcessInstances()) {
                    if (pi.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                        instanceMap.put(pi.getId(), pi);
                    }
                }
            }
        }
        return instanceMap;
    }

    public Map<String, ProcessInstance> getLiveProcessInstances(String vsnId, String processId) {
        VSNDefinition vsnDefinition = getProcessDefinitionGroup(vsnId);
        ProcessDefinition def = vsnDefinition.getProcessDefinition(processId);
        return def.getAllLiveInstances();

    }

    public List<String> getLiveProcessInstancesIds() {
        List<String> instanceMap = new ArrayList<String>();
        for (VSNDefinition group : processes.values()) {
            for (ProcessDefinition def : group.getAllProcessDefinitions()) {
                for (ProcessInstance pi : def.getAllProcessInstances()) {
                    if (pi.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                        instanceMap.add(pi.getId());
                    }
                }
            }
        }
        return instanceMap;
    }

    public List<String> getLiveProcessInstancesIds(String vsnId, String processId) {

        VSNDefinition vsnDefinition = getProcessDefinitionGroup(vsnId);
        ProcessDefinition def = vsnDefinition.getProcessDefinition(processId);
        return def.getAllLiveInstanceIds();
    }

    public Map<String, ProcessInstance> getCompletedProcessInstances() {
        Map<String, ProcessInstance> instanceMap = new HashMap<String, ProcessInstance>();
        for (VSNDefinition group : processes.values()) {
            for (ProcessDefinition def : group.getAllProcessDefinitions()) {
                for (ProcessInstance pi : def.getAllCompletedProcessInstances()) {
                    if (pi.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                        instanceMap.put(pi.getId(), pi);
                    }
                }
            }
        }
        return instanceMap;
    }

    public String getCompositionName() {
        return this.composition.getComposite().getName();
    }

    public ModelProviderFactory getModelFactory() {
        return modelFactory;
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // PI level adaptation API

    public boolean removeTask(String pId, String behaviorTermId, String taskId) {
        ProcessInstance pi = getProcessInstanceByInsId(pId);
        if (null == pi) {
            return false;
        }
        return pi.removeTask(behaviorTermId, taskId);

    }

    public boolean addTask(String pId, String behaviorTermId, Task task) {
        ProcessInstance pi = getProcessInstanceByInsId(pId);
        if (null == pi) {
            return false;
        }
        return pi.addTask(behaviorTermId, task);
    }

    public boolean removeBehaviorTerm(String pId, String behaviorTermId) {
        ProcessInstance pi = getProcessInstanceByInsId(pId);
        if (null == pi) {
            return false;
        }
        return pi.removeBehaviorTerm(behaviorTermId);
    }

    public boolean addBehaviorTerm(String pId, BehaviorTerm bt) {
        ProcessInstance pi = getProcessInstanceByInsId(pId);
        if (null == pi) {
            return false;
        }
        return pi.addBehaviorTerm(bt);
    }

    //logging
    public void subscribeLogWriter(LogWriter writer) {
        this.logwriter = writer;
    }

    public void writeLogMessage(String context, String message) {
        //TODO: We might need to write to multiple logwriters in the future
        if (null != this.logwriter) {
            this.logwriter.writeLog(context, message);
        } else {
            //logger.error("LogWriter is null");
            if (logger.isInfoEnabled()) {
                logger.info("[" + context + "]" + message);
            }
        }
    }

//    public SerendipOrganizer getSerendipOrgenizer() {
//        return this.serOrg;
//    }

    public String getUniqueId() {
        return String.valueOf(instanceIdNum.getAndIncrement());
    }

    public boolean isInForLogEnable() {
        return logger.isInfoEnabled();
    }

    public void cleanMessages(String pid) {
        for (Role role : composition.getComposite().getRoleMap().values()) {
            role.clean(pid);
        }
    }

    public void addProcessDefinitionGroup(VSNDefinition pdGroup) {
        processes.put(pdGroup.getId(), pdGroup);
    }

    public VSNDefinition removeProcessDefinitionGroup(String id) {
        return processes.remove(id);
    }

    public VSNDefinition getProcessDefinitionGroup(String id) {
        return processes.get(id);
    }

    public boolean containsProcessDefinitionGroup(String id) {
        return processes.containsKey(id);
    }

    public Collection<VSNDefinition> getAllProcessDefinitionGroups() {
        return processes.values();
    }

    public ProcessInstance getProcessInstanceByInsId(String pId) {
        for (VSNDefinition group : processes.values()) {
            for (ProcessDefinition def : group.getAllProcessDefinitions()) {
                for (ProcessInstance pi : def.getAllProcessInstances()) {
                    if (pi.getId().equals(pId)) {
                        return pi;
                    }
                }
            }
        }
        return null;
    }

    public void removeProcessInstanceById(String pid) {
        ProcessInstance instance = getProcessInstanceByInsId(pid);
        instance.getParent().removeProcessInstance(pid);
    }
}
