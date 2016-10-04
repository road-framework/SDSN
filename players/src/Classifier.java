/**
 * TODO
 */
public class Classifier {

    private String vsnId;
    private String processId;
    private String processInstanceId;

    public String getVsnId() {
        return vsnId;
    }

    public void setVsnId(String vsnId) {
        this.vsnId = vsnId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String toString() {
        return "[ VSN Id : " + vsnId + "; Process Id : " + processId + "; ProcessInsId : " + processInstanceId + " ]";
    }
}
