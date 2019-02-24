package au.edu.swin.ict.road.composite.message.analyzer;

import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper.SyncType;
import au.edu.swin.ict.road.xml.bindings.ResultMsgType;
import au.edu.swin.ict.road.xml.bindings.TaskType;


/**
 * This class holds all the information that is required to perform either
 * the disjunct or conjunct transformations. The instance of this class is
 * prepared by the InTransform and OutTransform classes and passed to the
 * disjunct and conjunct methods of the MessageAnalyzer.
 *
 * @author Saichander Kukunoor (saichanderreddy@gmail.com)
 */

public class TransformLogic {
    private Role role;
    private String deliveryType;
    private TaskType task;
    private String operationName;
    private String xsltFile;
    private String contractId;
    private String interactionId;
    private String targetMsgId;
    private boolean isResponse;
    private SyncType syncType;
    private ResultMsgType resultMsgType;

    public TransformLogic() {
        role = null;
        deliveryType = null;

        task = null;

    }

    public ResultMsgType getResultMsgType() {
        return resultMsgType;
    }

    public void setResultMsgType(ResultMsgType resultMsgType) {
        this.resultMsgType = resultMsgType;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public TaskType getTask() {
        return task;
    }

    public void setTask(TaskType task) {
        this.task = task;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean isResponse) {
        this.isResponse = isResponse;
    }

    public SyncType getSyncType() {
        return syncType;
    }

    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }

    public String getXsltFile() {
        return xsltFile;
    }

    public void setXsltFile(String xsltFile) {
        this.xsltFile = xsltFile;
    }

    public String getTargetMsgId() {
        return targetMsgId;
    }

    public void setTargetMsgId(String targetMsgId) {
        this.targetMsgId = targetMsgId;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public void setIsResponse(boolean isResponse) {
        this.isResponse = isResponse;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
}
