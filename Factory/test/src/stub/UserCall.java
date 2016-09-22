package stub;

import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;

/**
 * TODO documentation
 */
public class UserCall implements Runnable {
    @Override
    public void run() {
        try {
            GarageServiceStub garageServiceStub =
                    new GarageServiceStub("http://localhost:8080/axis2/services/GarageService");
            GarageServiceStub.DoRepair doRepair = new GarageServiceStub.DoRepair();
            doRepair.setContent("Need Repair");
            stub.GarageServiceStub.DoRepairResponse doRepairResponse =
                    garageServiceStub.doRepair(doRepair);
            System.out.println(doRepairResponse.get_return());
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
