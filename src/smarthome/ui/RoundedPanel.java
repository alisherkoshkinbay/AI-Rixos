package smarthome.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int arc;
    private Color fill, borderColor;

    public RoundedPanel(int arc, Color fill, Color borderColor) {
        this.arc = arc; this.fill = fill; this.borderColor = borderColor;
        setOpaque(false);
    }

    public void setFill(Color c) { fill = c; repaint(); }
    public void setBorderColor(Color c) { borderColor = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.setColor(fill);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        if (borderColor != null) {
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
