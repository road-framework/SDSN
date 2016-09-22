package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class SNStateImplementation {
    private String id;
    private String jarFileLocation;
    private String className;
    private SNStateImplManagementState mgtState;

    public SNStateImplementation(String id) {
        this.id = id;
        mgtState = new SNStateImplManagementState(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJarFileLocation() {
        return jarFileLocation;
    }

    public void setJarFileLocation(String jarFileLocation) {
        this.jarFileLocation = jarFileLocation;
    }

    public SNStateImplManagementState getMgtState() {
        return mgtState;
    }
}
