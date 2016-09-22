package au.edu.swin.ict.road.composite.transform;

import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.analyzer.MessageAnalyzer;
import au.edu.swin.ict.road.composite.message.analyzer.TransformLogic;
import au.edu.swin.ict.road.composite.message.analyzer.XSLTAnalyzer;
import au.edu.swin.ict.road.composite.message.containers.QueueListener;
import au.edu.swin.ict.road.xml.bindings.RoleType;
import au.edu.swin.ict.road.xml.bindings.TaskType;
import au.edu.swin.ict.road.xml.bindings.TasksType;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * This class listens to any message inputs to the inQueue of a role
 * by implementing the QueueListener.
 *
 * @author Saichander Kukunoor (saichanderreddy@gmail.com)
 */
public class InTransform implements QueueListener {

    private static Logger log = Logger.getLogger(InTransform.class.getName());
    private Role role;
    private Map<String, TaskType> taskTypeMap = new HashMap<String, TaskType>();

    public InTransform(Role role) {
        this.role = role;
        RoleType roleType = this.role.getRoleType();
        if (roleType != null) {
            TasksType tasks = this.role.getRoleType().getTasks();
            if (tasks != null) {
                for (TaskType task : tasks.getTask()) {
                    taskTypeMap.put(task.getId(), task);
                }
            }
        }
    }

    /**
     * This message is invoked whenever a message is placed in the
     * inQueue of the associated role
     *
     * @see au.edu.swin.ict.road.composite.message.containers.QueueListener#messageReceived(au.edu.swin.ict.road.composite.message.MessageWrapper)
     */
    @Override
    public void messageReceived(MessageWrapper message) {
        // get the task id
        String mwTaskId = message.getTaskId();
        TaskType task = taskTypeMap.get(mwTaskId);
        // get all the tasks in this role
//        TasksType tasks = role.getRoleType().getTasks();
        // if the task id is null or there are no tasks, then place the message directly in the routerQueue and return
        if (mwTaskId == null || task == null) {
            role.putSchedulerQueueMessage(message);
            return;
        }
        MessageAnalyzer analyzer = XSLTAnalyzer.getInstance();
        TransformLogic tLogic = new TransformLogic();
        tLogic.setTask(task);
        tLogic.setRole(role);
        analyzer.disjunct(message, tLogic);
    }
}
