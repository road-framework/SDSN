import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface CaseOfficerProxy {

    public AnalyzeReturn analyze(String memId, String complainDetails, long avgTime) throws AxisFault;

    public String payTC(String content, long avgTime) throws AxisFault;

    public String payGC(String content, long avgTime) throws AxisFault;

    public String payPS(String content, long avgTime) throws AxisFault;

    public String payVC(String content, long avgTime) throws AxisFault;

    public String payHC(String content, long avgTime) throws AxisFault;

    public String payTX(String content, long avgTime) throws AxisFault;

    public String payLF(String content, long avgTime) throws AxisFault;
}
