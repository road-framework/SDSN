package au.edu.swin.ict.serendip.tool.designer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialogBox extends JDialog {

    //TODO Improve
    public AboutDialogBox(JFrame parent) {
        super(parent, "About Serendip", true);

        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(new JLabel("Serendip Designer View"));
        b.add(new JLabel("Version 0.90"));
        b.add(new JLabel("(c) Malinda Kapuruge"));
        b.add(Box.createGlue());
        getContentPane().add(b, "Center");

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        getContentPane().add(p2, "South");

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        this.setLocation(200, 200);
        setSize(250, 150);
    }


}
