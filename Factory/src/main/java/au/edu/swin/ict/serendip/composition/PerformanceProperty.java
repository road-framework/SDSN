package au.edu.swin.ict.serendip.composition;

/**
 * Representatio for a performance property.
 * Example: Time/Cost to complete a task
 *
 * @author Malinda
 */
public class PerformanceProperty {
    public static final String MAXTIME = "MAXTIME";
    String value = null;

    public PerformanceProperty(String string) {
        super();
        this.value = string;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
