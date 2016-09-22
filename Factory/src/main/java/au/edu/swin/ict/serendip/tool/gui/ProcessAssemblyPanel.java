package au.edu.swin.ict.serendip.tool.gui;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.xml.bindings.CollaborationUnitType;
import au.edu.swin.ict.road.xml.bindings.CollaborationUnitsType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionType;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.epc.MergeBehavior;
import au.edu.swin.ict.serendip.serendip4saas.gui.SerendipJList;
import org.apache.log4j.Logger;
import org.processmining.framework.models.epcpack.ConfigurableEPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that allows assembing processes from behavior terms and view them
 *
 * @author Malinda
 */
public class ProcessAssemblyPanel extends JPanel implements ActionListener {
    static Logger log = Logger.getLogger(ProcessAssemblyPanel.class);
    private JButton selectBtn, removeBtn, viewBtn, modiBtn, epcBtn, saveBtn = null;
    private SerendipJList fromList, toList = null;
    private JScrollPane epcViewScrol = null;
    private Composite composite = null;
    private ProcessDefinitionType pdType = null;

    public ProcessAssemblyPanel(ProcessDefinitionType pdType, Composite composite) throws SerendipException {
        this.composite = composite;
        this.pdType = pdType;

        //Create UI
        this.setLayout(new BorderLayout());
        this.epcViewScrol = new JScrollPane();
        // Add controls view
        JPanel controlsPanel = new JPanel(new BorderLayout());

        // Add north label
        controlsPanel
                .add(
                        new JLabel(
                                "Select/Remove behavior terms to update the process view for " + this.pdType.getId()),
                        BorderLayout.NORTH);

        // Add south advanced controls
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(new JCheckBox("Hide abstracts", true));
        southPanel.add(new JButton("Refresh"));// new
        // ImageIcon("images/Refresh-icon.png")
        //southPanel.add(new JButton("Validate"));
        this.saveBtn = new JButton("Save");
        this.saveBtn.addActionListener(this);
        southPanel.add(this.saveBtn);
        controlsPanel.add(southPanel, BorderLayout.SOUTH);

        // Add East
        epcBtn = new JButton("}");
        this.epcBtn.addActionListener(this);
        epcBtn.setFont(new Font(Font.SERIF, Font.PLAIN, 88));
        controlsPanel.add(epcBtn, BorderLayout.EAST);
        // Add center lists
        JPanel listsPanel = new JPanel(new GridLayout(1, 3));
        controlsPanel.add(listsPanel, BorderLayout.CENTER);

        JPanel arrowBtnPanel = new JPanel(new GridLayout(8, 1));
        arrowBtnPanel.add(new JLabel(""));
        arrowBtnPanel.add(new JLabel(""));
        arrowBtnPanel.add(new JLabel(""));
        this.selectBtn = new JButton("Select >>");
        this.removeBtn = new JButton("<< Remove");
        this.viewBtn = new JButton("View");
        this.modiBtn = new JButton("Modify");
        this.selectBtn.addActionListener(this);
        this.removeBtn.addActionListener(this);
        this.viewBtn.addActionListener(this);
        this.modiBtn.addActionListener(this);

        arrowBtnPanel.add(selectBtn);
        arrowBtnPanel.add(removeBtn);
        arrowBtnPanel.add(new JLabel(""));
        arrowBtnPanel.add(viewBtn);
        arrowBtnPanel.add(modiBtn);

        // Populate fromList
        CollaborationUnitsType allBtRefsOfCompo = this.composite.getSmcBinding().getCollaborationUnits();
        List<CollaborationUnitType> allbtList = allBtRefsOfCompo.getCollaborationUnit();
        this.fromList = new SerendipJList("Available Behaviors", new String[]{});
        for (CollaborationUnitType btt : allbtList) {
            this.fromList.addItem(btt.getId());
        }

        //Populate toList. We set the already refed BTs in here
        List<String> btToIdList = pdType.getCollaborationUnitRef();
        this.toList = new SerendipJList("Selected Behaviors", new String[]{});
        for (String s : btToIdList) {
            this.fromList.removeItem(s);
            this.toList.addItem(s);
        }

        listsPanel.add(fromList);
        listsPanel.add(arrowBtnPanel);
        listsPanel.add(toList);


        this.createProcessView();

        //
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(controlsPanel);
        epcViewScrol.setAutoscrolls(false);
        mainPanel.add(epcViewScrol);
        this.add(mainPanel, BorderLayout.CENTER);

    }

