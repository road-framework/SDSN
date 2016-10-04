import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import stub.Addtaxihire_14CabsStub;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.util.TimerTask;

/**
 * TODO
 */
public class TaxiTimerTask extends TimerTask {

    private Classifier classifier;

    public TaxiTimerTask(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void run() {
        try {
            Addtaxihire_14CabsStub cabsStub =
                    new Addtaxihire_14CabsStub("http://localhost:8080/axis2/services/t/" +
                                               classifier.getVsnId() + "/roadside_14cabs");
            Addtaxihire_14CabsStub.ProvideTaxiInvoiceRequest request = new Addtaxihire_14CabsStub.ProvideTaxiInvoiceRequest();
            request.setContent("40$");
            cabsStub._getServiceClient().addStringHeader(
                    new QName("http://servicenetwork.com", "vsnId"), classifier.getVsnId());
            cabsStub._getServiceClient().addStringHeader(
                    new QName("http://servicenetwork.com", "processId"), classifier.getProcessId());
            cabsStub._getServiceClient().addStringHeader(
                    new QName("http://servicenetwork.com", "processInstanceId"), classifier.getProcessInstanceId());
            cabsStub._getServiceClient().getOptions().setProperty(
                    HTTPConstants.SO_TIMEOUT, 600000000);
            cabsStub._getServiceClient().getOptions().setProperty(
                    HTTPConstants.CONNECTION_TIMEOUT, 932323434);
            Addtaxihire_14CabsStub.ProvideTaxiInvoiceResponse response = cabsStub.provideTaxiInvoice(request);
            System.out.println(response.get_return());
        }
        catch (AxisFault ignored) {
        }
        catch (RemoteException ignored) {
        }
    }
}
