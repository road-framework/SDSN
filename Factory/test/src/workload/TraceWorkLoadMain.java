package workload;


import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.edu.swin.ict.road.demarshalling.exceptions.CompositeDemarshallingException;
import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContextFactory;

/**
 * TODO documentation
 */
public class TraceWorkLoadMain {

    public static void main(String args[]) throws AxisFault,
            CompositeDemarshallingException, CompositeInstantiationException, ConsistencyViolationException {
        MessagePusher messagePusher = new MessagePusher(
                ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
                "AXIS2PushListener", false);
        CompositeDemarshaller dm = new CompositeDemarshaller();
        Composite composite =
                dm.demarshalSMC("sample\\p3casestudy\\p3casestudy2.xml");
        for (Role role : composite.getRoleMap().values()) {
            role.registerNewPushListener(messagePusher);
        }
        Thread compo = new Thread(composite);
        compo.start();
        String fileName;
        int cNo;
        if (args.length == 0) {
            fileName = "test/resources/TestWorkLoad2.xlsx";
            cNo = 3;
        } else if (args.length == 1) {
            fileName = args[0];
            cNo = 3;
        } else {
            fileName = args[0];
            cNo = Integer.parseInt(args[1]);
        }
        TraceWorkLoad traceWorkLoad = new TraceWorkLoad();
        traceWorkLoad.build(fileName, cNo);
        WorkloadExecutor workloadExecutor = new WorkloadExecutor(traceWorkLoad, composite);
        workloadExecutor.execute();
    }
}
