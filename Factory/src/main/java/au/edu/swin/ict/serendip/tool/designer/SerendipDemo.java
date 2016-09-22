package au.edu.swin.ict.serendip.tool.designer;

import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.Document;
import com.jaxfront.core.dom.DocumentCreationException;
import com.jaxfront.core.schema.SchemaCreationException;
import com.jaxfront.core.util.URLHelper;
import com.jaxfront.swing.ui.editor.EditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO: File open dialog box
 *
 * @author Malinda
 * @deprecated (Only for testing)
 */


public class SerendipDemo extends JFrame {

    /**
     *
     */
    public SerendipDemo() {
        //File open dialog box
        this.startSerendipDemo("schemas/smc.xsd", "sample/Scenario/TestScenario2.xml", "xui/serendip.xui");
    }

    public void startSerendipDemo(String xsdFile, String xmlFile, String xuiFile) {

        this.setLookNFeel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width -= 300;
        screenSize.height -= 300;
        this.setSize(screenSize);
        this.setLocation(20, 20);

        JMenuBar mb = new JMenuBar();
        this.setJMenuBar(mb);
        JMenu fm = new JMenu("File");
        mb.add(fm);
        JMenuItem mi;
        fm.add(mi = new JMenuItem("Exit"));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        Document dom = null;
        try {
            dom = DOMBuilder.getInstance().build("default-context", URLHelper.getUserURL(xsdFile), URLHelper.getUserURL(xmlFile), URLHelper.getUserURL(xuiFile), null);
            EditorPanel editorPanel = new EditorPanel(dom.getRootType(), null);
            this.setContentPane(editorPanel);
            this.setSize(650, 400);
            this.setLocation(10, 10);
            this.setTitle("Serendip Descriptor Generator");

        } catch (SchemaCreationException e1) {
            e1.printStackTrace();
        } catch (DocumentCreationException e1) {
            e1.printStackTrace();
        }

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void setLookNFeel() {
        try {

            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /* Main View */
    public static void main(String[] a) {
        new SerendipDemo();
    }
} 