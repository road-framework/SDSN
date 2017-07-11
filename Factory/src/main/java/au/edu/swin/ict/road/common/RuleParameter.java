package au.edu.swin.ict.road.common;

/**
 * TODO
 */

public class RuleParameter {
    private String ruleName;
    private String place;
    private String type;
    private String parameterName;
    private String parameterValue;
    private String ruleFile;

    public RuleParameter(String ruleName, String place, String type, String parameterName, String parameterValue, String ruleFile) {
        this.ruleName = ruleName;
        this.place = place;
        this.type = type;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.ruleFile = ruleFile;
    }

    public RuleParameter() {
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getRuleFile() {
        return ruleFile;
    }

    public void setRuleFile(String ruleFile) {
        this.ruleFile = ruleFile;
    }

    @Override
    public String toString() {
        return "RuleParameter{" +
                "ruleName='" + ruleName + '\'' +
                ", place='" + place + '\'' +
                ", type='" + type + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", parameterValue='" + parameterValue + '\'' +
                ", ruleFile='" + ruleFile + '\'' +
                '}';
    }
}
