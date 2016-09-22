package au.swin.ict.research.cs3.road.road4ws.message;

import org.apache.axiom.soap.SOAPEnvelope;

public class ResponseMsg {
    private boolean successDelivery = false;
    private SOAPEnvelope envelope = null;
    private String soapAction;

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public ResponseMsg() {
        super();
    }


    public ResponseMsg(SOAPEnvelope envelope, boolean successDelivery) {
        super();
        this.successDelivery = successDelivery;
        this.envelope = envelope;
    }

    public boolean isSuccessDelivery() {
        return successDelivery;
    }

    public void setSuccessDelivery(boolean successDelivery) {
        this.successDelivery = successDelivery;
    }

    public SOAPEnvelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(SOAPEnvelope envelope) {
        this.envelope = envelope;
    }


}
