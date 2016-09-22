/**
 * Usdl_sc2CallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */

package usdl;

/**
 * Usdl_sc2CallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class Usdl_sc2CallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public Usdl_sc2CallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Usdl_sc2CallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for getUserInvocationStatFact method
     * override this method for handling normal response from getUserInvocationStatFact operation
     */
    public void receiveResultgetUserInvocationStatFact(
            usdl.Usdl_sc2Stub.Fact result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getUserInvocationStatFact operation
     */
    public void receiveErrorgetUserInvocationStatFact(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceCapability method
     * override this method for handling normal response from getServiceCapability operation
     */
    public void receiveResultgetServiceCapability(
            usdl.Usdl_sc2Stub.GetCapabilityReturn result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceCapability operation
     */
    public void receiveErrorgetServiceCapability(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getAllUserInvocationStatFacts method
     * override this method for handling normal response from getAllUserInvocationStatFacts operation
     */
    public void receiveResultgetAllUserInvocationStatFacts(
            usdl.Usdl_sc2Stub.Facts result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getAllUserInvocationStatFacts operation
     */
    public void receiveErrorgetAllUserInvocationStatFacts(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceTechnicalInterface method
     * override this method for handling normal response from getServiceTechnicalInterface operation
     */
    public void receiveResultgetServiceTechnicalInterface(
            usdl.Usdl_sc2Stub.GetTechnicalInterfaceReturn result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceTechnicalInterface operation
     */
    public void receiveErrorgetServiceTechnicalInterface(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceUser method
     * override this method for handling normal response from getServiceUser operation
     */
    public void receiveResultgetServiceUser(
            usdl.Usdl_sc2Stub.GetServiceReturn result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceUser operation
     */
    public void receiveErrorgetServiceUser(java.lang.Exception e) {
    }


}
    