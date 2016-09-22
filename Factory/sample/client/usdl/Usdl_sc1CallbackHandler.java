/**
 * Usdl_sc1CallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */

package usdl;

/**
 * Usdl_sc1CallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class Usdl_sc1CallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public Usdl_sc1CallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Usdl_sc1CallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for getServiceCapabilityAnon method
     * override this method for handling normal response from getServiceCapabilityAnon operation
     */
    public void receiveResultgetServiceCapabilityAnon(
            usdl.Usdl_sc1Stub.GetCapabilityReturn result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceCapabilityAnon operation
     */
    public void receiveErrorgetServiceCapabilityAnon(java.lang.Exception e) {
    }


}
    