package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.IOrganiserRole;
import au.edu.swin.ict.road.common.SMCDataExtractor;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.edu.swin.ict.road.demarshalling.exceptions.CompositeDemarshallingException;
import au.edu.swin.ict.serendip.core.mgmt.SerendipOrganizer;
import org.junit.Assert;

import java.io.FileNotFoundException;

/**
 * TODO documentation
 */
public class OrganizerTest {

    public static void main(String[] args) {
        CompositeDemarshaller dm = new CompositeDemarshaller();
        try {
            Composite composite =
                    dm.demarshalSMC("sample\\Scenario1\\RoSaS.xml");

            IOrganiserRole organiserRole = composite.getOrganiserRole();
            SerendipOrganizer serendipOrganizer = composite.getSerendipOrganizer();

            serendipOrganizer.addProcessGroupDefinition(
                    OMUtils.createOMElement("test\\resources\\slice.xml"));

            Assert.assertNotNull(composite.getSerendipEngine().getProcessDefinitionGroup("Tenant3"));
            Assert.assertNotNull(new SMCDataExtractor(
                    composite.getSmcBinding()).getProcessDefinitionGroup("Tenant3"));

            serendipOrganizer.addProcessDefinitionToGroup("Tenant3",
                    OMUtils.createOMElement("test\\resources\\process.xml"));

            Assert.assertNotNull(composite.getSerendipEngine().
                    getProcessDefinitionGroup("Tenant3").getProcessDefinition("PDBronzeT3"));
            Assert.assertNotNull(new SMCDataExtractor(
                    composite.getSmcBinding()).getProcessDefinition("Tenant3", "PDBronzeT3"));

            serendipOrganizer.removeProcessDefinitionFromGroup("Tenant3", "PDBronzeT3");

            Assert.assertNull(composite.getSerendipEngine().
                    getProcessDefinitionGroup("Tenant3").getProcessDefinition("PDBronzeT3"));
            Assert.assertNull(new SMCDataExtractor(
                    composite.getSmcBinding()).getProcessDefinition("Tenant3", "PDBronzeT3"));
            serendipOrganizer.removeProcessGroupDefinition("Tenant3");

            Assert.assertNull(composite.getSerendipEngine().getProcessDefinitionGroup("Tenant3"));
            Assert.assertNull(new SMCDataExtractor(
                    composite.getSmcBinding()).getProcessDefinitionGroup("Tenant3"));

        } catch (CompositeDemarshallingException e) {
            e.printStackTrace();
        } catch (ConsistencyViolationException e) {
            e.printStackTrace();
        } catch (CompositeInstantiationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
