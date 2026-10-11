package smarthome.ui;

import smarthome.facade.SmartRoomFacade;
import smarthome.facade.VoiceAssistant;
import smarthome.model.Room;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Бір бөлменің толық басқару беті
public class RoomView extends JPanel {
    private final Room room;
    private final SmartRoomFacade facade;
    private final RoomScenePanel scene;
    private final List<DeviceCard> cards = new ArrayList<>();
    private final StatusChip chip = new StatusChip();
    private final PillButton undoBtn, redoBtn;
    private final JLabel timeLabel = Theme.label("", Font.BOLD, 13, Theme.TEXT);
    private final FlatSlider timeSlider;

    public RoomView(Room room, SmartRoomFacade facade) {
        this.room = room;
        this.facade = facade;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));

        scene = new RoomScenePanel(room);

        undoBtn = new PillButton("↶  Undo", Theme.BLUE, false, () -> act(facade::undo));
        redoBtn = new PillButton("Redo  ↷", Theme.BLUE, false, () -> act(facade::redo));

        LocalTime now = LocalTime.now();
        timeSlider = new FlatSlider(0, 1439, now.getHour() * 60 + now.getMinute(), Theme.GOLD, m -> {
            scene.setHour(m / 60.0);
            updateTimeLabel(m);
        });
        updateTimeLabel(timeSlider.getValue());

        JPanel left = new JPanel(new BorderLayout(0, 14));
        left.setOpaque(false);
        left.add(buildHeader(), BorderLayout.NORTH);
        left.add(scene, BorderLayout.CENTER);
        left.add(buildBottom(), BorderLayout.SOUTH);
        add(left, BorderLayout.CENTER);
        add(buildRight(), BorderLayout.EAST);

        room.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshAll));
        refreshAll();
    }

    private void act(Runnable r) { r.run(); refreshAll(); }

    private void updateTimeLabel(int minutes) {
        timeLabel.setText(String.format("%02d:%02d", minutes / 60, minutes % 60));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel west = new JPanel();
        west.setOpaque(false);
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(Theme.label("Бөлме " + room.getNumber(), Font.BOLD, 28, Theme.TEXT));
        row.add(chip);
        JLabel sub = Theme.label(room.getType() + " · Rixos Smart Room", Font.PLAIN, 13, Theme.MUTED);
        sub.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 0));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        west.add(row);
        west.add(sub);
        header.add(west, BorderLayout.WEST);

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        east.setOpaque(false);
        east.add(undoBtn);
        east.add(redoBtn);
        header.add(east, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JPanel scen = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        scen.setOpaque(false);
        scen.setAlignmentX(LEFT_ALIGNMENT);
        scen.add(new PillButton("Қош келдіңіз", Theme.GOLD, true, () -> act(facade::welcome)));
        scen.add(new PillButton("Түнгі режим", Theme.PURPLE, false, () -> act(facade::nightMode)));
        scen.add(new PillButton("Кино", Theme.PURPLE, false, () -> act(facade::movieMode)));
        scen.add(new PillButton("Таң", Theme.AMBER, false, () -> act(facade::morningMode)));
        scen.add(new PillButton("Үнемдеу", Theme.GREEN, false, () -> act(facade::ecoMode)));
        scen.add(new PillButton("Check-out", Theme.RED, false, () -> act(facade::checkOut)));

        RoundedPanel timeBar = new RoundedPanel(18, Theme.CARD, Theme.BORDER);
        timeBar.setLayout(new BorderLayout(12, 0));
        timeBar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 14));
        timeBar.setAlignmentX(LEFT_ALIGNMENT);
        timeBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        timeBar.setPreferredSize(new Dimension(300, 50));
        timeBar.add(Theme.label("Сыртқы уақыт", Font.PLAIN, 12, Theme.MUTED), BorderLayout.WEST);
        timeBar.add(timeSlider, BorderLayout.CENTER);
        JPanel te = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        te.setOpaque(false);
        te.add(timeLabel);
        te.add(new PillButton("Қазір", Theme.GOLD, false, () -> {
            LocalTime n = LocalTime.now();
            int m = n.getHour() * 60 + n.getMinute();
            timeSlider.setValue(m);
            scene.setHour(m / 60.0);
            updateTimeLabel(m);
        }));
        timeBar.add(te, BorderLayout.EAST);

        bottom.add(scen);
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(timeBar);
        return bottom;
    }

    private static class ScrollPanel extends JPanel implements Scrollable {
        ScrollPanel() { setOpaque(false); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private JComponent buildRight() {
        ScrollPanel col = new ScrollPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));

        for (smarthome.bridge.Device d : room.getDevices()) {
            DeviceCard c = new DeviceCard(d, facade);
            c.setAlignmentX(LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.cardHeight()));
            cards.add(c);
            col.add(c);
            col.add(Box.createVerticalStrut(10));
        }
        EnergyPanel energy = new EnergyPanel(room);
        energy.setAlignmentX(LEFT_ALIGNMENT);
        energy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        col.add(energy);
        col.add(Box.createVerticalStrut(10));

        AssistantPanel assistant = new AssistantPanel(new VoiceAssistant(facade), this::refreshAll);
        assistant.setAlignmentX(LEFT_ALIGNMENT);
        assistant.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        col.add(assistant);

        JScrollPane sp = new JScrollPane(col);
        Theme.styleScroll(sp);
        sp.setPreferredSize(new Dimension(340, 100));
        return sp;
    }

    private void refreshAll() {
        for (DeviceCard c : cards) c.refresh();
        chip.set(room.getStatus());
        undoBtn.setEnabled(facade.canUndo());
        redoBtn.setEnabled(facade.canRedo());
        undoBtn.setToolTipText(facade.canUndo() ? "Болдырмау: " + facade.undoName() : null);
        redoBtn.setToolTipText(facade.canRedo() ? "Қайталау: " + facade.redoName() : null);
    }

    // Мәртебе белгісі
    private static class StatusChip extends JComponent {
        private Room.Status status = Room.Status.FREE;

        void set(Room.Status s) { status = s; repaint(); }

        @Override public Dimension getPreferredSize() { return new Dimension(112, 28); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.quality(g);
            Color c = status == Room.Status.OCCUPIED ? Theme.GREEN : Theme.MUTED;
            int w = getWidth(), h = getHeight();
            g.setColor(Theme.alpha(c, 36));
            g.fillRoundRect(0, 0, w - 1, h - 1, h, h);
            g.setColor(Theme.alpha(c, 110));
            g.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            g.setColor(c);
            g.fillOval(12, h / 2 - 4, 8, 8);
            g.setFont(Theme.font(Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(status.label, 27, (h + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }
}
