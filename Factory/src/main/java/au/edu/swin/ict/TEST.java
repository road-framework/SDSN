package au.edu.swin.ict;

import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.edu.swin.ict.road.demarshalling.exceptions.CompositeDemarshallingException;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork;

/**
 * TODO - documentation
 */
public class TEST {

    public static void main(String args[]) {
        CompositeDemarshaller compositeDemarshaller = new CompositeDemarshaller();
        try {
            ServiceNetwork smc =
                    compositeDemarshaller.demarshalSMCBinding("C:\\software\\ROAD-2.1-tag\\Factory\\suburb.xsd");
            System.out.println(smc.getVirtualServiceNetwork().get(0).getProcess().get(0).getCoS());
        } catch (CompositeDemarshallingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ConsistencyViolationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CompositeInstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
