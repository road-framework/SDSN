import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;

public class NoParameterServiceXML {
    private static Logger log = Logger.getLogger(EchoService.class.getName());

    public OMElement sayHello() throws XMLStreamException {
        String response = "<sayHelloResponse><return> Hello ? </return></sayHelloResponse>";
        return new StAXOMBuilder(new ByteArrayInputStream(response.getBytes())).getDocumentElement();
    }
}
