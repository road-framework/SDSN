package au.edu.swin.ict.serendip.tool.designer;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.edu.swin.ict.road.demarshalling.exceptions.CompositeDemarshallingException;
import au.edu.swin.ict.serendip.core.ModelProviderFactory;
import au.edu.swin.ict.serendip.tool.gui.CompositionView;
import au.edu.swin.ict.serendip.tool.gui.EPCViewSelectionPanel;
import au.edu.swin.ict.serendip.tool.gui.ProcessAssemblyPanelGroup;
import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.Document;
import com.jaxfront.core.dom.DocumentCreationException;
import com.jaxfront.core.schema.SchemaCreationException;
import com.jaxfront.core.util.URLHelper;
import com.jaxfront.swing.ui.editor.EditorPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The designer for Serendip
 * TODO: Sync the save action with rest of the GUI by reloading
 *
 * @author Malinda
 *         *
 */
public class DesignerFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(DesignerFrame.class);
    private final JFileChooser fc = new JFileChooser(
            ModelProviderFactory.getProperty("DEFAULT_LOC"));
    ;
    private File file = null;
    private Composite composite = null;
    private JTabbedPane tabbedPane = null;
    private JMenuBar menuBar;
    private JMenu fileMenu, toolMenu, helpMenu;
    private JMenuItem openItem, newItem, reloadItem, validateProcessManuItem, exitItem;
    private JMenuItem helpManuItem, aboutManuItem;

    public DesignerFrame() {
        this.setLookNFeel();
        this.setTitle("Serendip designer view");
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800, 720);

        // Create GUI
        this.createMenus();// TODO
        // Add tabbed pane
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.setContentPane(this.tabbedPane);
    }

    public static void main(String[] args) {
        new DesignerFrame();
    }

    private void createMenus() {

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        toolMenu = new JMenu("Tools");
        menuBar.add(toolMenu);
        // shift to the right
        menuBar.add(Box.createGlue());
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        // File Menu

        openItem = new JMenuItem("Open");
        openItem.addActionListener(this);
        fileMenu.add(openItem);

        newItem = new JMenuItem("New");
        newItem.addActionListener(this);
        fileMenu.add(newItem);

        reloadItem = new JMenuItem("Reload");
        reloadItem.addActionListener(this);
        fileMenu.add(reloadItem);

        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        //Tools Menu
        validateProcessManuItem = new JMenuItem("Validate Processes");
        validateProcessManuItem.addActionListener(this);
        toolMenu.add(validateProcessManuItem);
        // Help Menu
        helpManuItem = new JMenuItem("Help");
        helpManuItem.addActionListener(this);
        helpMenu.add(helpManuItem);
        aboutManuItem = new JMenuItem("About");
        aboutManuItem.addActionListener(this);
        helpMenu.add(aboutManuItem);

        this.setJMenuBar(menuBar);
    }

    private void loadComposite(File file) throws CompositeDemarshallingException, ConsistencyViolationException, CompositeInstantiationException {
        //if(true){return;}//To avoid delay in composite loading for UI testing
        if ((file == null) || (!file.exists())) {
            JOptionPane.showMessageDialog(this,
                    "Invalid file",
                    "File Selection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        CompositeDemarshaller demarsheller = new CompositeDemarshaller();

        this.composite = demarsheller.demarshalSMC(file.getAbsolutePath());
        if (null == composite) {
            log.error("Cannot instantiate the composite from file "
                    + file.getAbsoluteFile());
        }

        log.debug(this.composite.getName());
    }

    /**
     * Create a JaxFront panel
     *
     * @param xsdFile
     * @param xmlFile
     * @param xuiFile
     */
    private void addJAXPane(String xsdFile, String xmlFile, String xuiFile) {

        Document dom = null;
        try {
            dom = DOMBuilder.getInstance().build("default-context", URLHelper.getUserURL(xsdFile), URLHelper.getUserURL(xmlFile), URLHelper.getUserURL(xuiFile), null);

            EditorPanel editorPanel = new EditorPanel(dom.getRootType(), null);

            this.add(editorPanel);
            this.tabbedPane.addTab("Editor", editorPanel);

        } catch (SchemaCreationException e1) {
            e1.printStackTrace();
        } catch (DocumentCreationException e1) {
            e1.printStackTrace();
        }

    }

    private void updateUI() {
        // First of all we remove all the tabs
        this.tabbedPane.removeAll();
        // Then we set the title
        this.setTitle("Serendip Designer View : " + this.composite.getName());
        // Then we add an overview
        this.tabbedPane.add("Overview", new CompositionView(this.composite.getSerendipEngine()));
        //Then we add JAXPanel
        //this.addJAXPane("schemas/smc.xsd",this.file.getAbsolutePath(), "xui/serendip.xui");
        // Then we add the tab to see process views
        this.tabbedPane.add("Process Views", new EPCViewSelectionPanel(this.composite.getSerendipEngine(), true));
        //Then we add the process assmbers (group) for each process
        this.tabbedPane.add("Process Assembler", new ProcessAssemblyPanelGroup(this.composite));

    }

    public void exit() {

        // TODO: Persistance
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.exitItem)) {
            this.exit();
        } else if (e.getSource().equals(this.openItem)) {

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                try {
                    this.file = file;
                    this.loadComposite(file);
                    this.updateUI();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        } else if (e.getSource().equals(this.newItem)) {
            //Create a brand new file and load it
        } else if (e.getSource().equals(this.reloadItem)) {
            try {

                int res = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you need to reload?",
                        "Reload",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {

                    this.loadComposite(file);
                    this.updateUI();
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (e.getSource().equals(this.aboutManuItem)) {
            new AboutDialogBox(this).setVisible(true);
        }


    }

    private void setLookNFeel() {
        try {

            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
