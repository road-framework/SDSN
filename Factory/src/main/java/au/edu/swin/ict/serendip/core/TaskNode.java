package au.edu.swin.ict.serendip.core;

import au.edu.swin.ict.road.xml.bindings.QoSType;
import au.edu.swin.ict.road.xml.bindings.TaskRefType;
import au.edu.swin.ict.road.xml.bindings.TaskType;

/**
 * TODO documentation
 */
public class TaskNode {
    private TaskType taskType;
    private TaskRefType taskRefType;
    private int capacityUnit = 1;
    private int threshold;
    private int globalRate;
    private String roleId;

    public TaskNode(TaskType taskType, TaskRefType taskRefType, String roleId, int globalRate) {
        this.taskType = taskType;
        this.taskRefType = taskRefType;
        this.globalRate = globalRate;
        this.roleId = roleId;
        init();

    }

    private void init() {
        QoSType qoSType = taskType.getQoS();
        if (qoSType != null) {
            this.threshold = globalRate * capacityUnit;
        }
    }

    public int getThreshold() {
        return threshold;
    }

    public int getCapacityUnit() {
        return capacityUnit;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskRefType getTaskRefType() {
        return taskRefType;
    }

    public String getRoleId() {
        return roleId;
    }
}
