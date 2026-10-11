package smarthome.ui;

import smarthome.bridge.Device;
import smarthome.model.Room;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalTime;
import java.util.Random;

// Анимациялы бөлме суреті. OBSERVER: бөлме өзгергенде мақсатты мәндерді жаңартады, анимация жұмсақ өтеді
public class RoomScenePanel extends JPanel {
    private final Room room;
    private double lamp, lampT, curtain, curtainT, ac, acT, tv, tvT;
    private int acTemp, tvVol;
    private boolean locked = true;
    private double hour;
    private double phase;
    private final double[][] stars = new double[44][4];
    private final double[][] flow = new double[18][3];

    private static final double[] KH = {0, 5, 6.5, 8, 12, 17, 18.5, 20, 24};
    private static final int[] KT = {0x070B1E, 0x141B42, 0x5B7FC0, 0x4C9BE8, 0x2F86E6, 0x4C93E0, 0x3B4F9A, 0x121A45, 0x070B1E};
    private static final int[] KB = {0x1A2347, 0x3B3A6B, 0xF4A66A, 0xA9D6F5, 0x8FCBF7, 0xBFD9F2, 0xF18F5E, 0x3A3566, 0x1A2347};

    public RoomScenePanel(Room room) {
        this.room = room;
        setOpaque(false);
        setPreferredSize(new Dimension(640, 420));
        Random rnd = new Random(7);
        for (double[] s : stars) { s[0] = rnd.nextDouble(); s[1] = rnd.nextDouble() * 0.75; s[2] = 1 + rnd.nextDouble() * 1.6; s[3] = rnd.nextDouble() * 6.28; }
        for (double[] f : flow) { f[0] = rnd.nextDouble(); f[1] = rnd.nextDouble(); f[2] = 0.25 + rnd.nextDouble() * 0.3; }
        LocalTime now = LocalTime.now();
        hour = now.getHour() + now.getMinute() / 60.0;
        syncTargets();
        snap();
        room.addChangeListener(() -> SwingUtilities.invokeLater(this::syncTargets));
        new Timer(33, e -> tick()).start();
    }

    private void syncTargets() {
        Device l = room.getDevice("light");
        lampT = l.isOn() ? l.getLevel() / 100.0 : 0;
        curtainT = room.getDevice("curtains").getLevel() / 100.0;
        Device a = room.getDevice("ac");
        acT = a.isOn() ? 1 : 0;
        acTemp = a.getLevel();
        Device t = room.getDevice("tv");
        tvT = t.isOn() ? 1 : 0;
        tvVol = t.getLevel();
        locked = room.getDevice("door").isOn();
    }

    public void snap() { lamp = lampT; curtain = curtainT; ac = acT; tv = tvT; }

    public void setHour(double h) { hour = h; repaint(); }

    private void tick() {
        phase += 0.033;
        lamp += (lampT - lamp) * 0.14;
        curtain += (curtainT - curtain) * 0.10;
        ac += (acT - ac) * 0.12;
        tv += (tvT - tv) * 0.16;
        if (isShowing()) repaint();
    }

    // ---------- көмекші ----------
    private static double daylight(double h) {
        if (h < 5.5 || h > 18.5) return 0;
        return Math.pow(Math.max(0, Math.sin(Math.PI * (h - 5.5) / 13.0)), 0.7);
    }

    private Color sky(int[] k) {
        for (int i = 0; i < KH.length - 1; i++) {
            if (hour >= KH[i] && hour <= KH[i + 1]) {
                return Theme.lerp(new Color(k[i]), new Color(k[i + 1]), (hour - KH[i]) / (KH[i + 1] - KH[i]));
            }
        }
        return new Color(k[0]);
    }

    private static Color shade(Color c, double f) {
        Color dark = new Color((int) (c.getRed() * .20), (int) (c.getGreen() * .23), (int) (c.getBlue() * .36));
        return Theme.lerp(dark, c, f);
    }

