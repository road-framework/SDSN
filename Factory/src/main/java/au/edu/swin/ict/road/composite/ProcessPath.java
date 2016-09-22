package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.xml.bindings.ResultMsgType;
import au.edu.swin.ict.road.xml.bindings.SrcMsgType;
import au.edu.swin.ict.road.xml.bindings.SrcMsgsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO documentation
 */

public class ProcessPath {
    public final static String MSG_ID_SEPERATOR = ".";
    public final static String MSG_ID_REQUEST = "Req";
    public final static String MSG_ID_RESPONSE = "Res";
    private List<Node> nodes = new ArrayList<Node>();

    private Map<String, Node> resultSourceNodeMap = new HashMap<String, Node>();
    private Map<String, ResultMsgType> resultMsgs = new HashMap<String, ResultMsgType>();
    private String id;

    public String getId() {
        return id;
    }

    public ProcessPath(String id) {
        this.id = id;
    }

    public void addNode(Node node) {
        nodes.add(node);
        if (node.getTaskType().getResultMsgs() != null) {
            // TODO assume here the all messages must be there in a src message - no selection
            for (ResultMsgType resultMsg : node.getTaskType().getResultMsgs().getResultMsg()) {
                String edgeId = resultMsg.getContractId() + MSG_ID_SEPERATOR
                                + resultMsg.getTermId() + MSG_ID_SEPERATOR;

                edgeId += (resultMsg.isIsResponse()) ? MSG_ID_RESPONSE
                                                     : MSG_ID_REQUEST;

                resultSourceNodeMap.put(edgeId, node);
                resultMsgs.put(edgeId, resultMsg);
            }
        }
    }

    public void buildPath() {
        for (Node node : nodes) {
            SrcMsgsType srcMsgs = node.getTaskType().getSrcMsgs();
            if (srcMsgs == null) {
                continue;
            }
            for (SrcMsgType srcMsg : node.getTaskType().getSrcMsgs().getSrcMsg()) {
                String edgeId = srcMsg.getContractId() + MSG_ID_SEPERATOR
                                + srcMsg.getTermId() + MSG_ID_SEPERATOR;

                edgeId += (srcMsg.isIsResponse()) ? MSG_ID_RESPONSE
                                                  : MSG_ID_REQUEST;

                Node sourceNode = resultSourceNodeMap.get(edgeId);
                Edge edge = new Edge(edgeId, resultMsgs.get(edgeId), srcMsg, sourceNode);
                node.addInEdge(edge);
            }
        }
    }

    public void print() {
        for (Node node : nodes) {
            System.out.println(node.toString());
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
