package au.edu.swin.ict.serendip.tool.gui;

import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.xml.bindings.CollaborationUnitType;
import au.edu.swin.ict.road.xml.bindings.ContractType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionsType;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork.Contracts;
import au.edu.swin.ict.serendip.composition.Composition;
import au.edu.swin.ict.serendip.composition.view.BehaviorView;
import au.edu.swin.ict.serendip.composition.view.ProcessView;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.util.CompositionUtil;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class CompositionView extends JPanel implements ActionListener {
    private SerendipEngine engine = null;
    private final static String PD_PREFIX = "P:";
    private final static String BT_PREFIX = "B:";
    private final static String T_PREFIX = "T:";
    private JMenuItem addBTToProcessMenu = null, addNewProcessMenu = null;
    Graph<VType, String> processGraph = new DirectedSparseMultigraph<VType, String>();
    HashMap<String, BTVType> btMap = new HashMap<String, BTVType>();
    HashMap<String, PDVType> pdMap = new HashMap<String, PDVType>();
    private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    public CompositionView(SerendipEngine engine) {
        this.engine = engine;
        this.createView();
    }

    private void createView() {

        // Now we add the vv
        this.split.add(this.createProcessPanel(this.engine.getComposition()));
        // this.split.add(this.createSRPanel(this.engine.getComposition()));
        this.split.setDividerLocation(300);
        this.setLayout(new BorderLayout());
        this.add(this.split, BorderLayout.CENTER);

        // We'll add a lable too
        JLabel label = new JLabel(this.engine.getCompositionName());// +
        // " Composition: "+
        // this.engine.getModelFactory().getFileLoadingDirectory()
        this.add(label, BorderLayout.NORTH);

    }


    private JScrollPane createProcessPanel(Composition comp) {

        // First we will add BTs
        if (null != comp.getComposite().getSmcBinding().getCollaborationUnits()) {
            final List<CollaborationUnitType> CollaborationUnitTypeList = comp.getComposite()
                                                                              .getSmcBinding().getCollaborationUnits().getCollaborationUnit();
            for (int i = 0; i < CollaborationUnitTypeList.size(); i++) {
                BTVType btt = new BTVType(CollaborationUnitTypeList.get(i));
                btMap.put(btt.getId(), btt);
                processGraph.addVertex(btt);
            }
        }

        if (null != comp.getComposite().getSmcBinding().getVirtualServiceNetwork()) {
            // Then we add PDs and the edges
            final List<ProcessDefinitionsType> processDefinitionsTypeList = comp
                    .getComposite().getSmcBinding().getVirtualServiceNetwork();
            for (ProcessDefinitionsType processDefinitionTypeList : processDefinitionsTypeList)
                for (int i = 0; i < processDefinitionTypeList.getProcess().size(); i++) {
                    ProcessDefinitionType pdType = processDefinitionTypeList.getProcess().get(i);
                    // Add pd vertex
                    PDVType pdt = new PDVType(pdType);
                    pdMap.put(pdt.getId(), pdt);
                    processGraph.addVertex(pdt);

                    List<String> btRefList = pdType.getCollaborationUnitRef();

                    for (int j = 0; j < btRefList.size(); j++) {
                        // Add edges
                        String btId = btRefList.get(j);
                        // add an edge btwn ith process and the bt if
                        BTVType bttTemp = btMap.get(btId);

                        processGraph.addEdge(pdt.getId() + "-" + btId, bttTemp, pdt,
                                             EdgeType.DIRECTED);
                    }
                }
        }

        Layout<VType, String> layout = new CircleLayout<VType, String>(
                processGraph);
        layout.setSize(new Dimension(500, 300)); // sets the initial size of the
        // space
        final VisualizationViewer<VType, String> vv = new VisualizationViewer<VType, String>(
                layout);

        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        vv.getRenderContext().setVertexLabelTransformer(
                new ToStringLabeller<VType>() {
                    public String transform(VType input) {
                        return input.getId();// changed
                    }
                });

        vv.getRenderContext().setVertexIconTransformer(
                new DefaultVertexIconTransformer<VType>() {
                    public Icon transform(VType input) {

                        if (input instanceof BTVType) {
                            return new ImageIcon("images/bt.gif");
                        } else if (input instanceof PDVType) {
                            return new ImageIcon("images/process.png");
                        } else {
                            return new ImageIcon("images/unknown.gif");
                        }
                    }
                });
        // Title
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {
            int x;
            int y;
            Font font;
            FontMetrics metrics;
            int swidth;
            int sheight;
            String str = "Process Definitions";

            public void paint(Graphics g) {
                Dimension d = new Dimension(200, 60);
                if (font == null) {
                    font = new Font(g.getFont().getName(), Font.BOLD, 20);
                    metrics = g.getFontMetrics(font);
                    swidth = metrics.stringWidth(str);
                    sheight = metrics.getMaxAscent() + metrics.getMaxDescent();
                    x = 0;
                    y = (int) (d.height - sheight * 1.5);
                }
                g.setFont(font);
                Color oldColor = g.getColor();
                g.setColor(Color.lightGray);
                g.drawString(str, x, y);
                g.setColor(oldColor);
            }

            public boolean useTransform() {
                return false;
            }
        });// Eof Title

        // Background etc.
        vv.setBackground(Color.white);
        vv.setToolTipText("Process definitions and related behavior terms");

        // Mouse mode
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(gm);

        // add action listener
        vv.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent arg0) {
                // TODO Auto-generated method stub
                PickedState<VType> st = vv.getPickedVertexState();
                Set<VType> pickedSet = st.getPicked();
                Iterator<VType> iter = pickedSet.iterator();
                SerendipEPCView epcView = null;
                String id = null;
                while (iter.hasNext()) {
                    VType s = iter.next();

                    if (null == s) {
                        return;
                    }
                    if (s instanceof PDVType) {// A process
                        id = s.getId();
                        ProcessView pv = null;
                        try {
                            pv = engine.getModelFactory().getProcessView(id,
                                                                         null);
                        } catch (SerendipException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        epcView = new SerendipEPCView(pv);

                    } else if (s instanceof BTVType) {
                        id = s.getId();
                        BehaviorView bv = null;
                        try {
                            bv = engine.getModelFactory().getBehaviorView(id);
                            if (null == bv) {
                                showError("Cannot get view for " + id);
                                return;
                            }
                        } catch (SerendipException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        epcView = new SerendipEPCView(bv);
                    } else {
                        // Do nothing;

                    }

                    // Show
                    SerendipJFrame frame = new SerendipJFrame(id);
                    if (null != epcView) {
                        frame.getContentPane().add(epcView);
                    } else {
                        frame
                                .setTitle("Cannot generate the EPC view for "
                                          + id);
                    }
                    frame.setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });

        // Menu. Currently hidden

        // this.createProcessViewPopUp(vv);
        // Scrol
        JScrollPane scrol = new JScrollPane();
        scrol.getViewport().add(vv);
        return scrol;
    }

    // /////////////////////////////////////OLD METHOD/////////////////////////
    // Process
    private JScrollPane createProcessPanelOld(Composition comp) {

        // First we will create the graph
        Forest<String, String> g = new DelegateForest<String, String>();

        final List<ProcessDefinitionType> processDefinitionTypeList = comp
                .getComposite().getSmcBinding().getVirtualServiceNetwork()
                .get(0).getProcess();
        for (int i = 0; i < processDefinitionTypeList.size(); i++) {
            String pDefId = PD_PREFIX
                            + processDefinitionTypeList.get(i).getId();
            g.addVertex(pDefId);
            List<String> btRefList = processDefinitionTypeList.get(i)
                                                              .getCollaborationUnitRef();
            for (int j = 0; j < btRefList.size(); j++) {

                String btId = btRefList.get(j);
                // Here we do a lil trick to avoid redundant vertices. We add an
                // integer (i) as a suffix. Make sure to remove it when u read
                // or display
                String btIdTemp = i + BT_PREFIX + btId;
                g.addEdge(pDefId + "-" + btId, pDefId, btIdTemp,
                          EdgeType.DIRECTED);

                // //testing
                // for(int k=0; k< 3; k++){
                // String taskIdTemp = T_PREFIX +j+"SomeTask"+k ;
                //
                // g.addEdge(btIdTemp + "-" + taskIdTemp, btIdTemp, taskIdTemp,
                // EdgeType.DIRECTED);
                // }
            }
        }

        // Now we have the graph
        // Layout layout = new TreeLayout<String,String>(g);
        Layout layout = new RadialTreeLayout<String, String>(g);
        layout.setSize(new Dimension(500, 300)); // sets the initial size of the
        // space
        final VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(
                layout);

        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        vv.getRenderContext().setVertexLabelTransformer(
                new ToStringLabeller<String>() {
                    public String transform(String input) {
                        return input.split(":")[1];
                    }
                });

        vv.getRenderContext().setVertexIconTransformer(
                new DefaultVertexIconTransformer<String>() {
                    public Icon transform(String input) {
                        if (input.startsWith(PD_PREFIX)) {
                            return new ImageIcon("images/process.gif");
                        } else if (input.startsWith(T_PREFIX)) {
                            return new ImageIcon("images/task.gif");
                        } else {
                            return new ImageIcon("images/bt.gif");
                        }
                    }
                });

        // Title
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {
            int x;
            int y;
            Font font;
            FontMetrics metrics;
            int swidth;
            int sheight;
            String str = "Process Definitions";

            public void paint(Graphics g) {
                Dimension d = new Dimension(200, 60);
                if (font == null) {
                    font = new Font(g.getFont().getName(), Font.BOLD, 20);
                    metrics = g.getFontMetrics(font);
                    swidth = metrics.stringWidth(str);
                    sheight = metrics.getMaxAscent() + metrics.getMaxDescent();
                    x = 0;
                    y = (int) (d.height - sheight * 1.5);
                }
                g.setFont(font);
                Color oldColor = g.getColor();
                g.setColor(Color.lightGray);
                g.drawString(str, x, y);
                g.setColor(oldColor);
            }

            public boolean useTransform() {
                return false;
            }
        });

        // Background etc.
        vv.setBackground(Color.white);
        vv.setToolTipText("Process definitions and related behavior terms");

        // Mouse mode
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(gm);

        // add action listener
        vv.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent arg0) {
                // TODO Auto-generated method stub
                PickedState<String> st = vv.getPickedVertexState();
                Set<String> pickedSet = st.getPicked();
                Iterator<String> iter = pickedSet.iterator();
                SerendipEPCView epcView = null;
                String id = null;
                while (iter.hasNext()) {
                    String s = iter.next();

                    if (null == s) {
                        return;
                    }
                    if (s.startsWith(PD_PREFIX)) {// A process
                        for (int i = 0; i < processDefinitionTypeList.size(); i++) {
                            id = processDefinitionTypeList.get(i).getId();
                            if (s.equals(PD_PREFIX + id)) {// match
                                // We found the process id
                                ProcessView pv = null;
                                try {
                                    pv = engine.getModelFactory()
                                               .getProcessView(id, null);
                                } catch (SerendipException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                epcView = new SerendipEPCView(pv);

                            }// end of match
                        }
                    } else {
                        String[] res = s.split(":");

                        if (res.length > 1) {
                            id = res[1];
                        } else {
                            id = s;// Hmm...
                        }
                        BehaviorView bv = null;
                        try {
                            bv = engine.getModelFactory().getBehaviorView(id);
                            if (null == bv) {
                                showError("Cannot get view for " + id);
                                return;
                            }
                        } catch (SerendipException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        epcView = new SerendipEPCView(bv);
                    }

                    // Show
                    SerendipJFrame frame = new SerendipJFrame(id);
                    if (null != epcView) {
                        frame.getContentPane().add(epcView);
                    } else {
                        frame
                                .setTitle("Cannot generate the EPC view for "
                                          + id);
                    }
                    frame.setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });

        JScrollPane scrol = new JScrollPane();
        scrol.getViewport().add(vv);
        // Menu
        this.createProcessViewPopUp(vv);

        // End menu

        return scrol;
    }

    // SR
    private JScrollPane createSRPanel(Composition comp) {
        JScrollPane scrol = new JScrollPane();
        // First we will create the graph

        Graph<String, String> graph = new SparseMultigraph<String, String>();
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<String, String> layout = new CircleLayout<String, String>(graph);
        layout.setSize(new Dimension(500, 500)); // sets the initial size of the
        // space

        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(
                layout);
        vv.setPreferredSize(new Dimension(500, 500)); // Sets the viewing area
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {
            int x;
            int y;
            Font font;
            FontMetrics metrics;
            int swidth;
            int sheight;
            String str = "Service relationships";

            public void paint(Graphics g) {
                Dimension d = new Dimension(200, 60);
                if (font == null) {
                    font = new Font(g.getFont().getName(), Font.BOLD, 20);
                    metrics = g.getFontMetrics(font);
                    swidth = metrics.stringWidth(str);
                    sheight = metrics.getMaxAscent() + metrics.getMaxDescent();
                    x = 0;
                    y = (int) (d.height - sheight * 1.5);
                }
                g.setFont(font);
                Color oldColor = g.getColor();
                g.setColor(Color.lightGray);
                g.drawString(str, x, y);
                g.setColor(oldColor);
            }

            public boolean useTransform() {
                return false;
            }
        });

        vv.setBackground(Color.white);
        vv.setToolTipText("Service relationships");

        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(gm);

        scrol.getViewport().add(vv); // size
        // Get all the ContractTypes

        Contracts contracts = comp.getComposite().getSmcBinding()
                                  .getContracts();

        if (null == contracts) {
            return scrol;
        }
        List<ContractType> contractTypeList = contracts.getContract();
        // List<ContractType> contractTypeList = comp.getComposite()
        // .getSmcBinding().getContracts().getContract();
        Map<String, Contract> contractMap = comp.getComposite()
                                                .getContractMap();
        for (ContractType ct : contractTypeList) {
            String roleA = ct.getRoleAID();
            String roleB = ct.getRoleBID();
            graph.addEdge(ct.getId(), roleA, roleB);

        }

        // vv.getRenderContext().setEdgeLabelTransformer(new
        // ToStringLabeller());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        vv.getRenderContext().setEdgeDrawPaintTransformer(
                new PickableEdgePaintTransformer<String>(vv
                                                                 .getPickedEdgeState(), Color.red, Color.cyan));

        vv.getRenderContext().setEdgeLabelTransformer(
                new Transformer<String, String>() {
                    String path = "images/contract.gif";
                    URL url = getClass().getResource(path);

                    public String transform(String input) {
                        if (null == url) {

                        }
                        return "<html>" + input + "<img src=\"" + url
                               + "\" ></html>";// height=10 width=21
                        // return
                        // "<html>"+input+"<img src=images\\bt.gif  height=10 width=21></html>";
                    }
                });

        vv.getRenderContext().setVertexIconTransformer(
                new DefaultVertexIconTransformer<String>() {
                    public Icon transform(String input) {
                        String fileName = "images/" + input + ".gif";
                        if (!(new File(fileName)).exists()) {
                            fileName = "images/defRole.gif";
                        }
                        return new ImageIcon(fileName);

                    }
                });

        return scrol;
    }

    private void showError(String errorMsg) {
        JOptionPane.showMessageDialog(this, errorMsg, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param component
     * @return
     */
    private JPopupMenu createProcessViewPopUp(JComponent component) {
        JPopupMenu popup = new JPopupMenu();
        this.addNewProcessMenu = new JMenuItem("Add new process ");
        this.addNewProcessMenu.addActionListener(this);
        popup.add(this.addNewProcessMenu);
        this.addBTToProcessMenu = new JMenuItem(
                "Integrate behavior term to the process");
        this.addBTToProcessMenu.addActionListener(this);
        popup.add(this.addBTToProcessMenu);
        MouseListener popupListener = new PopupListener(popup);
        component.addMouseListener(popupListener);
        return popup;
    }

    class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO Auto-generated method stub
        if (event.getSource().equals(this.addNewProcessMenu)) {
            String s = (String) JOptionPane.showInputDialog(this,
                                                            "Enter name of the process", "New Process",
                                                            JOptionPane.PLAIN_MESSAGE, null, null, "Untitled1");
            // TODO POPUP
            ProcessDefinitionType pdType = new ProcessDefinitionType();
            pdType.setId(s);
            //TODO
            boolean res = this.engine.getComposition().getComposite()
                                     .getSmcBinding().getVirtualServiceNetwork().get(0).getProcess().add(pdType);
            if (res) {
                // TODO:Check if this is all we have to do
                PDVType pdvt = new PDVType(pdType);
                this.pdMap.put(pdvt.getId(), pdvt);
                this.processGraph.addVertex(pdvt);
            } else {
                showError("Cannot add a new process definition");
            }

        } else if (event.getSource().equals(this.addBTToProcessMenu)) {
            String[] btArr = CompositionUtil
                    .getAllBTIDOfComposition(this.engine.getComposition());
            String s = (String) JOptionPane.showInputDialog(this,
                                                            "Select the behavior term to add", "Updating process",
                                                            JOptionPane.PLAIN_MESSAGE, null, btArr, " ");
        }
    }

    // Inner classes for the graphs

    /**
     * The parent class that represenets a vertex of the VV
     */
    public class VType {
        protected String id;

        public String getId() {
            return this.id;
        }

        public String toString() {
            return this.id;
        }

    }

    public class PDVType extends VType {
        private ProcessDefinitionType pdType = null;

        public PDVType(ProcessDefinitionType pdType) {
            this.id = pdType.getId();
            this.pdType = pdType;
        }

        public ProcessDefinitionType getPdType() {
            return pdType;
        }

        public void setPdType(ProcessDefinitionType pdType) {
            this.pdType = pdType;
        }

    }

    public class BTVType extends VType {
        private CollaborationUnitType btType = null;

        public BTVType(CollaborationUnitType btType) {
            this.id = btType.getId();
            this.btType = btType;
        }

        public CollaborationUnitType getBtType() {
            return btType;
        }

        public void setBtType(CollaborationUnitType btType) {
            this.btType = btType;
        }

    }
}
