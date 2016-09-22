package au.edu.swin.ict.serendip.core.mgmt.action;


import au.edu.swin.ict.road.common.Parameter;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefSetInMsgOnTaskAction implements DefAdaptAction {
    private String operationName;
    private String operationReturnType;
    private boolean isResponse = false;
    private Parameter[] parameters;
    private String taskId;
    private String rId;

    public DefSetInMsgOnTaskAction(String operationName, String operationReturnType, boolean response, Parameter[] parameters, String taskId, String rId) {
        this.operationName = operationName;
        this.operationReturnType = operationReturnType;
        this.isResponse = response;
        this.parameters = parameters;
        this.taskId = taskId;
        this.rId = rId;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
//        OrganiserMgtOpResult res =
//                ((OrganiserRole)
//                        comp.getOrganiserRole()).setInMessageType(operationName, operationReturnType, parameters, taskId, rId);
//        return res.getResult();
        return false;
    }
}