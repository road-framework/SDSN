package au.edu.swin.ict.road.common;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class RuleParameterFactory {
    //MM.synchronization.admissioncheck.limit=130,ruleFile=admi.drl;MM.synchronization.admissioncheck.limit=130,ruleFile=admi.drl;
    public static List<RuleParameter> createRuleParameters(String config) {
        List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();
        String[] parameterStr = config.split(";");
        for (String paraStr : parameterStr) {
            String[] pass1 = paraStr.split(",");
            String pass12 = pass1[0];
            String pass13 = pass1[1];
            String ruleFile = pass13.split("=")[1].trim();
            String[] pass12_1 = pass12.split("=");
            String parameterValue = pass12_1[1].trim();
            String[] strs = pass12_1[0].split("\\.");
            String place = strs[0].trim();
            String type = strs[1].trim();
            String ruleName = strs[2].trim();
            String parameterName = strs[3].trim();
            RuleParameter ruleParameter = new RuleParameter(ruleName, place, type, parameterName, parameterValue, ruleFile);
            ruleParameters.add(ruleParameter);
        }
        return ruleParameters;
    }

    public static void instantiateParametrizedRule(String targetRuleFile, RuleParameter ruleParameter, String suffix, String ruleDir) {
        try {
            String content = IOUtils.toString(new FileInputStream(ruleDir + ruleParameter.getRuleFile()));
            content = content.replaceAll("<<" + ruleParameter.getParameterName() + ">>", ruleParameter.getParameterValue());
            content = content.replaceAll("(?m)^rule.*" + ruleParameter.getRuleName() + ".*", "rule " + "\"" + ruleParameter.getRuleName() + suffix + "\"");
            IOUtils.write(content, new FileOutputStream(targetRuleFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        List<RuleParameter> ruleParameters = createRuleParameters("MM.synchronization.admissioncheck.limit=130,ruleFile=admi.drl;SC.routing.admissioncheck.limit=130,ruleFile=admi.drl");
        System.out.println(ruleParameters);
        RuleParameter ruleParameter = new RuleParameter();
        ruleParameter.setParameterName("limit");
        ruleParameter.setParameterValue("400");
        ruleParameter.setRuleName("admissionCheckV1");
        ruleParameter.setRuleFile("D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\legalassistence\\data2\\rules\\admissionCheck.drl");
        instantiateParametrizedRule("D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\legalassistence\\data2\\rules\\admissionCheck__1.drl", ruleParameter, "__1", "");
    }
}
