package au.edu.swin.ict.serendip.event;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.regulation.VSNInstanceStateChangeListener;
import au.edu.swin.ict.road.composite.regulation.sglobal.GlobalKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.sglobal.GlobalRegTable;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynEvents;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationRegTable;
import au.edu.swin.ict.road.composite.utills.ProcessEventListener;
import au.edu.swin.ict.road.composite.utills.ROADProperties;
import au.edu.swin.ict.road.composite.utills.ROADThreadPool;
import au.edu.swin.ict.road.composite.utills.RoadCleaner;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.grammar.ep.EventPatternRecognizer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The event cloud.
 * A simple implementation to check  event patterns
 *
 * @author Malinda
 */
public class EventCloud {
    public static final int DEFAULT_MEX_TIMEOUT = 2 * 60 * 1000;
    private static Logger log = Logger.getLogger(EventCloud.class);
    private final ConcurrentHashMap<String, ProcessEventRecord> eventRecords = new ConcurrentHashMap<String, ProcessEventRecord>();
    private final ConcurrentHashMap<String, ProcessEventListener> eventListeners =
            new ConcurrentHashMap<String, ProcessEventListener>();
    //    private final LinkedBlockingQueue<EventRecord> newEventsQ = new LinkedBlockingQueue<EventRecord>();
    private SerendipEngine engine = null;
    //    private boolean sleep = true;
//    private boolean terminated = false;
    private ROADThreadPool eventCloudPool;
    private RoadCleaner roadCleaner;
    private Composite composite;

    public EventCloud(Composite composite, SerendipEngine engine, ROADThreadPool eventCloudPool, Timer timer) {
        roadCleaner = new RoadCleaner(engine);
        this.engine = engine;
        this.eventCloudPool = eventCloudPool;
        this.composite = composite;
        timer.scheduleAtFixedRate(roadCleaner, 0,
                Integer.parseInt(ROADProperties.getInstance().getProperty("cleaner.interval", "60000")));
    }

    public Collection<ProcessEventRecord> getEventRecords() {
        return eventRecords.values();
    }

    public Collection<EventRecord> getEventRecordsForPid(String pid) {
        ProcessEventRecord processEventRecord = eventRecords.get(pid);
        if (processEventRecord != null) {
            return processEventRecord.getEventRecords();
        } else {
            return new ArrayList<EventRecord>();
        }
    }

    public void printEventListerners() {
//        for (SerendipEventListener sel : this.eventListeners.values()) {
//            if (log.isDebugEnabled()) {
//                log.debug(sel.getId() + " is subscribed to " + sel.eventPattern);
//            }
//        }
    }

    public Collection<ProcessEventListener> getCurrentSubscriptions() {
        return this.eventListeners.values();
    }

    /**
     * Event record is a combination of event id and a process instnce id.
     * Event id should be surrounded by []. e.g, [MyEvent]
     *
     * @param e
     * @throws SerendipException
     */
    public void addEvent(final EventRecord e) throws SerendipException {
//        newEventsQ.add(e);
        String pid = e.getClassifier().getProcessInsId();
        ProcessEventRecord processEventRecord = eventRecords.get(pid);
        if (processEventRecord == null) {
            synchronized (eventRecords) {
                processEventRecord = eventRecords.get(pid);
                if (processEventRecord == null) {
                    processEventRecord = new ProcessEventRecord();
                    eventRecords.put(pid, processEventRecord);
                }
            }
        }
        processEventRecord.addEventRecord(e);
//        sleep = false;
        if (log.isDebugEnabled()) {
            log.debug("Added event  " + e.getEventId() + " for pid=" + pid);
        }
        newFireGlobal(e);
        newFire(e);
    }

