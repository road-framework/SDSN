package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.xml.bindings.TaskRefType;
import au.edu.swin.ict.road.xml.bindings.TaskType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */

public class Node {

    private List<Edge> in = new ArrayList<Edge>();
    private Role role;
    private TaskType taskType;
    private String identifier;
    private TaskRefType taskRefType;

    public Node(String identifier, Role iRole, TaskType taskType, TaskRefType taskRefType) {
        this.identifier = identifier;
        this.role = iRole;
        this.taskType = taskType;
        this.taskRefType = taskRefType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TaskRefType getTaskRefType() {
        return taskRefType;
    }

    public Role getRole() {
        return role;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void addInEdge(Edge edge) {
        in.add(edge);
    }

    public List<Edge> getInEdges() {
        return in;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Edge edge : in) {
            if (!first) {
                sb.append(" | ");
            }
            sb.append(edge.toString());
            first = false;

        }
        sb.append(" -> ");
        sb.append(identifier);
        return sb.toString();
    }
}
