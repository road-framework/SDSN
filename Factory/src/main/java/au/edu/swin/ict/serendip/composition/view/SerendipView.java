package au.edu.swin.ict.serendip.composition.view;

import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.epc.EPMLWriter;
import au.edu.swin.ict.serendip.epc.MergeBehavior;
import org.apache.log4j.Logger;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.epcpack.EPCDataObject;
import org.processmining.framework.models.epcpack.EPCEvent;
import org.processmining.framework.models.epcpack.EPCFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Malinda Kapuruge
 */
public abstract class SerendipView {
    private static Logger log = Logger.getLogger(SerendipView.class.getName());
    protected List<BehaviorTerm> btVec = new ArrayList<BehaviorTerm>();
    protected String id = null;
    private ConfigurableEPC epc = null;
    private boolean showData = false;

    public SerendipView() {
        this.constructView();
    }

    public SerendipView(String id, List<BehaviorTerm> btVec) {
        this.id = id;
        this.btVec = btVec;
        this.constructView();
    }

    public static ConfigurableEPC markDataDependencies(ConfigurableEPC epc, Map<String, String> map) {

        ArrayList<EPCFunction> functions = epc.getFunctions();
        for (EPCFunction f : functions) {
            String funcId = f.getIdentifier();
            if (map.containsKey(funcId)) {
                String dataVal = map.get(funcId);
                f.addDataObject(new EPCDataObject(dataVal, f));
                //f.addInfSysObject(new EPCInfSysObject(dataVal, f));
            }
        }
        return epc;
    }

    public static ConfigurableEPC markFiredEvents(ConfigurableEPC epc, Collection<EventRecord> firedEvents) {

        for (EventRecord er : firedEvents) {

            String eid = er.getEventId().trim();
            EPCEvent epcEvent = epc.getEvent(eid);
            if (null != epcEvent) {
                log.debug("Marking Fire Event" + epcEvent.getIdentifier());
                epcEvent.setAttribute(EPCEvent.FILLCOLOR_ATTR, "red");
            }
        }
        return epc;
    }

    public static ConfigurableEPC markExecutedTasks(ConfigurableEPC epc, Collection<String> executedTasks, Collection<String> activeTasks) {
        Collection<EPCFunction> funcitons = epc.getFunctions();
        for (EPCFunction func : funcitons) {
            log.debug("EPCFunction = " + func.getIdentifier());
            String funcId = func.getIdentifier();
            if (funcId.contains(".")) {
                String[] splits = funcId.split("\\.");
                log.debug("Splits " + splits);
                if (splits.length > 0) {
                    funcId = splits[1];
                }
            }
            if (null != executedTasks) {
                for (String s : executedTasks) {
                    if (s.equals(funcId)) {
                        func.setAttribute(EPCFunction.FILLCOLOR_ATTR, "red");
                    }
                }
            }
            if (null != activeTasks) {
                for (String s : activeTasks) {
                    if (s.equals(funcId)) {
                        func.setAttribute(EPCFunction.FILLCOLOR_ATTR, "green");
                    }
                }
            }

        }
        return epc;
    }

    public String getId() {
        return this.id;
    }

    public void needToShowData(boolean needToShowData) {
        this.showData = needToShowData;
    }

    public ConfigurableEPC getViewAsEPC() {

        if (null != this.epc) {
            this.epc.setIdentifier(this.id);
        }
        return this.epc;
    }

    protected void constructView() {
        if (this.btVec.size() == 0) {
            this.epc = null;
        } else if (this.btVec.size() == 1) {// For behavior terms
            this.epc = btVec.get(0).getEpc();
        } else {// For processes or multiple merged behavior
            btVec.toArray();
            BehaviorTerm[] btArr = (BehaviorTerm[]) this.btVec
                    .toArray(new BehaviorTerm[this.btVec.size()]);
            MergeBehavior merge = new MergeBehavior(btArr);
            this.epc = merge.getMergedBehaviorAsEPC();

        }
    }

    public void toFile(String fileName) throws IOException {

        EPMLWriter epmlWriter = new EPMLWriter(this.epc, true);
        epmlWriter.writeToFile(fileName);
    }

    @Override
    public String toString() {
        return this.getId();
    }

    public List<BehaviorTerm> getBtVec() {
        return btVec;
    }
}
