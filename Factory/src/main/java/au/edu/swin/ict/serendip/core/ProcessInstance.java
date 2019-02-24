package au.edu.swin.ict.serendip.core;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.DisabledRuleSet;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.VSNInstance;
import au.edu.swin.ict.road.regulator.ProcessMonitor;
import au.edu.swin.ict.road.regulator.ProcessMonitorInstance;
import au.edu.swin.ict.road.xml.bindings.ConstraintType;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.composition.view.ProcessView;
import au.edu.swin.ict.serendip.event.SerendipEventListener;
import au.edu.swin.ict.serendip.message.Message;
import au.edu.swin.ict.serendip.util.CompositionUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessInstance extends SerendipEventListener implements Cloneable {
    private static Logger logger = Logger.getLogger(ProcessInstance.class);
    public status currentStatus = status.active;
    private Map<String, DisabledRuleSet> disabledRuleSetMap = new HashMap<String, DisabledRuleSet>();
    private VSNInstance mgtState;
    private Lock processInstanceLock = new ReentrantLock();
    private Condition disableSetCreation = processInstanceLock.newCondition();
    private String processVersion;
    private SerendipEngine engine = null;
    private List<BehaviorTerm> btVec = new ArrayList<BehaviorTerm>();
    private List<ConstraintType> constraintsVec = new ArrayList<ConstraintType>();
    private Hashtable<String, Task> currentTasks = new Hashtable<String, Task>();
    private Hashtable<String, Task> completedTasks = new Hashtable<String, Task>();
    private ProcessDefinition parent;
    // private ProcessDefinitionType pDef = null;
    private String defId = null;
    private String pId;

    /**
     * A process instance that maintains the state of a running business process
     *
     * @param engine
     * @param defId
     */
    public ProcessInstance(SerendipEngine engine, String defId, Classifier classifier) {
        this.engine = engine;
        this.defId = defId;
        this.pId = classifier.getVsnId() + defId + engine.getUniqueId();//we increment the process ids
        setClassifier(classifier);
        this.mgtState = new VSNInstance(classifier);
    }

    public VSNInstance getMgtState() {
        return mgtState;
    }

    public Condition getDisableSetCreation() {
        return disableSetCreation;
    }

    public Lock getProcessInstanceLock() {
        return processInstanceLock;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public ProcessDefinition getParent() {
        return parent;
    }

    public void setParent(ProcessDefinition parent) {
        this.parent = parent;
        ProcessMonitor processMonitor = parent.getProcessMonitor();
        if (processMonitor != null) {
            ProcessMonitorInstance pmIns = new ProcessMonitorInstance(processMonitor);
            pmIns.setClassifier(getClassifier());
            engine.getEventCloud().subscribe(pmIns);
        }
    }

    public synchronized DisabledRuleSet getDisabledRuleSet(String roleId) {

        DisabledRuleSet disabledRuleSet = disabledRuleSetMap.get(roleId);
        if (disabledRuleSet == null) {
            disabledRuleSet = new DisabledRuleSet(roleId);
            disabledRuleSetMap.put(roleId, disabledRuleSet);
        }
        return disabledRuleSet;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public Object clone() {// Not complete.
        try {
            ProcessInstance cloned = (ProcessInstance) super.clone();
            //
            return cloned;
        } catch (CloneNotSupportedException e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * Subscribe to the event cloud so that when the CoT is met the process instance can release its resources.
     *
     * @throws SerendipException
     */
    public String initializeSubscriptions() throws SerendipException {
        // Listen for the condition of termination
        this.addEventPattern(this.engine.getComposition().getProcessDefinition(
                this.defId).getCoT().trim());
        this.engine.getEventCloud().subscribe(this);
        return this.pId;
    }

    /**
     * To retrieve the current view of the process instance.
     *
     * @return A process view
     * @throws SerendipException
     */
    public ProcessView getCurrentProcessView() throws SerendipException {
        return new ProcessView(this.defId + "(" + this.getId() + ")", this.btVec);
    }

    /**
     * Get all the constraints defined in the process definition
     *
     * @return
     */
    public ConstraintType[] getAllConstants() {
        Collection<ConstraintType> tempCollec = new ArrayList<ConstraintType>();

        ConstraintType[] pdCons = CompositionUtil.getAllConstriantsForPD(this.defId, true, this.engine.getComposition());
        tempCollec.addAll(this.constraintsVec);
        tempCollec.addAll(Arrays.asList(pdCons));
        return tempCollec.toArray(new ConstraintType[tempCollec.size()]);
    }

    /**
     * Add a constraint only for this instance
     *
     * @param cid
     * @param expression
     * @param enabled
     */
    public void addConstraint(String cid, String expression, boolean enabled) {
        ConstraintType cons = new ConstraintType();
        cons.setId(cid);
        cons.setExpression(expression);
        cons.setEnabled(enabled);
        this.constraintsVec.add(cons);
    }

    /**
     * When the CoT is met this will terminate the process instance. Self-destruction?
     */
    public void eventPatternMatched(String ep, Classifier classifier1) {
        System.out.println("Cvvvvvvv");
        this.engine.expireProcessInstanceOnBackground(pId);
    }

    public void terminateOnBackground() {
        this.engine.expireProcessInstanceOnBackground(pId);
    }

    public boolean canTerminate() {
//        return terminateFlag || currentTasks.isEmpty();
        return mgtState.getState().equals(ManagementState.STATE_QUIESCENCE);
    }

    public void terminate() {
        this.currentStatus = ProcessInstance.status.completed;
        //this.removeAllBehaviorTerms();//We temporarily disable this as we need to bakup the process instances
        this.engine.removeProcessInstance(this.getClassifier());
        this.engine.cleanMessages(pId);
        this.engine.getEventCloud().expireEvent(pId);
        this.engine.getEventCloud().expireEventListeners(pId);
        if (logger.isDebugEnabled()) {
            logger.debug("Remaining tasks : " + currentTasks.size());
            logger.debug("Successfully terminated the process instance " + this.pId);
        }
        if (engine.isInForLogEnable()) {
            this.engine.writeLogMessage("[PI-END]", "Remaining tasks : " + currentTasks.size() + this.pId);
            this.engine.writeLogMessage("[PI-END]", "Successfully terminated process instance " + this.pId);
        }
    }

    public void delete() {
        if (engine.isInForLogEnable()) {
            this.engine.writeLogMessage("[PI-DELETE]", "Successfully deleted process instance " + this.pId);
        }
        this.removeAllBehaviorTerms();
        // System.gc();
    }

    /**
     * To get the definition id
     *
     * @return
     */
    public String getDefinitionId() {
        return this.defId;
    }

    public SerendipEngine getEngine() {
        return engine;
    }

    public List<BehaviorTerm> getBtVec() {
        return btVec;
    }

    public BehaviorTerm getBehaviorTerm(String behaviorTermId) {
        for (int i = 0; i < this.btVec.size(); i++) {
            BehaviorTerm bt = this.btVec.get(i);
            if (bt.getId().equals(behaviorTermId)) {
                return bt;
            }
        }
        return null;
    }

    /**
     * Iterate thru current tasks that are active. A message should be recieved
     * when the task is active
     *
     * @param m
     * @return
     */
    public Task getCurrentTaskForMsg(Message m) {
        String mId = m.getId();

        // Scan thru current tasks. If the task has an out message for the above
        // name return the task
        Set<String> set = this.currentTasks.keySet();

        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String taskId = itr.next();
            Task task = this.currentTasks.get(taskId);

            if (task.getOutMessageId().equals(m.getId())) {
                return task;
            }
        }
        logger.error("Cannot find the task for the message" + m.getId());
        return null;
    }

    /**
     * Find a task by its id
     *
     * @param taskId
     * @return
     */
    public Task findTaskById(String taskId) {

        for (BehaviorTerm b : this.getBtVec()) {
            for (Task t : b.getAllTasks()) {
                if (t.getId().equals(taskId)) {
                    return t;

                }
            }
        }
        return null;
    }

    public List<Task> getAllTheTasks() {
        List<Task> allTasks = new ArrayList<Task>();

        for (BehaviorTerm b : this.getBtVec()) {
            allTasks.addAll(b.getAllTasks());
        }
        return allTasks;
    }

    public Hashtable<String, Task> getCurrentTasks() {
        return currentTasks;
    }

    public boolean isTaskInProgress(String taskId) {
        return currentTasks.containsKey(taskId);
    }

    public String getCurrentTasksAsString() {
        Set<String> set = this.currentTasks.keySet();
        String text = null;
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            text += itr.next() + " ";
        }
        return text;
    }

    public void removeFromCurrentTasks(String taskId) {
        this.currentTasks.remove(taskId);
    }

    public void addToCompletedTasks(Task t) {
        this.completedTasks.put(t.getId(), t);
    }

    public Hashtable<String, Task> getCompletedTasks() {

        return this.completedTasks;
    }

    // //Adaptation methods
    public boolean changeEventPattern(String behaviorTermId, String taskId,
                                      String newEventPattern) {

        BehaviorTerm bt = this.getBehaviorTerm(behaviorTermId);
        return bt.changeEventPattern(taskId, newEventPattern);
    }

    public boolean removeTask(String behaviorTermId, String taskId) {
        BehaviorTerm bt = this.getBehaviorTerm(behaviorTermId);
        return bt.removeTask(taskId);
    }

    public boolean addTask(String behaviorTermId, Task task) {
        BehaviorTerm bt = this.getBehaviorTerm(behaviorTermId);
        return bt.addTask(task);
    }

    public void addToCurrentTasks(Task task) {
        this.currentTasks.put(task.getId(), task);
    }

    public status getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean removeBehaviorTerm(String behaviorTermId) {
        for (int i = 0; i < this.btVec.size(); i++) {
            if (this.btVec.get(i).getId().equals(behaviorTermId)) {
                this.btVec.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeAllBehaviorTerms() {
        for (int i = 0; i < this.btVec.size(); i++) {
            this.btVec.remove(i);
        }
        return true;
    }

    public boolean addBehaviorTerm(BehaviorTerm bt) {
        this.btVec.add(bt);
        return true;
    }

    @Override
    public String getId() {
        // Unlike other EventListeners, process instance's pid=id
        return this.pId;
    }

    public boolean containsEvent(String eventId) {

        for (int i = 0; i < this.btVec.size(); i++) {
            if (this.btVec.get(i).containsEvent(eventId)) {
                return true;
            }
        }
        return false;
    }

    public BehaviorTerm getContainedBehavior(String taskId) {
        for (int i = 0; i < this.btVec.size(); i++) {
            if (this.btVec.get(i).containsTask(taskId)) {
                return this.btVec.get(i);
            }
        }
        return null;
    }

    //We specifically use lower case letters for enums to support the scripting. Change with care.
    public enum status {
        active, completed, abort, paused
    }

    public enum propertyAttribute {
        cot, cos
    }
}
