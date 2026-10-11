package smarthome.ui;

import smarthome.bridge.Device;
import smarthome.facade.SmartRoomFacade;
import smarthome.model.Room;
import smarthome.singleton.SmartHomeHub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

// Әкімші беті: барлық бөлменің күйі (Singleton хаб арқылы)
public class AdminOverviewPanel extends JPanel {
    private final SmartHomeHub hub;
    private final StatCard occupied = new StatCard("ҚОНАҚ БАР", Theme.GREEN);
    private final StatCard power = new StatCard("ЖАЛПЫ ҚУАТ", Theme.AMBER);
    private final StatCard energy = new StatCard("ЖАЛПЫ ШЫҒЫН", Theme.TEAL);

    public AdminOverviewPanel(SmartHomeHub hub, Map<String, SmartRoomFacade> facades, Consumer<String> open) {
        this.hub = hub;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel title = Theme.label("Әкімші шолу", Font.BOLD, 28, Theme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = Theme.label("Барлық бөлменің күйі бір жерде. Карточканы басып бөлмені ашыңыз", Font.PLAIN, 13, Theme.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(2, 2, 14, 0));
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        stats.setPreferredSize(new Dimension(300, 92));
        stats.add(occupied);
        stats.add(power);
        stats.add(energy);
        top.add(title);
        top.add(sub);
        top.add(stats);
        add(top, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        for (Room r : hub.getRooms()) {
            grid.add(new RoomCard(r, open));
            r.addChangeListener(() -> SwingUtilities.invokeLater(this::refresh));
        }
        add(grid, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(new PillButton("Бос бөлмелерді өшіру", Theme.GREEN, true, () -> {
            for (Room r : hub.getRooms()) {
                if (r.getStatus() == Room.Status.FREE) facades.get(r.getNumber()).shutdownEmpty();
            }
            refresh();
        }));
        actions.add(Theme.label("Қонақ жоқ бөлмелердегі жарық, кондиционер, теледидар өшіріліп, есік құлыпталады", Font.PLAIN, 12, Theme.MUTED));
        add(actions, BorderLayout.SOUTH);

        new Timer(1000, e -> refresh()).start();
        refresh();
    }

    private void refresh() {
        int occ = 0, total = 0;
        double watts = 0, kwh = 0;
        for (Room r : hub.getRooms()) {
            total++;
            if (r.getStatus() == Room.Status.OCCUPIED) occ++;
            watts += r.totalWatts();
            kwh += r.getEnergy().getKwh();
        }
        occupied.set(occ + " / " + total, "бөлме толған");
        power.set(String.format(Locale.US, "%.0f Вт", watts), "қазір тұтынылуда");
        energy.set(String.format(Locale.US, "%.2f кВт·сағ", kwh), String.format(Locale.US, "≈ %.1f ₸", kwh * 25));
        repaint();
    }

    private static class StatCard extends RoundedPanel {
        private final String label;
        private final Color accent;
        private String value = "", sub = "";

        StatCard(String label, Color accent) {
            super(18, Theme.CARD, Theme.BORDER);
            this.label = label; this.accent = accent;
        }

        void set(String v, String s) { value = v; sub = s; repaint(); }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            Theme.quality(g);
            g.setColor(accent);
            g.fillRoundRect(0, 18, 4, getHeight() - 36, 4, 4);
            g.setFont(Theme.font(Font.BOLD, 11));
            g.setColor(Theme.MUTED);
            g.drawString(label, 20, 26);
            g.setFont(Theme.font(Font.BOLD, 24));
            g.setColor(Theme.TEXT);
            g.drawString(value, 20, 56);
            g.setFont(Theme.font(Font.PLAIN, 12));
            g.setColor(Theme.MUTED);
            g.drawString(sub, 20, 76);
            g.dispose();
        }
    }

    private static class RoomCard extends RoundedPanel {
        private final Room room;
        private boolean hover;

        RoomCard(Room room, Consumer<String> open) {
            super(20, Theme.CARD, Theme.BORDER);
            this.room = room;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; setBorderColor(Theme.GOLD); }
                @Override public void mouseExited(MouseEvent e) { hover = false; setBorderColor(Theme.BORDER); }
                @Override public void mouseClicked(MouseEvent e) { open.accept(room.getNumber()); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            Theme.quality(g);
            int w = getWidth(), h = getHeight();

            g.setFont(Theme.font(Font.BOLD, 26));
            g.setColor(Theme.TEXT);
            g.drawString(room.getNumber(), 20, 40);
            g.setFont(Theme.font(Font.PLAIN, 12));
            g.setColor(Theme.MUTED);
            g.drawString(room.getType(), 22, 60);

            boolean occ = room.getStatus() == Room.Status.OCCUPIED;
            Color sc = occ ? Theme.GREEN : Theme.MUTED;
            g.setFont(Theme.font(Font.BOLD, 11));
            FontMetrics fm = g.getFontMetrics();
            String st = room.getStatus().label;
            int cw = fm.stringWidth(st) + 30;
            g.setColor(Theme.alpha(sc, 36));
            g.fillRoundRect(w - cw - 16, 20, cw, 24, 24, 24);
            g.setColor(sc);
            g.fillOval(w - cw - 16 + 10, 28, 8, 8);
            g.drawString(st, w - cw - 16 + 24, 36);

            // ортасы: үлкен температура және қуат жолағы
            Device acd = room.getDevice("ac");
            g.setFont(Theme.font(Font.BOLD, 38));
            g.setColor(acd.isOn() ? Theme.TEXT : Theme.alpha(Theme.MUTED, 120));
            String big = acd.isOn() ? acd.getLevel() + "°C" : "— °C";
            int midY = 60 + (h - 100 - 60) / 2 + 14;
            g.drawString(big, 20, midY);
            g.setFont(Theme.font(Font.PLAIN, 11));
            g.setColor(Theme.MUTED);
            g.drawString("бөлме температурасы", 22, midY + 18);
            int bx = 20, bw = w - 40, by = midY + 34;
            g.setColor(new Color(0x2C3543));
            g.fillRoundRect(bx, by, bw, 6, 6, 6);
            double ratio = Theme.clamp(room.totalWatts() / 1400.0, 0, 1);
            g.setPaint(new GradientPaint(bx, 0, Theme.TEAL, bx + bw, 0, Theme.AMBER));
            g.fillRoundRect(bx, by, Math.max(6, (int) (bw * ratio)), 6, 6, 6);

            // құрылғы белгішелері
            String[] ids = {"light", "ac", "curtains", "tv", "door"};
            int x = 36;
            for (String id : ids) {
                Device d = room.getDevice(id);
                boolean active = d.hasPower() ? d.isOn() : d.getLevel() > 0;
                Color c = active ? colorFor(id) : Theme.alpha(Theme.MUTED, 150);
                if (id.equals("door")) c = d.isOn() ? Theme.GREEN : Theme.AMBER;
                String ic = id.equals("door") ? (d.isOn() ? "lock" : "unlock") : id;
                Icons.draw(g, ic, x, h - 62, 22, c);
                g.setFont(Theme.font(Font.PLAIN, 11));
                g.setColor(active || id.equals("door") ? Theme.TEXT : Theme.MUTED);
                String t = summary(d);
                g.drawString(t, x - g.getFontMetrics().stringWidth(t) / 2, h - 38);
                x += (w - 40) / 5;
            }
            g.setFont(Theme.font(Font.BOLD, 12));
            g.setColor(Theme.GOLD);
            String p = String.format(Locale.US, "%.0f Вт", room.totalWatts());
            g.drawString(p, w - 20 - g.getFontMetrics().stringWidth(p), h - 14);
            g.setFont(Theme.font(Font.PLAIN, 11));
            g.setColor(Theme.MUTED);
            g.drawString("қуат", 20, h - 14);
            g.dispose();
        }

        private static String summary(Device d) {
            switch (d.getId()) {
                case "light": return d.isOn() ? d.getLevel() + "%" : "өшік";
                case "ac": return d.isOn() ? d.getLevel() + "°C" : "өшік";
                case "curtains": return d.getLevel() + "%";
                case "tv": return d.isOn() ? "қосулы" : "өшік";
                default: return d.isOn() ? "құлып" : "ашық";
            }
        }

        private static Color colorFor(String id) {
            switch (id) {
                case "light": return Theme.AMBER;
                case "ac": return Theme.BLUE;
                case "curtains": return Theme.GOLD;
                default: return Theme.PURPLE;
            }
        }
    }
}
