package au.edu.swin.ict.road.common;


import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * TODO
 */
public class DisabledRuleSetAgendaFilter implements AgendaFilter {
    private DisabledRuleSet disabledRules;

    public DisabledRuleSetAgendaFilter(DisabledRuleSet disabledRules) {
        this.disabledRules = disabledRules;
    }

    public boolean accept(Match activation) {
        if (disabledRules.contains(activation.getRule().getName())) {
            return false;
        }
        return true;
    }
}
