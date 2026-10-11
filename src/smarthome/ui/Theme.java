package smarthome.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

// Бүкіл қолданбаның түстері, қаріптері және көмекші сызу әдістері
public final class Theme {
    private Theme() {}

    public static final Color BG = new Color(0x0D1015);
    public static final Color SIDEBAR = new Color(0x11151C);
    public static final Color CARD = new Color(0x171D26);
    public static final Color CARD_HI = new Color(0x212935);
    public static final Color BORDER = new Color(0x293240);
    public static final Color TEXT = new Color(0xEAEEF5);
    public static final Color MUTED = new Color(0x8A95A8);
    public static final Color GOLD = new Color(0xD9B26F);
    public static final Color AMBER = new Color(0xFBBF24);
    public static final Color BLUE = new Color(0x5EB0FF);
    public static final Color TEAL = new Color(0x3DD6B5);
    public static final Color GREEN = new Color(0x4ADE80);
    public static final Color RED = new Color(0xF87171);
    public static final Color PURPLE = new Color(0xA78BFA);

    private static final String FAMILY = pickFamily();

    private static String pickFamily() {
        List<String> have = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : new String[]{"Segoe UI", "SF Pro Text", "Helvetica Neue", "Noto Sans", "DejaVu Sans"}) {
            if (have.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    public static Font font(int style, float size) {
        return new Font(FAMILY, Font.PLAIN, 12).deriveFont(style, size);
    }

    public static void quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) clamp(a, 0, 255));
    }

    public static Color lerp(Color a, Color b, double t) {
        t = clamp(t, 0, 1);
        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t),
                (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    // ---- қараңғы скроллбар ----
    private static class DarkScrollBarUI extends BasicScrollBarUI {
        @Override protected JButton createDecreaseButton(int o) { return zero(); }
        @Override protected JButton createIncreaseButton(int o) { return zero(); }

        private JButton zero() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d); b.setMinimumSize(d); b.setMaximumSize(d);
            return b;
        }

        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            g2.setColor(alpha(Color.WHITE, isThumbRollover() ? 70 : 38));
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }
    }

    public static void styleScroll(JScrollPane sp) {
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
