package au.edu.swin.ict.serendip.epc;

import org.apache.log4j.Logger;
import org.processmining.mining.epcmining.EPCResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EPMLBehavior {
    private static final String EXT = ".epml";
    static Logger logger = Logger.getLogger(EPMLBehavior.class);
    private String epmllocation = "";
    private String[] btIds = null;
    private List<EPCResult> resultsVec = new ArrayList<EPCResult>();

    public EPMLBehavior(String epmllocation, String[] btIds) {
        this.epmllocation = epmllocation;
        this.btIds = btIds;
    }

    public static void createEmptyBehaviors(String epmllocation, String[] btIds)
            throws IOException {
        // TODO: Create empty epml files for the given btIds array. Later users
        // can load them using the editor and define the interactions
        for (int i = 0; i < btIds.length; i++) {
            createEmptyBehavior(epmllocation, btIds[i]);

        }
    }

    public static void createEmptyBehavior(String epmllocation, String btId)
            throws IOException {

        // Create a file for each behavior id if they do not exist
        File file = new File(epmllocation + "/" + btId + EXT);
        if (file.exists()) {
            logger.debug("We do not create " + file.getPath()
                    + " as it already exists");
            return;
        }
        FileWriter fstream = new FileWriter(file.getAbsolutePath());

        BufferedWriter out = new BufferedWriter(fstream);
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<epml:epml xmlns:epml=\"http://www.epml.de\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"" + "epml_1_draft.xsd\">\n"
                + "<epc EpcId=\"1\" Name=\"EPC\">\n" + "</epc>\n"
                + "</epml:epml>\n");
        out.close();
    }

    // Translate the epml files to equivalent petrinets.
    public void translateAll() throws FileNotFoundException, IOException {
        File file = null;
        EPCResult result = null;

        for (int i = 0; i < btIds.length; i++) {
            file = new File(epmllocation + btIds[i] + EXT);
            result = EPMLReader.readFile(file);
            resultsVec.add(result);
        }
    }

    public List<EPCResult> getEPCResults() {
        return this.resultsVec;
    }

}
