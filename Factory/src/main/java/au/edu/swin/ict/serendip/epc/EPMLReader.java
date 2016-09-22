package au.edu.swin.ict.serendip.epc;

import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.mining.epcmining.EPCResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EPMLReader {

    public static ConfigurableEPC getEPCFromFile(File file)
    throws FileNotFoundException, IOException {
        return readFile(file).getEPC();
    }

    public static EPCResult readFile(File file) throws FileNotFoundException,
    IOException {
        if (null == file) {
            return null;
        }
        SerendipEPMLImport importplugin = new SerendipEPMLImport();

        EPCResult epcResult = (EPCResult) importplugin
                .importFile(new FileInputStream(file));

        return epcResult;
    }

}
