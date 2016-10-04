import org.apache.log4j.Logger;

public class NoParameterServiceRPC {
    private static Logger log = Logger.getLogger(EchoService.class.getName());

    public String sayHello() {
        return " Hello ?";
    }
}
