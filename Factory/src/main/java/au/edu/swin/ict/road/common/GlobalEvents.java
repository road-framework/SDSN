package au.edu.swin.ict.road.common;


import org.kie.api.runtime.rule.AgendaFilter;

import java.util.List;

/**
 * TODO
 */
public class GlobalEvents implements IEvent {

    private List<EventRecord> eventRecords;
    private AgendaFilter agendaFilter;
    private DisabledRuleSet disabledRuleSet;
    private Classifier classifier;

    public GlobalEvents(List<EventRecord> eventRecords, DisabledRuleSet disabledRuleSet, Classifier classifier) {
        this.eventRecords = eventRecords;
        this.disabledRuleSet = disabledRuleSet;
        this.classifier = classifier;
    }

    public List<EventRecord> getEventRecords() {
        return eventRecords;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void disableRule(String ruleName) {
        disabledRuleSet.disable(ruleName);
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public DisabledRuleSet getDisabledRuleSet() {
        return disabledRuleSet;
    }
}