    private void chip(Graphics2D g, String text, double cx, double cy, Color dot) {
        g.setFont(Theme.font(Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text) + 28, h = 22;
        int x = (int) Theme.clamp(cx - w / 2.0, 6, getWidth() - w - 6);
        int y = (int) (cy - h / 2.0);
        g.setColor(new Color(10, 14, 22, 200));
        g.fillRoundRect(x, y, w, h, h, h);
        g.setColor(dot);
        g.fillOval(x + 9, y + h / 2 - 3, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(text, x + 20, y + (h + fm.getAscent() - fm.getDescent()) / 2);
    }

    // ---------- сызу ----------
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        int W = getWidth(), H = getHeight();
        if (W < 60 || H < 60) { g.dispose(); return; }

        RoundRectangle2D frame = new RoundRectangle2D.Double(0, 0, W - 1, H - 1, 28, 28);
        g.setClip(frame);

        double day = daylight(hour);
        double f = Theme.clamp(0.16 + day * (0.10 + 0.70 * curtain) + lamp * 0.78, 0, 1);
        int floorY = (int) (H * 0.74);

        // қабырға мен еден
        g.setPaint(new GradientPaint(0, 0, shade(new Color(0xD8CDBC), f * 0.9), 0, floorY, shade(new Color(0xCBBFAC), f)));
        g.fillRect(0, 0, W, floorY);
        g.setColor(shade(new Color(0xEDE6DA), f));
        g.fillRect(0, floorY - 9, W, 9);
        g.setPaint(new GradientPaint(0, floorY, shade(new Color(0x92704F), f), 0, H, shade(new Color(0x6E5238), f)));
        g.fillRect(0, floorY, W, H - floorY);
        g.setColor(shade(new Color(0x5E452F), f));
        for (int i = 1; i < 6; i++) {
            int y = floorY + i * (H - floorY) / 6;
            g.drawLine(0, y, W, y);
        }

        drawWindow(g, W, H, f, day);
        drawCabinet(g, W, H, floorY, f);
        drawTv(g, W, H);
        drawBed(g, W, H, floorY, f);
        drawAc(g, W, H, f);
        drawDoor(g, W, H, floorY, f);
        drawLamp(g, W, H, floorY);

        // терезеден түскен күн сәулесі
        if (day > 0.15 && curtain > 0.1) {
            int wx = (int) (W * .05), ww = (int) (W * .32);
            Path2D p = new Path2D.Double();
            p.moveTo(wx + ww * 0.10, floorY + 4);
            p.lineTo(wx + ww * 0.95, floorY + 4);
            p.lineTo(wx + ww * 1.25, H - 6);
            p.lineTo(wx + ww * 0.25, H - 6);
            p.closePath();
            g.setColor(new Color(255, 240, 190, (int) (46 * day * curtain)));
            g.fill(p);
        }

        // виньетка
        g.setPaint(new RadialGradientPaint(new Point2D.Double(W / 2.0, H / 2.0), (float) (Math.max(W, H) * 0.78),
                new float[]{0.55f, 1f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)}));
        g.fillRect(0, 0, W, H);

        g.setClip(null);
        g.setColor(Theme.BORDER);
        g.draw(frame);
        g.dispose();
    }

