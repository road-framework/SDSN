package au.edu.swin.ict.serendip.epc.test;

import au.edu.swin.ict.serendip.epc.EPMLReader;
import org.apache.log4j.Logger;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.epcpack.EPCFunction;
import org.processmining.mining.epcmining.EPCResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TestEPMLRead {
    private static Logger log = Logger.getLogger(TestEPMLRead.class.getName());

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
        log.debug(epc.getIdentifier());
        ArrayList<EPCFunction> funcs = epc.getFunctions();
        for (int i = 0; i < funcs.size(); i++) {
            log.debug(funcs.get(i).getIdentifier() + " "
                      + funcs.get(i).getId());
        }
    }
}
