/**
 * GarageServiceCallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package stub;

/**
 * GarageServiceCallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class GarageServiceCallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public GarageServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public GarageServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for placeRepairOrder method
     * override this method for handling normal response from placeRepairOrder operation
     */
    public void receiveResultplaceRepairOrder(
            stub.GarageServiceStub.PlaceRepairOrderResponse result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from placeRepairOrder operation
     */
    public void receiveErrorplaceRepairOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for payRepair method
     * override this method for handling normal response from payRepair operation
     */
    public void receiveResultpayRepair(
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from payRepair operation
     */
    public void receiveErrorpayRepair(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for doRepair method
     * override this method for handling normal response from doRepair operation
     */
    public void receiveResultdoRepair(
            stub.GarageServiceStub.DoRepairResponse result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from doRepair operation
     */
    public void receiveErrordoRepair(java.lang.Exception e) {
    }


}
    