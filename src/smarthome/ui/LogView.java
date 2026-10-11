package smarthome.ui;

import smarthome.observer.ActivityLog;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Оқиғалар журналы: Observer жазған құрылғы оқиғалары және Command орындаған әрекеттер
public class LogView extends JPanel {
    private final ActivityLog log;
    private final DefaultListModel<ActivityLog.Entry> model = new DefaultListModel<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogView(ActivityLog log) {
        this.log = log;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel t = Theme.label("Оқиғалар журналы", Font.BOLD, 28, Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = Theme.label("Бірінші түс: құрылғы өзгерді (Observer) · Екінші түс: әрекет орындалды (Command)", Font.PLAIN, 13, Theme.MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);
        s.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));
        titles.add(t);
        titles.add(s);
        head.add(titles, BorderLayout.WEST);
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        east.setOpaque(false);
        east.add(new PillButton("Тазалау", Theme.RED, false, log::clear));
        head.add(east, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        JList<ActivityLog.Entry> list = new JList<>(model);
        list.setOpaque(false);
        list.setFixedCellHeight(40);
        list.setCellRenderer(new Row());
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override public void setSelectionInterval(int a, int b) {}
        });
        JScrollPane sp = new JScrollPane(list);
        Theme.styleScroll(sp);

        RoundedPanel card = new RoundedPanel(20, Theme.CARD, Theme.BORDER);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 4));
        card.add(sp, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        log.addListener(() -> SwingUtilities.invokeLater(this::reload));
        reload();
    }

    private void reload() {
        List<ActivityLog.Entry> all = log.snapshot();
        model.clear();
        for (int i = all.size() - 1; i >= 0; i--) model.addElement(all.get(i));
    }

    private static class Row extends JComponent implements ListCellRenderer<ActivityLog.Entry> {
        private ActivityLog.Entry e;

        @Override
        public Component getListCellRendererComponent(JList<? extends ActivityLog.Entry> l, ActivityLog.Entry v,
                                                      int i, boolean sel, boolean focus) {
            e = v;
            return this;
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 40); }

        @Override
        protected void paintComponent(Graphics g0) {
            if (e == null) return;
            Graphics2D g = (Graphics2D) g0.create();
            Theme.quality(g);
            int h = getHeight(), w = getWidth();
            boolean cmd = e.kind == ActivityLog.Kind.COMMAND;
            Color k = cmd ? Theme.PURPLE : Theme.TEAL;
            g.setColor(Theme.alpha(k, 28));
            g.fillRoundRect(4, 3, w - 8, h - 6, 12, 12);
            g.setColor(k);
            g.fillRoundRect(4, 8, 3, h - 16, 3, 3);
            g.setFont(Theme.font(Font.PLAIN, 12));
            g.setColor(Theme.MUTED);
            g.drawString(e.time.format(FMT), 18, h / 2 + 4);
            g.setFont(Theme.font(Font.BOLD, 12));
            g.setColor(Theme.GOLD);
            g.drawString("№" + e.room, 90, h / 2 + 4);
            g.setFont(Theme.font(Font.BOLD, 13));
            g.setColor(Theme.TEXT);
            g.drawString(e.source, 150, h / 2 + 4);
            g.setFont(Theme.font(Font.PLAIN, 13));
            g.setColor(cmd ? Theme.PURPLE : Theme.TEXT);
            g.drawString(e.text, 290, h / 2 + 4);
            g.dispose();
        }
    }
}
