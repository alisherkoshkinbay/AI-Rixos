package smarthome.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

// Заманауи слайдер
public class FlatSlider extends JComponent {
    private int min, max, value;
    private final Color accent;
    private final IntConsumer onChange;
    private boolean hover;
    private static final int PAD = 10;

    public FlatSlider(int min, int max, int value, Color accent, IntConsumer onChange) {
        this.min = min; this.max = max; this.value = value; this.accent = accent; this.onChange = onChange;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter m = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { update(e.getX()); }
            @Override public void mouseDragged(MouseEvent e) { update(e.getX()); }
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    public void setValue(int v) { if (v != value) { value = v; repaint(); } }
    public int getValue() { return value; }

    private void update(int x) {
        if (max == min) return;
        double t = Theme.clamp((x - PAD) / (double) (getWidth() - 2 * PAD), 0, 1);
        int v = min + (int) Math.round(t * (max - min));
        if (v != value) {
            value = v;
            repaint();
            if (onChange != null) onChange.accept(v);
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int w = getWidth(), h = getHeight();
        int cy = h / 2;
        double t = max == min ? 0 : (value - min) / (double) (max - min);
        int x = PAD + (int) (t * (w - 2 * PAD));
        g.setColor(new Color(0x2C3543));
        g.fillRoundRect(PAD, cy - 3, w - 2 * PAD, 6, 6, 6);
        g.setPaint(new GradientPaint(PAD, 0, Theme.lerp(accent, Color.BLACK, 0.35), x + 1, 0, accent));
        g.fillRoundRect(PAD, cy - 3, Math.max(6, x - PAD), 6, 6, 6);
        int r = hover ? 9 : 8;
        g.setColor(Theme.alpha(accent, 60));
        g.fillOval(x - r - 3, cy - r - 3, 2 * r + 6, 2 * r + 6);
        g.setColor(Color.WHITE);
        g.fillOval(x - r, cy - r, 2 * r, 2 * r);
        g.setColor(accent);
        g.fillOval(x - 3, cy - 3, 6, 6);
        g.dispose();
    }
}