    private void drawWindow(Graphics2D g, int W, int H, double f, double day) {
        int wx = (int) (W * .05), wy = (int) (H * .10), ww = (int) (W * .32), wh = (int) (H * .46);
        g.setColor(shade(new Color(0xF3EEE4), f));
        g.fillRoundRect(wx - 9, wy - 9, ww + 18, wh + 18, 12, 12);

        Shape saved = g.getClip();
        g.clip(new Rectangle(wx, wy, ww, wh));
        Color top = sky(KT), bottom = sky(KB);
        g.setPaint(new GradientPaint(0, wy, top, 0, wy + wh, bottom));
        g.fillRect(wx, wy, ww, wh);

        // жұлдыздар
        double night = Theme.clamp((0.4 - day) / 0.4, 0, 1);
        if (night > 0) {
            for (int i = 0; i < stars.length; i++) {
                double[] s = stars[i];
                int a = (int) (230 * night * (0.55 + 0.45 * Math.sin(phase * 2 + s[3])));
                g.setColor(new Color(255, 255, 255, Math.max(0, a)));
                double sz = s[2];
                g.fill(new Ellipse2D.Double(wx + s[0] * ww, wy + s[1] * wh, sz, sz));
            }
        }

        // күн немесе ай
        if (hour >= 6 && hour <= 18) {
            double t = (hour - 6) / 12.0;
            double sx = wx + ww * (0.1 + 0.8 * t), sy = wy + wh * (0.74 - 0.60 * Math.sin(Math.PI * t));
            g.setPaint(new RadialGradientPaint(new Point2D.Double(sx, sy), 70f, new float[]{0f, 1f},
                    new Color[]{new Color(255, 236, 170, 190), new Color(255, 236, 170, 0)}));
            g.fill(new Ellipse2D.Double(sx - 70, sy - 70, 140, 140));
            g.setColor(new Color(255, 242, 190));
            g.fill(new Ellipse2D.Double(sx - 17, sy - 17, 34, 34));
        } else {
            double h2 = hour > 18 ? hour - 18 : hour + 6;
            double t = h2 / 12.0;
            double mx = wx + ww * (0.1 + 0.8 * t), my = wy + wh * (0.60 - 0.46 * Math.sin(Math.PI * t));
            g.setPaint(new RadialGradientPaint(new Point2D.Double(mx, my), 50f, new float[]{0f, 1f},
                    new Color[]{new Color(210, 225, 255, 90), new Color(210, 225, 255, 0)}));
            g.fill(new Ellipse2D.Double(mx - 50, my - 50, 100, 100));
            g.setColor(new Color(238, 241, 252));
            g.fill(new Ellipse2D.Double(mx - 14, my - 14, 28, 28));
            g.setColor(top);
            g.fill(new Ellipse2D.Double(mx - 6, my - 18, 26, 26));
        }

        // бұлттар
        g.setColor(new Color(255, 255, 255, (int) (150 * day)));
        for (int i = 0; i < 2; i++) {
            double cx = wx + ((phase * (5 + i * 3) + i * ww * 0.6) % (ww + 140)) - 70;
            double cy = wy + wh * (0.2 + 0.16 * i);
            g.fill(new Ellipse2D.Double(cx, cy, 56, 22));
            g.fill(new Ellipse2D.Double(cx + 16, cy - 10, 40, 24));
            g.fill(new Ellipse2D.Double(cx + 34, cy + 2, 46, 18));
        }

        // төбелер
        Color farHill = Theme.lerp(new Color(0x0F1A2C), new Color(0x4E8A78), day);
        Color nearHill = Theme.lerp(new Color(0x0A121F), new Color(0x2F6B55), day);
        Path2D far = new Path2D.Double();
        far.moveTo(wx, wy + wh * .74);
        far.quadTo(wx + ww * .25, wy + wh * .56, wx + ww * .5, wy + wh * .74);
        far.quadTo(wx + ww * .78, wy + wh * .90, wx + ww, wy + wh * .70);
        far.lineTo(wx + ww, wy + wh); far.lineTo(wx, wy + wh); far.closePath();
        g.setColor(farHill); g.fill(far);
        Path2D near = new Path2D.Double();
        near.moveTo(wx, wy + wh * .90);
        near.quadTo(wx + ww * .35, wy + wh * .72, wx + ww * .65, wy + wh * .90);
        near.quadTo(wx + ww * .85, wy + wh, wx + ww, wy + wh * .86);
        near.lineTo(wx + ww, wy + wh); near.lineTo(wx, wy + wh); near.closePath();
        g.setColor(nearHill); g.fill(near);

        // шыны шағылысы
        g.setColor(new Color(255, 255, 255, 22));
        Path2D gl = new Path2D.Double();
        gl.moveTo(wx + ww * .12, wy); gl.lineTo(wx + ww * .34, wy);
        gl.lineTo(wx + ww * .10, wy + wh); gl.lineTo(wx - ww * .08, wy + wh); gl.closePath();
        g.fill(gl);
        g.setClip(saved);

        // жақтау
        g.setColor(shade(new Color(0xF3EEE4), f));
        g.fillRect(wx + ww / 2 - 2, wy, 4, wh);
        g.fillRect(wx, wy + (int) (wh * .62), ww, 4);

        // перде
        int rodY = wy - 20;
        double cw = 16 + (1 - curtain) * (ww / 2.0 - 8);
        Color cc = shade(new Color(0x9B3550), 0.25 + f * 0.75);
        int ch = wh + 44;
        drawCurtain(g, wx - 12, rodY + 8, (int) cw + 12, ch, cc, true);
        drawCurtain(g, (int) (wx + ww - cw), rodY + 8, (int) cw + 12, ch, cc, false);
        g.setColor(shade(new Color(0xCDA45E), f));
        g.fillRoundRect(wx - 24, rodY, ww + 48, 7, 7, 7);
        g.fillOval(wx - 30, rodY - 3, 13, 13);
        g.fillOval(wx + ww + 17, rodY - 3, 13, 13);

        chip(g, "Перде · " + (int) Math.round(curtainT * 100) + "%", wx + ww / 2.0, wy + wh + 32, Theme.GOLD);
    }

