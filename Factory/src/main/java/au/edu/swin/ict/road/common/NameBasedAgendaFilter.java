package au.edu.swin.ict.road.common;

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import java.util.List;

/**
 * TODO
 */
public class NameBasedAgendaFilter implements AgendaFilter {

    private List<RegulationRuleSet> regulationRuleSet;

    public NameBasedAgendaFilter(List<RegulationRuleSet> regulationRuleSet) {
        this.regulationRuleSet = regulationRuleSet;
    }

    public boolean accept(Match activation) {
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
