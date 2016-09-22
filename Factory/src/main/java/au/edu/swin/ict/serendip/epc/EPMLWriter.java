package au.edu.swin.ict.serendip.epc;

import att.grappa.*;
import org.apache.log4j.Logger;
import org.processmining.exporting.epcs.EpmlExport;
import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphPanel;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.plugin.ProvidedObject;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Export an EPC to EPML format
 *
 * @author Malinda Kapuruge
 */
public class EPMLWriter {
    static Logger logger = Logger.getLogger(EPMLWriter.class);
    EpmlExport epcexport = new EpmlExport();
    ConfigurableEPC epc = null;
    private ModelGraphPanel modelGraphPanel = null;

    public EPMLWriter(ConfigurableEPC epc, boolean withVisualArrange) {

        if (withVisualArrange) {
            try {
                this.epc = this.arrange(epc);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (null == this.epc.visualObject) {
                logger.debug("Visual arrangement unsuccessful ");
            } else {
                logger.debug("Visual arrangement successful ");
            }
        } else {
            this.epc = epc;
        }

    }

    private ConfigurableEPC arrange(ConfigurableEPC epc) throws Exception {
        File dotFile;
        BufferedWriter bw;
        Graph graph;
        Parser parser;
        // create temporary DOT file
        dotFile = File.createTempFile("pmt", ".dot");
        dotFile.deleteOnExit();
        bw = new BufferedWriter(new FileWriter(dotFile, false));
        epc.writeToDot(bw);// writeToDot(bw);
        bw.close();

        logger.debug("Executing dot file " + dotFile.getAbsolutePath());

        Process dot = Runtime.getRuntime().exec(
                "dot" + System.getProperty("file.separator") + "dot.exe -q5 \""
                + dotFile.getAbsolutePath() + "\"");

        parser = new Parser(dot.getInputStream(), System.err);
        parser.parse();
        graph = parser.getGraph();

        dot.destroy();

        modelGraphPanel = new ModelGraphPanel(graph, epc);
        modelGraphPanel.setScaleToFit(true);

        epc.visualObject = graph;

        return epc;
    }

    /**
     * For visualization purposes
     *
     * @return ModelGraphPanel is a JPanel
     */
    public ModelGraphPanel getModelGraphPanel() {
        return modelGraphPanel;
    }

    public void writeToFile(String fileName) throws IOException {
        File expfile = new File(fileName);
        FileOutputStream expstream = new FileOutputStream(expfile);

        this.writeToStream(expstream);
        expstream.close();
        logger.debug("EPML written to " + expfile.getAbsolutePath());
    }

    public void writeToStream(OutputStream os) {
        try {
            epcexport.export(new ProvidedObject("EPC Hierarchy",
                                                new Object[]{this.epc}), os);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void buildNodeMapping(Subgraph graph) {
        HashMap nodeMapping = new HashMap();// EMPTY ERROR?
        // First, enumerate the nodes on this level
        Enumeration e = graph.nodeElements();
        while (e.hasMoreElements()) {
            Node n = (Node) e.nextElement();
            n.object = nodeMapping.get(n.getName());
            if ((n.object == null) || !(n.object instanceof ModelGraphVertex)) {
                continue;
            }
            ((ModelGraphVertex) n.object).visualObject = n;
        }

        // Now enumerate the nodes on lower levels
        e = graph.subgraphElements();
        while (e.hasMoreElements()) {
            Subgraph g = (Subgraph) e.nextElement();
            buildNodeMapping(g);
        }
    }

    private void buildEdgeMapping(Subgraph graph) {
        Enumeration e = graph.edgeElements();
        while (e.hasMoreElements()) {
            Edge edge = (Edge) e.nextElement();
            Node n1 = edge.getTail();
            Node n2 = edge.getHead();

            ModelGraphVertex v1 = (ModelGraphVertex) n1.object;
            ModelGraphVertex v2 = (ModelGraphVertex) n2.object;

            if ((v1 == null) || (v1.getOutEdges() == null) || (v2 == null)) {
                continue;
            }

            ModelGraphEdge edge2 = null;
            Iterator it = v1.getOutEdges().iterator();
            boolean found = false;
            while (!found && it.hasNext()) {
                edge2 = (ModelGraphEdge) it.next();
                found = (edge2.getDest() == v2) && (edge2.visualObject == null);
            }
            edge.object = edge2;
            if (edge2 != null) {
                edge2.visualObject = edge;
            }
        }

        // Now enumerate the edges on lower levels
        e = graph.subgraphElements();
        while (e.hasMoreElements()) {
            Subgraph g = (Subgraph) e.nextElement();
            buildEdgeMapping(g);
        }
    }

}
