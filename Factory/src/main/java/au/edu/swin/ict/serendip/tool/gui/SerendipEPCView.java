package au.edu.swin.ict.serendip.tool.gui;

import au.edu.swin.ict.serendip.composition.view.SerendipView;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.epc.EPMLWriter;
import au.edu.swin.ict.serendip.petrinet.PetriNetBuilder;
import org.apache.log4j.Logger;
import org.processmining.analysis.petrinet.PetriNetAnalysisUI;
import org.processmining.framework.models.ModelGraphPanel;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SerendipEPCView extends JPanel implements ActionListener {
    static Logger logger = Logger.getLogger(SerendipEPCView.class);
    private String id = null;
    // private ModelProviderFactory mpv = null;
    private ConfigurableEPC epc = null;
    private JButton btnSaveTPN = new JButton("Export petri net");
    private JButton btnShowPN = new JButton("Show petri net");
    private JButton btnValidate = new JButton("Validate");
    private JPanel btnPane = new JPanel(new FlowLayout());

    public SerendipEPCView(SerendipView serendipView) {
        this(serendipView.getId(), serendipView.getViewAsEPC());
    }

    public SerendipEPCView(String id, ConfigurableEPC epc) {
        this.id = id;
        this.epc = epc;
        this.createView();
    }

    public void saveEPML(String fileName) throws IOException {
        EPMLWriter epmlWriter = new EPMLWriter(this.epc, true);
        epmlWriter.writeToFile(fileName);
    }

    private void createView() {
        if (null == this.epc) {
            logger.error("Cannot find EPC for " + this.id);
            return;
        }

        EPMLWriter epmlWriter = new EPMLWriter(this.epc, true);
        ModelGraphPanel mgp = epmlWriter.getModelGraphPanel();

        // mgp.setEnabled(true);
        mgp.setScaleToFit(true);
        this.setLayout(new BorderLayout());

        this.btnSaveTPN.addActionListener(this);
        this.btnShowPN.addActionListener(this);
        this.btnPane.add(this.btnSaveTPN);
        this.btnPane.add(this.btnShowPN);
        this.btnPane.add(this.btnValidate);
        this.add(mgp, BorderLayout.CENTER);
        this.add(this.btnPane, BorderLayout.SOUTH);// Use this if u need to
        // export an EPC to PN
        this.setBorder(BorderFactory.createTitledBorder(this.id));

    }

    private void showPN(PetriNet pn) {
        PetriNetAnalysisUI pnUI = new PetriNetAnalysisUI(pn);
        SerendipJFrame frame = new SerendipJFrame("Petrinet of " + this.id);
        frame.getContentPane().add(pnUI);

        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO Auto-generated method stub
        if (event.getSource().equals(this.btnSaveTPN)) {
            logger.debug("Writing petri net " + this.id);
            //Get the location
            final JFileChooser fc = new JFileChooser();
            String fileName = null;
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().getAbsolutePath();
            } else {
                //We Ignore
                return;
            }

            PetriNet pn = null;
            try {
                pn = PetriNetBuilder.epcToPetriNet(this.epc);


//				String fileName = this.engine.getModelFactory()
//						.getTempFileDirectory()
//						+ "/" + this.id + Constants.SERENDIP_PETRINET_FILE_EXT;
                PetriNetBuilder.writeToFile(pn, fileName, null);
                JOptionPane.showMessageDialog(this,
                        "File successfully saved to " + fileName, "Export",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SerendipException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        } else if (event.getSource().equals(this.btnShowPN)) {
            logger.debug("Showing petri net " + this.id);
            PetriNet pn = null;
            try {
                pn = PetriNetBuilder.epcToPetriNet(this.epc);
                this.showPN(pn);
            } catch (SerendipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


}
