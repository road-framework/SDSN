package au.edu.swin.ict.serendip.tool.gui;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionsType;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProcessAssemblyPanelGroup extends JPanel implements ActionListener {
    static Logger logger = Logger.getLogger(ProcessAssemblyPanelGroup.class);
    private Composite composite = null;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private List<BehaviorTerm> behaviorTerms = null;


    public ProcessAssemblyPanelGroup(Composite composition) {
        this.composite = composition;
        try {
            this.createUI();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SerendipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * Add existing processes to the UI
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     * @throws SerendipException
     */
    private void createUI() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException, SerendipException {

        this.setLayout(new BorderLayout());
        this.add(this.tabbedPane, BorderLayout.CENTER);
        List<ProcessDefinitionsType> pDefsTypes = this.composite.getSmcBinding().getVirtualServiceNetwork();
        for (ProcessDefinitionsType pDefsType : pDefsTypes) {
            //Create a tab for each and every PD
            for (ProcessDefinitionType pdType : pDefsType.getProcess()) {
                JPanel assembler = new ProcessAssemblyPanel(pdType, this.composite);
                tabbedPane.addTab(pdType.getId(), null, assembler, pdType.getId());
            }
        }

    }


}
