package workload;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */
public class Sample {
    private int sampleId;
    private List<TenantUnit> units = new ArrayList<TenantUnit>();

    public Sample(int sampleId) {
        this.sampleId = sampleId;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void addTenantUnit(TenantUnit unit) {
        units.add(unit);
    }

    public List<TenantUnit> getTenantUnits() {
        return units;
    }
}
