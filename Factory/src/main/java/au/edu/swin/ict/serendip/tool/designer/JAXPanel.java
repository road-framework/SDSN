package au.edu.swin.ict.serendip.tool.designer;

import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.Document;
import com.jaxfront.core.dom.DocumentCreationException;
import com.jaxfront.core.schema.SchemaCreationException;
import com.jaxfront.core.util.URLHelper;
import com.jaxfront.swing.ui.editor.EditorPanel;

import javax.swing.*;

public class JAXPanel extends JPanel {
    public JAXPanel(String xsdFile, String xmlFile, String xuiFile) {

        Document dom = null;
        try {
            dom = DOMBuilder.getInstance().build("default-context", URLHelper.getUserURL(xsdFile), URLHelper.getUserURL(xmlFile), URLHelper.getUserURL(xuiFile), null);
            EditorPanel editorPanel = new EditorPanel(dom.getRootType(), null);
            this.add(editorPanel);
            //this.setSize(650, 400);
            this.setLocation(10, 10);

        } catch (SchemaCreationException e1) {
            e1.printStackTrace();
        } catch (DocumentCreationException e1) {
            e1.printStackTrace();
        }


    }


}
