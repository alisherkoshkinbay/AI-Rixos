package smarthome.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Дөңгелек батырма: primary (толтырылған) немесе outline
public class PillButton extends JComponent {
    private String text;
    private final Color accent;
    private final boolean primary;
    private final Runnable action;
    private boolean hover, pressed;

    public PillButton(String text, Color accent, boolean primary, Runnable action) {
        this.text = text; this.accent = accent; this.primary = primary; this.action = action;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.font(Font.BOLD, 13));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; pressed = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) {
                boolean click = pressed && hover && isEnabled();
                pressed = false; repaint();
                if (click && PillButton.this.action != null) PillButton.this.action.run();
            }
        });
    }

    public void setText(String t) { text = t; revalidate(); repaint(); }

    @Override public void setEnabled(boolean b) {
        super.setEnabled(b);
        setCursor(Cursor.getPredefinedCursor(b ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(text) + 32, 36);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int w = getWidth(), h = getHeight();
        boolean en = isEnabled();
        Color bg, fg, bd;
        if (primary) {
            bg = hover ? Theme.lerp(accent, Color.WHITE, 0.15) : accent;
            if (pressed) bg = Theme.lerp(accent, Color.BLACK, 0.15);
            fg = new Color(0x1A1406); bd = bg;
        } else {
            bg = hover ? Theme.alpha(accent, 40) : Theme.CARD_HI;
            if (pressed) bg = Theme.alpha(accent, 70);
            fg = Theme.TEXT; bd = hover ? accent : Theme.BORDER;
        }
        if (!en) { bg = Theme.alpha(bg, 90); fg = Theme.alpha(fg, 90); bd = Theme.alpha(bd, 60); }
        g.setColor(bg); g.fillRoundRect(0, 0, w - 1, h - 1, h, h);
        g.setColor(bd); g.drawRoundRect(0, 0, w - 1, h - 1, h, h);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.setColor(fg);
        g.drawString(text, (w - fm.stringWidth(text)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
