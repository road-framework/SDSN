package au.edu.swin.ict.serendip.grammar.behav.model;

import au.edu.swin.ict.road.xml.bindings.CollaborationUnitType;
import au.edu.swin.ict.road.xml.bindings.ConfigurationDesignType;
import au.edu.swin.ict.road.xml.bindings.TaskRefType;

import java.util.ArrayList;
import java.util.List;

public class ModelTransform {
    /**
     * Given a BTType get Behavior model
     *
     * @param btType
     * @return
     */
    public static Behavior behaviorFwdTransform(CollaborationUnitType btType) {

        Behavior behavior = new Behavior();
        //Set id
        behavior.setBehaviorId(btType.getId());
        String extendsStr = btType.getExtends();

        //Set extends if any
        if (null != extendsStr) {
            behavior.setExtendId(extendsStr);
        }
        //Add tasks
        List<Task> tasks = new ArrayList<Task>();
        ConfigurationDesignType taskRefs = btType.getConfigurationDesign();
        if (null != taskRefs) {

            for (TaskRefType tr : taskRefs.getTaskRef()) {
                String[] idSplit = tr.getId().split("\\.");
                Task task = new Task();
                task.setPreEP(tr.getPreEP());//STRING in behav.g
                task.setPostEP(tr.getPostEP());
                task.setPp(tr.getPerformanceVal());
                task.setRoblig(idSplit[0]);
                task.setTaskId(idSplit[1]);

                tasks.add(task);
            }
        }

        behavior.setTasks(tasks);
        return behavior;
    }

    /**
     * Given a behavior get a BehaviorType
     *
     * @param behavior
     * @param btt
     * @return
     */
    public static CollaborationUnitType behaviorRevTransform(Behavior behavior, CollaborationUnitType btt) {

        //Set id
        btt.setId(behavior.getBehaviorId());
        //Set extends if any
        String extendsStr = behavior.getExtendId();
        if (null != extendsStr) {
            btt.setExtends(extendsStr);
        }
        //Remove existing tasks references
        btt.setConfigurationDesign(null);
        //Add tasks
        List<Task> tasks = behavior.getTasks();
        if (tasks.size() > 0) {
            //add a new reference
            ConfigurationDesignType tRefs = new ConfigurationDesignType();
            btt.setConfigurationDesign(tRefs);

            for (Task task : tasks) {
                TaskRefType tr = new TaskRefType();
                tr.setId(task.getRoblig() + "." + task.getTaskId());
                tr.setPreEP(task.getPreEP());
                tr.setPostEP(task.getPostEP());
                tr.setPerformanceVal(task.getPp());
                //Set any otehr future attributed in here
                tRefs.getTaskRef().add(tr);
            }
        }


        return btt;
    }
}
