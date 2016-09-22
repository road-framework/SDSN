package au.edu.swin.ict.serendip.epc.test;

import au.edu.swin.ict.serendip.epc.EPCToSerendip;
import au.edu.swin.ict.serendip.epc.EPMLReader;
import org.apache.log4j.Logger;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.mining.epcmining.EPCResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestEPCtoSrendip {
    private static Logger log = Logger.getLogger(TestEPCtoSrendip.class.getName());

    public static void main(String[] args) {
        EPCResult result = null;
        File file = new File("sample/xml/epml/MM-CO_B1.epml");
        try {
            result = EPMLReader.readFile(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ConfigurableEPC epc = result.getEPC();
        log.debug(" EPC Id" + epc.getIdentifier());

        EPCToSerendip.convrtEPCToSerendip(null, null, epc, null);
    }
}