    private void drawCurtain(Graphics2D g, int x, int y, int w, int h, Color c, boolean left) {
        g.setColor(c);
        g.fillRoundRect(x, y, w, h, 12, 12);
        int n = Math.max(2, w / 14);
        for (int i = 0; i < n; i++) {
            int fx = x + (int) ((i + 0.5) * w / n);
            g.setColor(new Color(0, 0, 0, 46));
            g.fillRect(fx, y + 4, Math.max(2, w / n / 2), h - 8);
            g.setColor(new Color(255, 255, 255, 18));
            g.fillRect(fx - Math.max(2, w / n / 3), y + 4, 2, h - 8);
        }
    }

    private void drawCabinet(Graphics2D g, int W, int H, int floorY, double f) {
        int x = (int) (W * .05) - 6, w = (int) (W * .32) + 12;
        int y = (int) (H * .635), h = floorY - y + 10;
        g.setColor(new Color(0, 0, 0, 40));
        g.fillRoundRect(x + 4, y + 6, w, h, 10, 10);
        g.setColor(shade(new Color(0x6E5039), f));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(shade(new Color(0x8A6849), f));
        g.fillRoundRect(x - 3, y - 6, w + 6, 10, 8, 8);
        g.setColor(shade(new Color(0x553C2A), f));
        g.drawLine(x + w / 2, y + 8, x + w / 2, y + h - 4);
        g.setColor(shade(new Color(0xCDA45E), f));
        g.fillOval(x + w / 2 - 12, y + h / 2 - 3, 6, 6);
        g.fillOval(x + w / 2 + 6, y + h / 2 - 3, 6, 6);
    }

    private void drawBed(Graphics2D g, int W, int H, int floorY, double f) {
        int bx = (int) (W * .50), bw = (int) (W * .37);
        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(bx - 12, (int) (H * .775), bw + 24, (int) (H * .07));
        g.setColor(shade(new Color(0x4A5670), f));
        g.fillRoundRect(bx - 8, (int) (H * .41), bw + 16, (int) (H * .27), 24, 24);
        g.setColor(shade(new Color(0x3C475E), f));
        for (int i = 1; i < 6; i++) g.drawLine(bx + bw * i / 6, (int) (H * .44), bx + bw * i / 6, (int) (H * .64));
        g.setColor(shade(new Color(0xF0ECE4), f));
        g.fillRoundRect(bx, (int) (H * .575), bw, (int) (H * .23), 18, 18);
        g.setColor(shade(new Color(0xFFFFFF), f));
        g.fillRoundRect(bx + (int) (bw * .06), (int) (H * .545), (int) (bw * .41), (int) (H * .08), 16, 16);
        g.fillRoundRect(bx + (int) (bw * .53), (int) (H * .545), (int) (bw * .41), (int) (H * .08), 16, 16);
        g.setColor(shade(new Color(0xBBB5A8), f));
        g.drawRoundRect(bx + (int) (bw * .06), (int) (H * .545), (int) (bw * .41), (int) (H * .08), 16, 16);
        g.drawRoundRect(bx + (int) (bw * .53), (int) (H * .545), (int) (bw * .41), (int) (H * .08), 16, 16);
        g.setColor(shade(new Color(0xB48F55), f));
        g.fillRoundRect(bx, (int) (H * .665), bw, (int) (H * .14), 18, 18);
        g.setColor(shade(new Color(0xD3B27A), f));
        g.fillRoundRect(bx, (int) (H * .655), bw, 12, 8, 8);
    }

