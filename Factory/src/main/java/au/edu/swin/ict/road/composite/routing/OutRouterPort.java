package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.xml.bindings.ResultMsgType;

/**
 * TODO documentation
 */

public class OutRouterPort {
    public ResultMsgType outInterface;
    private String interaction;
    private String desContract;

    public OutRouterPort(String desContract) {
        this.desContract = desContract;
    }

    public OutRouterPort(ResultMsgType outInterface) {
        this.outInterface = outInterface;
        this.desContract = outInterface.getContractId();
        this.interaction = outInterface.getTermId();
    }

    public String getDesContract() {
        return desContract;
    }

    public ResultMsgType getOutInterface() {
        return outInterface;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }
}
