/**
 *
 */
package au.edu.swin.ict.road.regulator;

import java.util.*;

/**
 * @author Aditya
 */
public class FactTupleSpaceRow {

    private String factType;
    private Map<String, FactObject> factObjectMap;
    private Date time;
    private List<Object> sources;
    private List<Object> subscribers;
    private FactObject masterFact;

    public FactTupleSpaceRow(FactObject masterFact) {
        this.factType = masterFact.getFactType();
        this.factObjectMap = new HashMap<String, FactObject>();
        this.time = new Date();
        this.sources = new ArrayList<Object>();
        this.subscribers = new ArrayList<Object>();
        this.masterFact = masterFact;
    }

    public FactObject getFactObjectByIdValue(String idVal) {
        return factObjectMap.get(idVal);
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public Collection<FactObject> getFactList() {
        return factObjectMap.values();
    }

    public void setFactList(List<FactObject> factList) {
        for (FactObject factObject : factList) {
            addFact(factObject);
        }
    }

    public void addFact(FactObject fact) {
        this.factObjectMap.put(fact.getIdentifier(), fact);
    }

    public void updateFact(FactObject fact) {
        this.factObjectMap.put(fact.getIdentifier(), fact);
    }

    public boolean deleteFact(FactObject fact) {
        this.factObjectMap.remove(fact.getIdentifier());
        return true;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public List<Object> getSources() {
        return sources;
    }

    public void setSources(List<Object> sources) {
        this.sources = sources;
    }

    public List<Object> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Object> subscribers) {
        this.subscribers = subscribers;
    }

    public FactObject getMasterFact() {
        return masterFact;
    }

    public void setMasterFact(FactObject masterFact) {
        this.masterFact = masterFact;
    }

}
