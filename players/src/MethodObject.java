import java.lang.reflect.Method;

public class MethodObject {
    private Method method;
    private boolean noParameter;

    public MethodObject(Method method, boolean noParameter) {
        this.method = method;
        this.noParameter = noParameter;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isNoParameter() {
        return noParameter;
    }

    public void setNoParameter(boolean noParameter) {
        this.noParameter = noParameter;
    }
}
