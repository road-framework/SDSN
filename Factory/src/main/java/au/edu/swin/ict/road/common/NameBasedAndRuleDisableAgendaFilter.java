package au.edu.swin.ict.road.common;


import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import java.util.List;

/**
 * TODO
 */
public class NameBasedAndRuleDisableAgendaFilter implements AgendaFilter {

    private List<RegulationRuleSet> regulationRuleSet;
    private DisabledRuleSet disabledRules;

    public NameBasedAndRuleDisableAgendaFilter(List<RegulationRuleSet> regulationRuleSet, DisabledRuleSet disabledRules) {
        this.regulationRuleSet = regulationRuleSet;
        this.disabledRules = disabledRules;
    }

    public boolean accept(Match activation) {
        if (disabledRules.contains(activation.getRule().getName())) {
            return false;
        }
        boolean tobeReturn = false;
        for (RegulationRuleSet ruleSet : regulationRuleSet) {
            tobeReturn = ruleSet.contains(activation.getRule().getName());
            if (tobeReturn) {
                break;
            }
        }
        return tobeReturn;
    }
}
