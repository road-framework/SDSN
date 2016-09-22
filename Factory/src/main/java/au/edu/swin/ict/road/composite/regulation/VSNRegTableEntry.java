package au.edu.swin.ict.road.composite.regulation;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public class VSNRegTableEntry {

    private String vsnName;
    private Map<String, ProcessRegTableEntry> processEntryKeyMap = new HashMap<String, ProcessRegTableEntry>();

    public VSNRegTableEntry(String vsnName) {
        this.vsnName = vsnName;
    }

    public void addProcessRegTableEntry(String name, ProcessRegTableEntry processRegTableEntry) {
        processEntryKeyMap.put(name, processRegTableEntry);
    }

    public ProcessRegTableEntry getProcessRegTableEntry(String name) {
        return processEntryKeyMap.get(name);
    }

    public String getVsnName() {
        return vsnName;
    }
}
