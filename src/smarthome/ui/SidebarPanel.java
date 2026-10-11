package smarthome.ui;

import smarthome.model.Room;
import smarthome.singleton.SmartHomeHub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Сол жақ мәзір: логотип, беттер, бөлмелер тізімі
public class SidebarPanel extends JPanel {
    private static final String[][] NAV = {{"room", "Бөлме"}, {"admin", "Әкімші шолу"}, {"log", "Оқиғалар"}};
    private final SmartHomeHub hub;
    private final Consumer<String> onNav;
    private final Consumer<String> onRoom;
    private String view = "room", selectedRoom = "305";
    private int hover = -1;
    private final List<Rectangle> hits = new ArrayList<>();
    private final List<String> hitKeys = new ArrayList<>();

    public SidebarPanel(SmartHomeHub hub, Consumer<String> onNav, Consumer<String> onRoom) {
        this.hub = hub; this.onNav = onNav; this.onRoom = onRoom;
        setOpaque(false);
        setPreferredSize(new Dimension(230, 100));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        for (Room r : hub.getRooms()) r.addChangeListener(() -> SwingUtilities.invokeLater(this::repaint));
        MouseAdapter m = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int h = hitAt(e.getPoint());
                if (h != hover) { hover = h; repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                int h = hitAt(e.getPoint());
                if (h < 0) return;
                String key = hitKeys.get(h);
                if (key.startsWith("nav:")) onNav.accept(key.substring(4));
                else onRoom.accept(key.substring(5));
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    public void setSelection(String view, String room) {
        this.view = view;
        if (room != null) this.selectedRoom = room;
        repaint();
    }

    private int hitAt(Point p) {
        for (int i = 0; i < hits.size(); i++) if (hits.get(i).contains(p)) return i;
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int w = getWidth(), h = getHeight();
        g.setColor(Theme.SIDEBAR);
        g.fillRect(0, 0, w, h);
        g.setColor(Theme.BORDER);
        g.drawLine(w - 1, 0, w - 1, h);
        hits.clear();
        hitKeys.clear();

        // логотип
        g.setFont(Theme.font(Font.BOLD, 24));
        g.setColor(Theme.GOLD);
        String logo = "R I X O S";
        g.drawString(logo, 26, 50);
        g.setFont(Theme.font(Font.PLAIN, 11));
        g.setColor(Theme.MUTED);
        g.drawString("S M A R T   H O T E L", 27, 70);
        g.setColor(Theme.BORDER);
        g.drawLine(24, 90, w - 24, 90);

        int y = 108;
        for (String[] n : NAV) {
            boolean sel = view.equals(n[0]);
            Rectangle r = new Rectangle(14, y, w - 28, 40);
            boolean hov = hits.size() == hover;
            item(g, r, n[1], sel, hov, null);
            hits.add(r); hitKeys.add("nav:" + n[0]);
            y += 46;
        }

        y += 14;
        g.setFont(Theme.font(Font.BOLD, 11));
        g.setColor(Theme.MUTED);
        g.drawString("Б Ө Л М Е Л Е Р", 26, y);
        y += 12;
        for (Room room : hub.getRooms()) {
            boolean sel = view.equals("room") && selectedRoom.equals(room.getNumber());
            Rectangle r = new Rectangle(14, y, w - 28, 42);
            boolean hov = hits.size() == hover;
            item(g, r, room.getNumber() + "  ·  " + room.getType(), sel, hov,
                    room.getStatus() == Room.Status.OCCUPIED ? Theme.GREEN : Theme.alpha(Theme.MUTED, 140));
            hits.add(r); hitKeys.add("room:" + room.getNumber());
            y += 46;
        }

        g.setFont(Theme.font(Font.PLAIN, 11));
        g.setColor(Theme.alpha(Theme.MUTED, 200));
        g.drawString("Bridge · Command · Observer", 26, h - 38);
        g.drawString("Facade · Singleton", 26, h - 22);
        g.dispose();
    }

    private void item(Graphics2D g, Rectangle r, String text, boolean sel, boolean hov, Color dot) {
        if (sel) {
            g.setColor(Theme.alpha(Theme.GOLD, 34));
            g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
            g.setColor(Theme.GOLD);
            g.fillRoundRect(r.x, r.y + 9, 3, r.height - 18, 3, 3);
        } else if (hov) {
            g.setColor(new Color(255, 255, 255, 12));
            g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        }
        int tx = r.x + 16;
        if (dot != null) {
            g.setColor(dot);
            g.fillOval(r.x + 14, r.y + r.height / 2 - 4, 9, 9);
            tx = r.x + 34;
        }
        g.setFont(Theme.font(sel ? Font.BOLD : Font.PLAIN, 14));
        g.setColor(sel ? Theme.TEXT : (hov ? Theme.TEXT : Theme.MUTED));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, tx, r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
    }
}
