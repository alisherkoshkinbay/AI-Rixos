package smarthome.ui;

import smarthome.model.Room;
import smarthome.observer.EnergyMonitor;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

// Энергия картасы: қазіргі қуат, кВт·сағ, құны және график. Мәліметті EnergyMonitor (Observer) береді
public class EnergyPanel extends RoundedPanel {
    private final EnergyMonitor mon;

    public EnergyPanel(Room room) {
        super(18, Theme.CARD, Theme.BORDER);
        this.mon = room.getEnergy();
        setPreferredSize(new Dimension(300, 150));
        new Timer(1000, e -> { mon.tick(); repaint(); }).start();
        room.addChangeListener(() -> SwingUtilities.invokeLater(this::repaint));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int w = getWidth(), h = getHeight();

        g.setFont(Theme.font(Font.BOLD, 11));
        g.setColor(Theme.MUTED);
        g.drawString("ЭНЕРГИЯ", 16, 24);

        g.setFont(Theme.font(Font.BOLD, 26));
        g.setColor(Theme.TEXT);
        g.drawString(String.format(Locale.US, "%.0f", mon.getWatts()), 16, 56);
        int nw = g.getFontMetrics().stringWidth(String.format(Locale.US, "%.0f", mon.getWatts()));
        g.setFont(Theme.font(Font.PLAIN, 13));
        g.setColor(Theme.MUTED);
        g.drawString("Вт", 16 + nw + 5, 56);

        g.setFont(Theme.font(Font.BOLD, 13));
        g.setColor(Theme.GOLD);
        String r1 = String.format(Locale.US, "%.2f кВт·сағ", mon.getKwh());
        g.drawString(r1, w - 16 - g.getFontMetrics().stringWidth(r1), 38);
        g.setFont(Theme.font(Font.PLAIN, 12));
        g.setColor(Theme.MUTED);
        String r2 = String.format(Locale.US, "≈ %.1f ₸", mon.getCost());
        g.drawString(r2, w - 16 - g.getFontMetrics().stringWidth(r2), 56);

        // график
        List<Double> s = mon.getSamples();
        int gx = 16, gy = 70, gw = w - 32, gh = h - 70 - 26;
        g.setColor(new Color(255, 255, 255, 10));
        for (int i = 0; i <= 3; i++) g.drawLine(gx, gy + gh * i / 3, gx + gw, gy + gh * i / 3);
        if (s.size() > 1) {
            double max = 1500;
            Path2D line = new Path2D.Double();
            Path2D area = new Path2D.Double();
            for (int i = 0; i < s.size(); i++) {
                double x = gx + gw * i / (double) (s.size() - 1);
                double y = gy + gh - Math.min(1, s.get(i) / max) * gh;
                if (i == 0) { line.moveTo(x, y); area.moveTo(x, gy + gh); area.lineTo(x, y); }
                else { line.lineTo(x, y); area.lineTo(x, y); }
            }
            area.lineTo(gx + gw, gy + gh);
            area.closePath();
            g.setPaint(new GradientPaint(0, gy, Theme.alpha(Theme.TEAL, 90), 0, gy + gh, Theme.alpha(Theme.TEAL, 0)));
            g.fill(area);
            g.setColor(Theme.TEAL);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(line);
        }
        g.setFont(Theme.font(Font.PLAIN, 10));
        g.setColor(Theme.alpha(Theme.MUTED, 200));
        g.drawString("Демо: 1 секунд = 1 минут · тариф 25 ₸/кВт·сағ", 16, h - 9);
        g.dispose();
    }
}
