package au.edu.swin.ict.serendip.composition.view;

import au.edu.swin.ict.serendip.composition.BehaviorTerm;

import java.util.ArrayList;
import java.util.List;

public class ProcessView extends SerendipView {
    List<String> constraintsVec = new ArrayList<String>();

    public ProcessView() {
        super();
    }

    public ProcessView(String id, List<BehaviorTerm> btVec) {
        super(id, btVec);

    }

    public void addConstraint(String constraint) {
        this.constraintsVec.add(constraint);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
