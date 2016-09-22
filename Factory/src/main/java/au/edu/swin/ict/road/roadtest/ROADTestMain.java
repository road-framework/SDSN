package au.edu.swin.ict.road.roadtest;

import au.edu.swin.ict.road.roadtest.UI.CompositionUI;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ROADTestMain {
    private static Logger log = Logger.getLogger(ROADTestMain.class.getName());

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            log.debug("Failed loading L&F: ");
            log.debug(ex);
        }
        new CompositionUI();
    }

}
