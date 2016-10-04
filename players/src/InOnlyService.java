import org.apache.log4j.Logger;

public class InOnlyService {
    private static Logger log = Logger.getLogger(EchoService.class.getName());

    public void recordMessage(String msg) {
        System.out.println("In Only : " + msg);
    }
}
