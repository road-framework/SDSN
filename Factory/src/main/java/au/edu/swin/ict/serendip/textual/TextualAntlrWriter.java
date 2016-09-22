package au.edu.swin.ict.serendip.textual;

import au.edu.swin.ict.road.xml.bindings.CollaborationUnitType;
import au.edu.swin.ict.road.xml.bindings.ConstraintType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionType;
import au.edu.swin.ict.road.xml.bindings.TaskRefType;

import java.util.List;

/**
 * @author Malinda
 */
public class TextualAntlrWriter {
    public static String PDTypeToText(ProcessDefinitionType pdt) {
        StringBuffer buf = new StringBuffer();
        buf.append("ProcessDefinittion " + pdt.getId() + "{");
        buf.append("CoS " + pdt.getCoS());
        buf.append("CoT) " + pdt.getCoT());
        List<String> btIdList = pdt.getCollaborationUnitRef();
        for (String s : btIdList) {
            buf.append("BehaviorTermRef " + s + ";");
        }
        buf.append("};\n");
        return buf.toString();
    }

    public static String btTypeToText(CollaborationUnitType btt) {
        //TODO: Us toString of BehaviorTerm in the grammar
        StringBuffer buf = new StringBuffer();
        String extendsPart = "";
        if (!((null == btt.getExtends()) || (btt.getExtends().equals("")))) {
            extendsPart = " extends " + btt.getExtends();
        }

        //Show behavior terms
        buf.append("BehaviorTerm " + btt.getId() + extendsPart + "{\n");
        List<TaskRefType> trList = btt.getConfigurationDesign().getTaskRef();
        for (TaskRefType trt : trList) {
            buf.append(taskRefTypeToText(trt) + "\n");
        }

        //Show constraints
        List<ConstraintType> cList = btt.getConstraints().getConstraint();
        for (ConstraintType c : cList) {
            buf.append(constraintToText(c));
        }
        buf.append("};\n");

        return buf.toString();
    }

    public static String taskRefTypeToText(TaskRefType trt) {
        StringBuffer buf = new StringBuffer();
        buf.append("Task " + trt.getId() + "{\n");
        buf.append("EPpre " + trt.getPreEP() + "\n");
        buf.append("EPPost " + trt.getPostEP() + "\n");
        buf.append("PerfProb " + trt.getPerformanceVal() + "\n");

        buf.append("};" + "\n");

        return buf.toString();
    }

    public static String constraintToText(ConstraintType ct) {
        StringBuffer buf = new StringBuffer();

        return buf.toString();
    }
}
