package au.edu.swin.ict.serendip.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO documentation
 */
public class PerformanceModel {

    private List<TaskNode> taskNodes = new ArrayList<TaskNode>();

    public void addTaskNode(TaskNode taskNode) {
        taskNodes.add(taskNode);
    }

    public Collection<TaskNode> getTaskNodes() {
        return taskNodes;
    }
}
