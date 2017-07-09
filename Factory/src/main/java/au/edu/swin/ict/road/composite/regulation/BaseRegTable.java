package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.RegulationRuleSet;
import au.edu.swin.ict.road.common.RegulationUnitKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRegTable {
    private String name;
    private Map<String, List<RegulationUnitKey>> vsn_process_2_regUnits = new HashMap<String, List<RegulationUnitKey>>();
    private Map<String, RegulationRuleSet> regUnit_RuleSet = new HashMap<String, RegulationRuleSet>();
    private Map<RuleSetKey, RegulationRuleSet> multiMsgSignatureRules = new HashMap<RuleSetKey, RegulationRuleSet>();
    private Map<String, Object> customMap = new HashMap<String, Object>();

    public BaseRegTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addVSNTableEntry(String key, RegulationUnitKey unitKey) {
        List<RegulationUnitKey> regUnits = vsn_process_2_regUnits.get(key);
        if (regUnits == null) {
            regUnits = new ArrayList<RegulationUnitKey>();
            vsn_process_2_regUnits.put(key, regUnits);
        }
        if (regUnits.isEmpty()) {
            regUnits.add(unitKey);
        } else {
            boolean caAdd = true;
            for (RegulationUnitKey key2 : regUnits) {
                if (key2.getUnitId().equals(unitKey.getUnitId())) {
                    caAdd = false;
                    break;
                }
            }
            if (caAdd) {
                regUnits.add(unitKey);
            }
        }
    }

    public void removeVSNTableEntry(String key, String regUnit) {
        List<RegulationUnitKey> unitKeys =
                vsn_process_2_regUnits.get(key);
        RegulationUnitKey tobeRemoved = null;
        for (RegulationUnitKey unitKey : unitKeys) {
            if (unitKey.getUnitId().equals(regUnit)) {
                tobeRemoved = unitKey;
                break;
            }
        }
        if (tobeRemoved != null) {
            unitKeys.remove(tobeRemoved);
        }
    }

    public RegulationUnitKey getVSNTableEntry(String key, String regUnit) {
        List<RegulationUnitKey> unitKeys =
                vsn_process_2_regUnits.get(key);
        for (RegulationUnitKey unitKey : unitKeys) {
            if (unitKey.getUnitId().equals(regUnit)) {
                return unitKey;
            }
        }
        return null;
    }

    public List<RegulationUnitKey> getVSNTableEntry(String key) {
        return vsn_process_2_regUnits.get(key);
    }

//    public void addRegulationUnitTavbleEntry(String regUnit, String ruleId) {
//        List<String> signatureList = regUnits_Rules.get(regUnit);
//        if( signatureList  == null){
//            signatureList = new ArrayList<String>();
//            regUnits_Rules.put(regUnit, signatureList);
//        }
//        signatureList.add(ruleId);
//    }
//
//    public List<String> getRegulationUnitTableEntry(String key) {
//        return regUnits_Rules.get(key);
//    }

    public void addRuleSet(String regRuleUnitId, RegulationRuleSet ruleKey) {
        regRuleUnitId = regRuleUnitId.trim();
        if (!regUnit_RuleSet.containsKey(regRuleUnitId)) {
            regUnit_RuleSet.put(regRuleUnitId, ruleKey);
        }
    }

    public RegulationRuleSet getRegulationRuleSet(String name) {
            return regUnit_RuleSet.get(name);
    }

    public RegulationRuleSet removeRegulationRuleSet(String name) {
        return regUnit_RuleSet.remove(name);
    }

    public void removeRuleFromRegulationRuleSet(String name, String ruleId) {
        RegulationRuleSet regulationRuleSet = regUnit_RuleSet.get(name);
        regulationRuleSet.getAllRules().remove(name);
    }

    public void setName(String name) {
        this.name = name;
    }


    public Object getCustomFunction(String id) {
        return customMap.get(id);
    }

    public void addCustomFunction(String id, Object object) {
        customMap.put(id, object);
    }

    public boolean containsCustomFunction(String id) {
        return customMap.containsKey(id);
    }

}