    private void createProcessView() throws SerendipException {
        String[] btIds = this.toList.getAllData();
        // if there are no selected behavior terms we will send an empty JPanel
        if (btIds.length < 1) {
            if (log.isInfoEnabled()) {
                log.info("Cannot generate the view");
            }
            this.epcViewScrol.setViewportView(new JPanel());
            return;
        }
        //To collect the selected behaviour terms.
        ArrayList<BehaviorTerm> selectedBehaviorTerms = new ArrayList<BehaviorTerm>();
        for (String s : btIds) {
            CollaborationUnitsType btsType = this.composite.getSmcBinding().getCollaborationUnits();
            List<CollaborationUnitType> btList = btsType.getCollaborationUnit();
            for (CollaborationUnitType btt : btList) {
                if (btt.getId().equals(s)) {
                    BehaviorTerm b = new BehaviorTerm(btt, this.composite);
                    selectedBehaviorTerms.add(b);
                }
            }
        }

        ArrayList<ConfigurableEPC> epcArrayList = new ArrayList<ConfigurableEPC>();
        for (BehaviorTerm b : selectedBehaviorTerms) {
            ConfigurableEPC epc = b.constructEPC();
            epcArrayList.add(epc);
        }

        ConfigurableEPC[] epcs = new ConfigurableEPC[epcArrayList.size()];
        epcs = epcArrayList.toArray(epcs);

        ConfigurableEPC result = epcs[0];
        for (int i = 1; i < epcs.length; i++) {
            result = MergeBehavior.mergeEPC(result, epcs[i]);
        }

        SerendipEPCView epcView = new SerendipEPCView(this.pdType.getId(), result);

        this.epcViewScrol.setViewportView(epcView);


    }

    /**
     * Check if the selection is a valid selection or not depending on the values in toList
     *
     * @param sel
     * @return
     */
    private boolean isValidSelection(String sel) {

        for (String s : this.toList.getAllData()) {
            //Check if the selected item is child of a parent already in toList
            boolean isAChildOf = BehaviorTerm.isChildOf(this.composite.getSerendipEngine().getComposition(), sel, s);
            if (isAChildOf) {
                JOptionPane.showMessageDialog(this,
                                              "Invalid selection. Already a parent is  selected, PARENT ID=" + s,
                                              "Selection error",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }

            //Check if the selected item is a parent of a chid already in the toList (the otherway)
            boolean isParentOf = BehaviorTerm.isChildOf(this.composite.getSerendipEngine().getComposition(), s, sel);
            if (isParentOf) {
                JOptionPane.showMessageDialog(this,
                                              "Invalid selection. Already a child is selected, CHILD ID=" + s,
                                              "Selection error",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }


        //No issues
        return true;
    }

    private void saveChanges(String path) {


        //Remove already existing ones
        for (int i = 0; i < this.pdType.getCollaborationUnitRef().size(); i++) {
            this.pdType.getCollaborationUnitRef().remove(i);
        }

        //get the current selected behavior terms
        String[] selectedBTerms = this.toList.getAllData();
        //Add new ones
        for (int i = 0; i < selectedBTerms.length; i++) {
            String btId = selectedBTerms[i];
            this.pdType.getCollaborationUnitRef().add(btId);
        }

//        ((OrganiserRole) composite.getOrganiserRole()).takeSnapshot(path);
        log.debug("File saved to " + path);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(this.selectBtn)) {
            String selected = this.fromList.getSelectedItem();
            if (null != selected) {
                //Check if the selected item is a child of an item already in the list.
                if (this.isValidSelection(selected)) {
                    this.toList.addItem(selected);
                    this.fromList.removeItem(this.fromList.getSelectedIndex());
                } else {
                    //error in selection
                }
            }
        } else if (event.getSource().equals(this.removeBtn)) {
            String selected = this.toList.getSelectedItem();
            if (null != selected) {
                this.fromList.addItem(selected);
                this.toList.removeItem(this.toList.getSelectedIndex());
            }
        } else if (event.getSource().equals(this.epcBtn)) {

            try {
                this.createProcessView();
            } catch (SerendipException e) {
                // TODO Auto-generated catch block
                JOptionPane.showMessageDialog(this,
                                              "Cannot generate the EPC view",
                                              "EPC error",
                                              JOptionPane.ERROR_MESSAGE);
            }

        } else if (event.getSource().equals(this.viewBtn)) {

            String id = this.fromList.getSelectedItem();
            if (id != null) {

                CollaborationUnitType btType = this.composite.getSerendipEngine().getComposition().getCollaborationUnitTypeById(id);


                BehaviorTerm bt = null;
                try {
                    bt = new BehaviorTerm(btType, this.composite);
                } catch (SerendipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ConfigurableEPC epc = bt.constructEPC();
                if (null == epc) {
                    log.debug("EPC is null");
                }
                SerendipEPCView epcView = new SerendipEPCView(id, epc);
                SerendipJFrame frame = new SerendipJFrame(id);
                if (null != epcView) {
                    frame.getContentPane().add(epcView);
                } else {
                    frame.setTitle("Cannot generate the EPC view for " + id);
                }
                frame.setVisible(true);

            }

        } else if (event.getSource().equals(this.modiBtn)) {
            String id = this.fromList.getSelectedItem();
            if (id != null) {
                CollaborationUnitType btType = this.composite.getSerendipEngine().getComposition().getCollaborationUnitTypeById(id);

                BehaviorModifyPanel bmp = new BehaviorModifyPanel(btType);

                JFrame f = new JFrame();
                f.setTitle(id);
                f.setBounds(100, 100, 400, 200);
                f.add(bmp);
                f.setVisible(true);

            } else {
                log.error("Cannot identify BT " + id);
            }
        } else if (event.getSource().equals(this.saveBtn)) {
            final JFileChooser fc = new JFileChooser();
            String fileName = null;
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().getAbsolutePath();
                this.saveChanges(fileName);
            } else {
                return;// We Ignore
            }
        }

    }


}
