package au.edu.swin.ict.road.common;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 */
public class Weight implements Serializable {
    private static final long serialVersionUID = 6973334919450459206L;
    private String id;
    private int weight = 1;
    private AtomicInteger count = new AtomicInteger(weight);

    public Weight(int weight) {
        this.weight = weight;
    }

    public Weight() {
    }

    public int getAndDecrementCurrentAllowed() {
        return count.getAndDecrement();
    }

    public void resetCurrentAllowed() {
        count.set(weight);
    }

    public int getWeight() {
        return count.get();
    }

    public void setWeight(int weight) {
        this.weight = weight;
        count.set(weight);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