    private void newFire(EventRecord e) {
        Classifier classifier = e.getClassifier();
        String vsnID = classifier.getVsnId();
        String vsnInstanceId = classifier.getProcessInsId();
        String processID = classifier.getProcessId();
        Role destinationRole = composite.getRole(e.getPlace());
        String roleID = destinationRole.getId();
        SynchronizationRegTable synRegTable = destinationRole.getSynchronizationRegTable();
        ProcessInstance processInstance = engine.getProcessInstance(classifier);
        if (processInstance == null) {
            return;
        }
        VSNInstance vsnState = processInstance.getMgtState();
        if (!vsnState.getState().equals(ManagementState.STATE_ACTIVE)) {
            vsnState.subscribe(new VSNInstanceStateChangeListener(roleID, this));
        } else {
            DisabledRuleSet disabledRuleSet = processInstance.getDisabledRuleSet(roleID);
            if (synRegTable != null) {
                List<RegulationUnitKey> vsnRegTableEntry = synRegTable.getVSNTableEntry(vsnID + "_" + processID);
                if (vsnRegTableEntry != null) {
                    List<RegulationRuleSet> ruleSets = new ArrayList<RegulationRuleSet>();
                    for (RegulationUnitKey regUnitId : vsnRegTableEntry) {
                        if (vsnInstanceId != null) {
                            if (regUnitId.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)
                                    && !regUnitId.isExcluded(vsnInstanceId)) {
                                ruleSets.add(synRegTable.getRegulationRuleSet(regUnitId.getUnitId()));
                            } else if (regUnitId.getMgtState().getState().equals(ManagementState.STATE_PASSIVE)
                                    && regUnitId.isIncluded(vsnInstanceId)) {
                                ruleSets.add(synRegTable.getRegulationRuleSet(regUnitId.getUnitId()));
                            }
                        } else {
                            ruleSets.add(synRegTable.getRegulationRuleSet(regUnitId.getUnitId()));
                        }
                    }
                    try {
                        SynchronizationKnowledgebase synRules = destinationRole.getSynRules();
                        SynEvents synEvents = new SynEvents(eventRecords.get(
                                classifier.getProcessInsId()).getEventRecords(), disabledRuleSet, classifier);
                        synEvents.setAgendaFilter(new NameBasedAndRuleDisableAgendaFilter(ruleSets, disabledRuleSet));
                        synRules.insertEvent(synEvents);
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        }
    }

    private void newFireGlobal(EventRecord e) {
        GlobalRegTable globalRegTable = composite.getGlobalRegTable();
        GlobalKnowledgebase globalKnowledgebase = composite.getGlobalKnowledgebase();
        if (globalRegTable != null && globalKnowledgebase != null) {
            Classifier classifier = e.getClassifier();
            String vsnID = classifier.getVsnId();
            String processID = classifier.getProcessId();
            String vsnInstanceId = classifier.getProcessInsId();
            ProcessInstance processInstance = engine.getProcessInstance(classifier);
            if (processInstance == null) {
                return;
            }
            DisabledRuleSet disabledRuleSet = processInstance.getDisabledRuleSet("global");
            List<RegulationUnitKey> vsnRegTableEntry = globalRegTable.getVSNTableEntry(vsnID + "_" + processID);
            if (vsnRegTableEntry != null) {
                List<RegulationRuleSet> ruleSets = new ArrayList<RegulationRuleSet>();
                for (RegulationUnitKey unitKey : vsnRegTableEntry) {
                    if (unitKey.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                        if (!unitKey.isExcluded(vsnInstanceId)) {
                            ruleSets.add(globalRegTable.getRegulationRuleSet(unitKey.getUnitId()));
                        } else {
                            System.out.println("VSN instance " + vsnInstanceId + " is excluded from the new VSN as " +
                                    "it is from the old VSN");
                        }
                    } else if (unitKey.getMgtState().getState().equals(ManagementState.STATE_PASSIVE)) {
                        //passive VSN but there is a VSN instance => old VSN instance - need to progress using the
                        // passive one
                        // to be removed unit key -> should not be used by new elements - should only be used by old
                        // elements
                        if (unitKey.isIncluded(vsnInstanceId)) {
                            ruleSets.add(globalRegTable.getRegulationRuleSet(unitKey.getUnitId()));
                        } else {
                            System.out.println("VSN instance " + vsnInstanceId + " is not included " +
                                    " as " +
                                    "it is from the new VSN");
                        }
                    } else {
                        System.out.println(" Unknown state of " + unitKey.getMgtState().getState() + " for  " +
                                "" + vsnInstanceId);
                    }
                }
                try {
                    GlobalEvents globalEvents = new GlobalEvents(eventRecords.get(
                            classifier.getProcessInsId()).getEventRecords(), disabledRuleSet, classifier);
                    globalEvents.setAgendaFilter(new NameBasedAndRuleDisableAgendaFilter(ruleSets, disabledRuleSet));
                    globalKnowledgebase.insertEvent(globalEvents);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
        }
    }

    public void subscribe(SerendipEventListener sel) {
        String pid = sel.getClassifier().getProcessInsId();
        ProcessEventListener processEventListener = eventListeners.get(pid);
        if (processEventListener == null) {
            synchronized (eventListeners) {
                processEventListener = eventListeners.get(pid);
                if (processEventListener == null) {
                    processEventListener = new ProcessEventListener(pid);
                    eventListeners.put(pid, processEventListener);
                }
            }
        }
        processEventListener.addSerendipEventListener(sel);
    }

    /**
     * Call <code>eventPatternMatched</code> of the
     * <code>SerendipEventListener</code> when an event pattern is matched.
     *
     * @param sourceEvent
     * @throws SerendipException
     */
    private void fire(EventRecord sourceEvent) throws SerendipException {
        if (log.isInfoEnabled()) {
            log.info("Event added to cloud " + sourceEvent);
        }
        final String pid = sourceEvent.getClassifier().getProcessInsId();
        ProcessEventListener processEventListener = eventListeners.get(pid);
        if (processEventListener != null) {
            final Collection<SerendipEventListener> listeners = processEventListener.getSerendipEventListener(this, sourceEvent.getEventId());
            for (final SerendipEventListener sel : listeners) {
                eventCloudPool.execute(new Runnable() {
                    public void run() {
                        try {
                            sel.eventPatternMatched(sel.getCurrentMatchedPattern(), sel.getClassifier());
                        } catch (Exception ignored) {
                            log.error("unexpected exception : " + ignored);
                        }
                    }
                });
            }
        }
    }

    /**
     * Check whether an event pattern is matched
     *
     * @param eventPattern
     * @param pId
     * @return
     */
    public boolean isPatternMatched(String eventPattern, String pId) {

        try {
            return EventPatternRecognizer.isPatternMatched(eventPattern, pId, this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("Error when matching event pattern : " + e.getMessage(), e);
        }
        return false;
    }


    /**
     * Compare time stamps of two event records of the same process instance
     *
     * @param event1Id e1
     * @param event2Id
     * @return the value 0 if the argument Date is equal to this Date; a value
     * less than 0 if this Date is before the Date argument; and a value
     * greater than 0 if this Date is after the Date argument.
     */
    public int compareEventDate(String event1Id, String event2Id, String pId) {
        EventRecord e1, e2 = null;

        e1 = this.getEventReocrd(event1Id, pId);
        e2 = this.getEventReocrd(event2Id, pId);

        return 1;
    }

    /**
     * If the record is found, return it. Else return null
     *
     * @param eventId
     * @param pId
     * @return
     */
    public EventRecord getEventReocrd(String eventId, String pId) {
        ProcessEventRecord processEventRecord = eventRecords.get(pId);
        if (processEventRecord != null) {
            return processEventRecord.getEventRecord(eventId);
        }
        return null;
    }

    public EventRecord getEventReocrd(String eventId, Classifier classifier) {
        return getEventReocrd(eventId, classifier.getProcessInsId());
    }

    /**
     * Check whether a particular event has occurred for a given process
     * instance.
     *
     * @param eventId
     * @param pId
     * @return
     */
    public boolean isEventRecorded(String eventId, String pId) {
        ProcessEventRecord processEventRecord = eventRecords.get(pId);
        return processEventRecord != null && processEventRecord.isContains(eventId);
        //eventId = eventId.substring(1, eventId.length() - 1);
    }

    /**
     * Removes an event from the event record
     *
     * @param eventId
     * @param pId
     */
    public void expireEvent(String eventId, String pId) {
        ProcessEventRecord processEventRecord = eventRecords.get(pId);
        if (processEventRecord != null) {
            processEventRecord.remove(eventId);
        }
        if (log.isDebugEnabled()) {
            log.debug("Removed event : " + pId);
        }
    }

    public void expireEvent(String eventId, Classifier classifier) {
        expireEvent(eventId, classifier.getProcessInsId());
    }

    public void expireEvent(final String pId) {
        eventRecords.remove(pId);
    }

    public void expireEventListeners(final String pId) {
        eventListeners.remove(pId);
    }

    public void expireProcessInstanceOnBackground(String pId) {
        roadCleaner.addCompletedProcessInstance(pId);
    }


    public void terminate() {
        this.roadCleaner.cancel();
        this.eventCloudPool.shutdown();
//        this.terminated = true;
    }
}
