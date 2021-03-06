package au.edu.swin.ict.road.demarshalling;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.demarshalling.exceptions.CompositeDemarshallingException;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork;
import au.edu.swin.ict.serendip.core.Constants;
import au.edu.swin.ict.serendip.core.ModelProviderFactory;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.tool.gui.AdminView;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.xml.bind.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>CompositeDemarshaller</code> is responsible for taking a composite XML
 * description and using it to instantiate a runtime Java <code>Composite</code>
 * object. As well the location of the XML file it also requires the folder
 * location containing the rules files that will be used with the
 * <code>Composite</code>. JAXB is being used as the library for XML to Java
 * instantiation.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class CompositeDemarshaller {

    private static Logger log = Logger.getLogger(CompositeDemarshaller.class.getName());

    /**
     * Uses JAXB to demarshall an instance of the ROAD composite schemas and
     * returns a populated ROAD composite object.
     *
     * @param smcFile  the fully qualified file name of the instance of a composite
     *                 schema.
     * @param rulesDir
     * @return a populated ROAD composite.
     * @throws CompositeDemarshallingException if there is an error demarshalling xml data
     * @throws ConsistencyViolationException
     * @throws CompositeInstantiationException
     */
    public ServiceNetwork demarshalSMCBinding(String smcFile)
            throws CompositeDemarshallingException,
            ConsistencyViolationException, CompositeInstantiationException {

        if (log.isInfoEnabled()) {
            log.info("Starting instantiation of " + smcFile);
        }

        // JAXB code
        JAXBContext jc;
        CollectingValidationEventHandler handler = new CollectingValidationEventHandler();
        try {
            jc = JAXBContext.newInstance("au.edu.swin.ict.road.xml.bindings");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            unmarshaller.setEventHandler(handler);

            return (ServiceNetwork) unmarshaller.unmarshal(new File(smcFile));

            // /EofSerendip
        } catch (JAXBException e) {
            String errorMessage = "XML parse errors:";
            if (!handler.getMessages().isEmpty()) {
                for (String message : handler.getMessages()) {
                    errorMessage += "\n" + message;
                }

            }
            e.printStackTrace();
            throw new CompositeDemarshallingException(e.getMessage() + "\nMore:\n" + errorMessage);
        }
    }

    public Composite demarshalSMC(String smcFile)
            throws CompositeDemarshallingException,
            ConsistencyViolationException, CompositeInstantiationException {

        ServiceNetwork smcBinding = demarshalSMCBinding(smcFile);

        if (log.isInfoEnabled()) {
            log.info("Starting instantiation of " + smcFile);
        }

        // JAXB code

        // populate with JAXB binding
        Composite outputComposite = new Composite(smcBinding);

        // /Serendip
        if (ModelProviderFactory
                .getProperty(Constants.SERENDIP_SHOW_ADMIN_VIEW) != null) {
            if (ModelProviderFactory.getProperty(
                    Constants.SERENDIP_SHOW_ADMIN_VIEW).equalsIgnoreCase(
                    "TRUE")) {
                SerendipEngine serendipEngine = outputComposite
                        .getSerendipEngine();
                AdminView adminView = new AdminView(serendipEngine);
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Serendip adminview disabled. "
                            + "If you need to monitor processes set "
                            + Constants.SERENDIP_SHOW_ADMIN_VIEW + "=TRUE"
                            + "in serendip.properties file");
                }
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Serendip adminview disabled. "
                        + Constants.SERENDIP_SHOW_ADMIN_VIEW
                        + "Property not found."
                        + "If you need to monitor processes set "
                        + Constants.SERENDIP_SHOW_ADMIN_VIEW + "=TRUE"
                        + "in serendip.properties file");
            }
        }
        // /EofSerendip

        if (log.isInfoEnabled()) {
            log.info("Finished instantiation of " + smcFile);
        }

        return outputComposite;

    }

    // Private Class start
    private static class CollectingValidationEventHandler implements
            ValidationEventHandler {
        private List<String> messages = new ArrayList<String>();

        public List<String> getMessages() {
            return messages;
        }

        public boolean handleEvent(ValidationEvent event) {
            if (event == null)
                throw new IllegalArgumentException("event is null");

            // calculate the severity prefix and return value
            String severity = null;
            boolean continueParsing = false;
            switch (event.getSeverity()) {
                case ValidationEvent.WARNING:
                    severity = "Warning";
                    continueParsing = true; // continue after warnings
                    break;
                case ValidationEvent.ERROR:
                    severity = "Error";
                    continueParsing = true; // terminate after errors
                    break;
                case ValidationEvent.FATAL_ERROR:
                    severity = "Fatal error";
                    continueParsing = false; // terminate after fatal errors
                    break;
                default:
                    assert false : "Unknown severity.";
            }

            String location = getLocationDescription(event);
            String message = severity + " parsing " + location + " due to "
                    + event.getMessage();
            messages.add(message);

            return continueParsing;
        }

        private String getLocationDescription(ValidationEvent event) {
            ValidationEventLocator locator = event.getLocator();
            if (locator == null) {
                return "XML with location unavailable";
            }

            StringBuffer msg = new StringBuffer();
            URL url = locator.getURL();
            Object obj = locator.getObject();
            Node node = locator.getNode();
            int line = locator.getLineNumber();

            if (url != null || line != -1) {
                msg.append("line " + line);
                if (url != null)
                    msg.append(" of " + url);
            } else if (obj != null) {
                msg.append(" obj: " + obj.toString());
            } else if (node != null) {
                msg.append(" node: " + node.toString());
            }

            return msg.toString();
        }
    }
    // Private class end
}