    private void drawTv(Graphics2D g, int W, int H) {
        int tx = (int) (W * .56), ty = (int) (H * .205), tw = (int) (W * .27), th = (int) (H * .185);
        if (tv > 0.02) {
            g.setPaint(new RadialGradientPaint(new Point2D.Double(tx + tw / 2.0, ty + th / 2.0), (float) (tw * 1.05),
                    new float[]{0f, 1f}, new Color[]{new Color(110, 160, 255, (int) (80 * tv)), new Color(110, 160, 255, 0)}));
            g.fillRect(tx - tw, ty - th, tw * 3, th * 3);
        }
        g.setColor(new Color(0x0A0C11));
        g.fillRoundRect(tx - 5, ty - 5, tw + 10, th + 10, 12, 12);
        Shape saved = g.getClip();
        g.clip(new Rectangle(tx, ty, tw, th));
        Color off = new Color(0x13161D);
        if (tv > 0.02) {
            Color c1 = Theme.lerp(off, Color.getHSBColor((float) ((phase * 0.03) % 1.0), 0.55f, 0.95f), tv);
            Color c2 = Theme.lerp(off, Color.getHSBColor((float) ((phase * 0.03 + 0.2) % 1.0), 0.75f, 0.55f), tv);
            g.setPaint(new GradientPaint(tx, ty, c1, tx + tw, ty + th, c2));
            g.fillRect(tx, ty, tw, th);
            g.setColor(new Color(255, 255, 255, (int) (38 * tv)));
            g.fill(new Ellipse2D.Double(tx + (0.5 + 0.38 * Math.sin(phase * 0.7)) * tw - 24, ty + th * .25, 48, 48));
            g.fill(new Ellipse2D.Double(tx + (0.5 + 0.38 * Math.cos(phase * 0.5)) * tw - 16, ty + th * .5, 32, 32));
            g.setFont(Theme.font(Font.BOLD, 13));
            g.setColor(new Color(255, 255, 255, (int) (220 * tv)));
            String s = "RIXOS TV";
            g.drawString(s, tx + (tw - g.getFontMetrics().stringWidth(s)) / 2, ty + th / 2 + 4);
            int bw = (int) (tw * .36);
            g.setColor(new Color(255, 255, 255, (int) (90 * tv)));
            g.fillRoundRect(tx + 10, ty + th - 14, bw, 5, 5, 5);
            g.setColor(new Color(255, 255, 255, (int) (230 * tv)));
            g.fillRoundRect(tx + 10, ty + th - 14, (int) (bw * tvVol / 100.0), 5, 5, 5);
        } else {
            g.setColor(off);
            g.fillRect(tx, ty, tw, th);
            g.setColor(new Color(255, 255, 255, 14));
            Path2D p = new Path2D.Double();
            p.moveTo(tx + tw * .1, ty); p.lineTo(tx + tw * .4, ty);
            p.lineTo(tx + tw * .15, ty + th); p.lineTo(tx - tw * .1, ty + th); p.closePath();
            g.fill(p);
        }
        g.setClip(saved);
    }

    private void drawAc(Graphics2D g, int W, int H, double f) {
        int ax = (int) (W * .55), ay = (int) (H * .04), aw = (int) (W * .30), ah = (int) (H * .085);
        g.setColor(new Color(0, 0, 0, 45));
        g.fillRoundRect(ax + 2, ay + 5, aw, ah, 18, 18);
        g.setColor(shade(new Color(0xF4F6F8), f * 0.45 + 0.55));
        g.fillRoundRect(ax, ay, aw, ah, 18, 18);
        g.setColor(shade(new Color(0xC3CAD2), f * 0.5 + 0.5));
        g.fillRoundRect(ax + 12, ay + ah - 13, aw - 24, 6, 6, 6);
        g.setFont(Theme.font(Font.BOLD, 13));
        g.setColor(acT > 0.5 ? new Color(0x2E86DE) : new Color(0x9AA3AE));
        g.drawString(acT > 0.5 ? acTemp + "°C" : "OFF", ax + 16, ay + ah / 2 + 2);
        g.setColor(ac > 0.5 ? new Color(0x2ED573) : new Color(0x9AA3AE));
        g.fillOval(ax + aw - 20, ay + 11, 7, 7);

        if (ac > 0.03) {
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (double[] p : flow) {
                double prog = (p[1] + phase * p[2]) % 1.0;
                double px = ax + aw * (0.10 + 0.8 * p[0]) + Math.sin(phase * 1.6 + p[0] * 9) * 6 * prog;
                double py = ay + ah - 3 + prog * (H * 0.075);
                int a = (int) (170 * (1 - prog) * ac);
                g.setColor(new Color(190, 230, 255, Math.max(0, a)));
                g.draw(new Line2D.Double(px, py, px + Math.sin(phase * 2 + p[0] * 5) * 2, py + 9));
            }
            g.setStroke(new BasicStroke(1f));
        }
    }

