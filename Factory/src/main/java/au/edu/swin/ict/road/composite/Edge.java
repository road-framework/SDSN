package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.xml.bindings.ResultMsgType;
import au.edu.swin.ict.road.xml.bindings.SrcMsgType;

/**
 * TODO documentation
 */
public class Edge {

    private Node source;
    private String identifier;
    private ResultMsgType resultMsgType;
    private SrcMsgType srcMsgType;

    public Edge(String identifier, ResultMsgType resultMsgType, SrcMsgType srcMsgType, Node source) {
        this.identifier = identifier;
        this.resultMsgType = resultMsgType;
        this.srcMsgType = srcMsgType;
        this.source = source;
    }

    public Node getSource() {
        return source;
    }

    public ResultMsgType getResultMsgType() {
        return resultMsgType;
    }

    public SrcMsgType getSrcMsgType() {
        return srcMsgType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (source != null) {
            sb.append(source.getIdentifier());
            sb.append(" -> ");
        }
        sb.append(identifier);
        return sb.toString();
    }
}