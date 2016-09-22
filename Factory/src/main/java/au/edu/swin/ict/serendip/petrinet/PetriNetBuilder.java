package au.edu.swin.ict.serendip.petrinet;

import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.log4j.Logger;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.epcpack.EPCEvent;
import org.processmining.framework.models.epcpack.algorithms.EPCToPetriNetConverter;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class PetriNetBuilder {
    static Logger log = Logger.getLogger(PetriNetBuilder.class);

    public static PetriNet epcToPetriNet(ConfigurableEPC epc)
    throws SerendipException {
        PetriNet pn = null;
        if (null == epc) {
            throw new SerendipException("No model is available");
        }

        if (log.isDebugEnabled()) {
            log.debug("Converting EPC: " + epc.getIdentifier());
        }
        // Conversion
        // EPCToPetriNetConverterPlugin epcToPNplugin = new
        // EPCToPetriNetConverterPlugin();
        // pn = epcToPNplugin.convert(epc);

        pn = EPCToPetriNetConverter.convert(epc, new HashMap(), null, null);

        // DEBUG
        Iterator<EPCEvent> events = epc.getEvents().iterator();
        while (events.hasNext()) {
            if (log.isDebugEnabled()) {
                log.debug(">" + events.next().getIdentifier());
            }
        }

        Iterator<Place> places = pn.getPlaces().iterator();
        while (places.hasNext()) {
            if (log.isDebugEnabled()) {
                log.debug("]" + places.next().getIdentifier());
            }
        }
        // end DEBUG
        if (null == pn) {
            throw new SerendipException(
                    "Cannot get the equivalent petrinet of " + epc.getId());
        }
        pn.setName(epc.getIdentifier());

        return pn;
    }

    public static void writeToFile(PetriNet pn, String fileName,
                                   String[] initPlaces) throws IOException {
        FileWriter fw = new FileWriter(fileName, false);
        BufferedWriter bw = new BufferedWriter(fw);
        PetriNetWriter.write(pn, bw, fileName, initPlaces);
        bw.close();
        if (log.isDebugEnabled()) {
            log.debug("Petrinet written to " + fileName);
        }

    }
}
