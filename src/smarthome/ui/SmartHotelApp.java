package smarthome.ui;

import javax.swing.*;
import java.awt.*;

public class SmartHotelApp {
    public static void launch() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Rixos Smart Hotel");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new SmartHotelPanel());
            f.setSize(1280, 800);
            f.setMinimumSize(new Dimension(1120, 720));
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
