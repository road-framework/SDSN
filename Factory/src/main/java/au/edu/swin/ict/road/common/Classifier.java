package au.edu.swin.ict.road.common;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * TODO documentation
 */
public class Classifier implements Serializable {

    private String vsnId;
    private String processId;
    private String processRole;
    private String processInsId;

    public Classifier(String vsnId, String processId, String processInsId) {
        this.processId = processId;
        this.processInsId = processInsId;
        this.vsnId = vsnId;
    }

    public Classifier() {
    }

    public Classifier(String processInsId) {
        this.processInsId = processInsId;
    }

    public Classifier(String vsnId, String processId, String processInsId, String processRole) {
        this.vsnId = vsnId;
        this.processId = processId;
        this.processRole = processRole;
        this.processInsId = processInsId;
    }

    public Classifier(String vsnId, String processId) {
        this.vsnId = vsnId;
        this.processId = processId;
    }

    public String getProcessInsId() {
        return processInsId;
    }

    public void setProcessInsId(String processInsId) {
        this.processInsId = processInsId;
    }

    public String getProcessRole() {

        return processRole;
    }

    public void setProcessRole(String processRole) {
        this.processRole = processRole;
    }

    public String getVsnId() {
        return vsnId;
    }

    @XmlElement(name = "VsnId")
    public void setVsnId(String vsnId) {
        this.vsnId = vsnId;
    }

    @XmlElement(name = "ProcessId")
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Override
    public String toString() {
        return "Classifier{" +
                "vsnId='" + vsnId + '\'' +
                ", processId='" + processId + '\'' +
                ", processRole='" + processRole + '\'' +
                ", processInsId='" + processInsId + '\'' +
                '}';
    }
}
