package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.RegulationRuleSet;

import java.util.HashMap;
import java.util.Map;

public class ProcessRegTableEntry {
    private String processName;
    private Map<String, RegulationRuleSet> msgSignatureRules = new HashMap<String, RegulationRuleSet>();
    private Map<RuleSetKey, RegulationRuleSet> multiMsgSignatureRules = new HashMap<RuleSetKey, RegulationRuleSet>();

    public ProcessRegTableEntry(String processName) {
        this.processName = processName;
    }

    public void addRuleSet(String signature, RegulationRuleSet ruleKey) {
        signature = signature.trim();
        if (signature.indexOf(",") > 0) {
            RuleSetKey ruleSetKey = new RuleSetKey(signature);
            multiMsgSignatureRules.put(ruleSetKey, ruleKey);
        } else {
            msgSignatureRules.put(signature, ruleKey);
        }
    }

    public RegulationRuleSet getRegulationRuleSet(String name) {
        RegulationRuleSet ruleSet = msgSignatureRules.get(name);
        if (ruleSet == null) {
            for (RuleSetKey key : multiMsgSignatureRules.keySet()) {
                if (key.contains(name)) {
                    ruleSet = multiMsgSignatureRules.get(key);
                    break;
                }
            }
        }
        return ruleSet;
    }

    public String getProcessName() {
        return processName;
    }
}