    private void drawDoor(Graphics2D g, int W, int H, int floorY, double f) {
        int dx = (int) (W * .905), dy = (int) (H * .14), dw = (int) (W * .085), dh = floorY - dy;
        g.setColor(shade(new Color(0xEDE6DA), f));
        g.fillRect(dx - 6, dy - 6, dw + 12, dh + 6);
        g.setColor(shade(new Color(0x7B5A3C), f));
        g.fillRect(dx, dy, dw, dh);
        g.setColor(shade(new Color(0x684A30), f));
        g.drawRect(dx + 8, dy + 14, dw - 16, dh / 2 - 22);
        g.drawRect(dx + 8, dy + dh / 2, dw - 16, dh / 2 - 20);
        g.setColor(shade(new Color(0xD7B26E), f));
        g.fillOval(dx + 9, dy + (int) (dh * .52), 9, 9);
        Color led = locked ? Theme.GREEN : Theme.AMBER;
        g.setColor(Theme.alpha(led, 70));
        g.fillOval(dx + dw - 22, dy + 8, 14, 14);
        g.setColor(led);
        g.fillOval(dx + dw - 19, dy + 11, 8, 8);
        chip(g, locked ? "Құлыптаулы" : "Ашық", dx + dw / 2.0, dy + dh * 0.30, led);
    }

    private void drawLamp(Graphics2D g, int W, int H, int floorY) {
        int lx = (int) (W * .435), ly = (int) (H * .21);
        if (lamp > 0.02) {
            // жарық конусы
            Path2D cone = new Path2D.Double();
            cone.moveTo(lx - 22, ly + 6); cone.lineTo(lx + 22, ly + 6);
            cone.lineTo(lx + W * .30, floorY + (H - floorY) * .8);
            cone.lineTo(lx - W * .30, floorY + (H - floorY) * .8);
            cone.closePath();
            g.setPaint(new GradientPaint(0, ly, new Color(255, 222, 150, (int) (70 * lamp)),
                    0, floorY + (H - floorY) * .8f, new Color(255, 222, 150, 0)));
            g.fill(cone);
            g.setPaint(new RadialGradientPaint(new Point2D.Double(lx, ly + 10), (float) (W * .42),
                    new float[]{0f, 1f}, new Color[]{new Color(255, 214, 140, (int) (110 * lamp)), new Color(255, 214, 140, 0)}));
            g.fillRect(0, 0, W, H);
        }
        g.setColor(new Color(0x2B2F38));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(lx, 0, lx, ly - 26);
        g.setStroke(new BasicStroke(1f));
        Path2D shadeP = new Path2D.Double();
        shadeP.moveTo(lx - 30, ly + 4); shadeP.lineTo(lx + 30, ly + 4);
        shadeP.quadTo(lx + 14, ly - 24, lx, ly - 28);
        shadeP.quadTo(lx - 14, ly - 24, lx - 30, ly + 4);
        g.setColor(new Color(0x30343E));
        g.fill(shadeP);
        g.setColor(new Color(0xC9A15B));
        g.fillRoundRect(lx - 30, ly + 2, 60, 5, 5, 5);
        Color bulb = Theme.lerp(new Color(0x59606B), new Color(255, 236, 160), lamp);
        g.setColor(Theme.alpha(bulb, 255));
        g.fill(new Ellipse2D.Double(lx - 11, ly + 4, 22, 22));
        chip(g, lampT > 0 ? "Жарық · " + (int) Math.round(lampT * 100) + "%" : "Жарық · өшірулі",
                lx, ly + 52, lampT > 0 ? Theme.AMBER : Theme.MUTED);
    }
}
