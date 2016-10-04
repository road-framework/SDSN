import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class InOnlyServiceXML {
    private static Logger log = Logger.getLogger(EchoService.class.getName());

    public void recordMessage(OMElement msg) {
        System.out.println("In Only : " + msg);
    }
}
