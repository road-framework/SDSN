package au.edu.swin.ict.road.composite.regulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 */
public class RuleSetKey {
    private List<String> keySet = new ArrayList<String>();

    public RuleSetKey(String keys) {
        if (keys.indexOf(",") > 0) {
            String[] nameList = keys.split(",");
            Collections.addAll(keySet, nameList);
        } else {
            keySet.add(keys);
        }
    }

    public boolean contains(String keys) {
        return keySet.contains(keys);
    }
}
