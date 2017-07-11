package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class RegulationMechanism {
    private final RegMecManagementState mgtState;
    private String id;
    private String jarFileLocation;
    private String className;

    public RegulationMechanism(String id) {
        this.id = id;
        this.mgtState = new RegMecManagementState(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJarFileLocation() {
        return jarFileLocation;
    }

    public void setJarFileLocation(String jarFileLocation) {
        this.jarFileLocation = jarFileLocation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public RegMecManagementState getMgtState() {
        return mgtState;
    }
}
