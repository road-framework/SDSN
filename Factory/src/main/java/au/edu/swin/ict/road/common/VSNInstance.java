package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class VSNInstance extends ManagementState {
    public static String STATE_SATISFIED = "satisfied";
    public static String STATE_VIOLATED = "violated";
    private Classifier classifier;

    public VSNInstance(String id, Classifier classifier) {
        super(id);
        this.classifier = classifier;
    }

    public VSNInstance(Classifier classifier) {
        this.classifier = classifier;
        this.setId(classifier.getProcessInsId());
    }

    public Classifier getClassifier() {
        return classifier;
    }
}
