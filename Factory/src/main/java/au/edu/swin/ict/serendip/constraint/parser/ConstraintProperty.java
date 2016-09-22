package au.edu.swin.ict.serendip.constraint.parser;

/**
 * @author Malinda Kapuruge
 * @depreciated Still not in use.
 */
public abstract class ConstraintProperty {
    private String expression = null;

    public ConstraintProperty(String expression) {
        this.expression = expression;
    }

    public abstract String convert();
}
