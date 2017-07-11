package au.edu.swin.ict.serendip.tool.gui;

import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.serendip.composition.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Malinda
 * @depreciated
 */
public class RoleView extends JPanel implements ActionListener {
    static int index = 0;
    JPanel northPanel, chkBoxPanel = null;
    private Role role = null;
    private JTable taskTable = null;
    private DefaultTableModel roleDtModel = null;
    private JButton doButton = new JButton("Complete Task");
    private List<Task> curActiveTasks = new ArrayList<Task>();

    // private List<JCheckBox> messagesofCurTask = new ArrayList<JCheckBox>();

    public RoleView(Role role) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        this.role = role;
        this.createGUI();
    }

    private void createGUI() {
        JLabel roleLabel = new JLabel("Possible Messages ");
        JFrame roleFrame = new JFrame();
        this.northPanel = new JPanel(new FlowLayout());
        this.chkBoxPanel = new JPanel(new FlowLayout());
        this.chkBoxPanel.setSize(this.getWidth(), 100);
        this.setLayout(new BorderLayout());

        this.taskTable = new JTable(this.createDefaultTableModel());
        this.taskTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                Point p = e.getPoint();
                int row = taskTable.rowAtPoint(p);
                Task task = curActiveTasks.get(row);
                if (null == task) {
                    doButton.setEnabled(false);
                } else {
                    doButton.setEnabled(true);
                }

            }
        });

        // northPanel.add(roleLabel);
        doButton.setEnabled(false);
        northPanel.add(doButton);

        JScrollPane scrol = new JScrollPane();
        scrol.setViewportView(this.taskTable);

        this.add(northPanel, BorderLayout.NORTH);
        this.add(scrol, BorderLayout.CENTER);

        roleFrame.getContentPane().add(this);
        roleFrame.setVisible(true);
        roleFrame.setLocation(800, index * 150);
        roleFrame.setSize(400, 150);
        roleFrame.setTitle(this.role.getId() + " View");
        roleFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                "images/" + this.role.getId() + ".gif"));
        doButton.addActionListener(this);
        index++;// to position the GUI

    }

    private DefaultTableModel createDefaultTableModel() {
        String[] columnNames = {"TaskId", "Task Descr", "PID"};
        Object[][] data = {};
        roleDtModel = new DefaultTableModel();
        roleDtModel.setDataVector(data, columnNames);

        return roleDtModel;
    }

    public void addTaskToTable(Task task) {
        this.curActiveTasks.add(task);
        this.roleDtModel.addRow(new Object[]{task.getId(),
                task.getTaskDescr(), task.getId()});

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO Auto-generated method stub
        if (event.getSource().equals(this.doButton)) {
            int rowIndex = this.taskTable.getSelectedRow(); // Get currently
            // selected row

            Task task = this.curActiveTasks.get(rowIndex);
//			try {
//				this.role.emitMessage(task);
//			} catch (SerendipException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

            // Both needs to be done at the same time
            this.roleDtModel.removeRow(rowIndex);
            this.curActiveTasks.remove(rowIndex);

            this.doButton.setEnabled(false);

        }
    }

}
