package smarthome.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// iOS стиліндегі ауыстырғыш (анимациямен). Күйді құрылғы анықтайды, ауыстырғыш тек көрсетеді
public class ToggleSwitch extends JComponent {
    private boolean selected;
    private double pos;
    private final Color accent;
    private final Timer anim;

    public ToggleSwitch(Color accent, Runnable onClick) {
        this.accent = accent;
        setPreferredSize(new Dimension(46, 26));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        anim = new Timer(16, e -> {
            double target = selected ? 1 : 0;
            pos += (target - pos) * 0.35;
            if (Math.abs(target - pos) < 0.02) { pos = target; ((Timer) e.getSource()).stop(); }
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
        });
    }

    public void setSelected(boolean s) {
        if (s == selected) return;
        selected = s;
        if (isShowing()) anim.start(); else { pos = s ? 1 : 0; repaint(); }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int w = getWidth(), h = getHeight();
        g.setColor(Theme.lerp(new Color(0x394353), accent, pos));
        g.fillRoundRect(0, 0, w - 1, h - 1, h, h);
        int d = h - 8;
        int x = (int) (4 + pos * (w - d - 8));
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x, 5, d, d);
        g.setColor(Color.WHITE);
        g.fillOval(x, 4, d, d);
        g.dispose();
    }
}
