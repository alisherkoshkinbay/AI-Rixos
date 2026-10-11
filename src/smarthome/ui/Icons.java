package smarthome.ui;

import java.awt.*;
import java.awt.geom.*;

// Құрылғы белгішелері (эмодзи қолданбай, Java2D-мен салынады)
public final class Icons {
    private Icons() {}

    public static void draw(Graphics2D g0, String id, double cx, double cy, double s, Color c) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.setColor(c);
        g.setStroke(new BasicStroke((float) (s * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (id) {
            case "light": {
                Ellipse2D bulb = new Ellipse2D.Double(cx - s * .28, cy - s * .42, s * .56, s * .56);
                g.setColor(Theme.alpha(c, 55)); g.fill(bulb);
                g.setColor(c); g.draw(bulb);
                g.draw(new Line2D.Double(cx - s * .13, cy + s * .26, cx + s * .13, cy + s * .26));
                g.draw(new Line2D.Double(cx - s * .08, cy + s * .37, cx + s * .08, cy + s * .37));
                break;
            }
            case "ac": {
                double r = s * .38;
                for (int i = 0; i < 3; i++) {
                    double a = Math.toRadians(i * 60 + 90);
                    double dx = Math.cos(a) * r, dy = Math.sin(a) * r;
                    g.draw(new Line2D.Double(cx - dx, cy - dy, cx + dx, cy + dy));
                }
                g.fill(new Ellipse2D.Double(cx - s * .07, cy - s * .07, s * .14, s * .14));
                break;
            }
            case "curtains": {
                g.draw(new Line2D.Double(cx - s * .4, cy - s * .36, cx + s * .4, cy - s * .36));
                RoundRectangle2D l = new RoundRectangle2D.Double(cx - s * .36, cy - s * .30, s * .30, s * .68, 6, 6);
                RoundRectangle2D r = new RoundRectangle2D.Double(cx + s * .06, cy - s * .30, s * .30, s * .68, 6, 6);
                g.setColor(Theme.alpha(c, 55)); g.fill(l); g.fill(r);
                g.setColor(c); g.draw(l); g.draw(r);
                break;
            }
            case "tv": {
                RoundRectangle2D scr = new RoundRectangle2D.Double(cx - s * .42, cy - s * .32, s * .84, s * .52, 6, 6);
                g.setColor(Theme.alpha(c, 55)); g.fill(scr);
                g.setColor(c); g.draw(scr);
                g.draw(new Line2D.Double(cx - s * .16, cy + s * .36, cx + s * .16, cy + s * .36));
                g.draw(new Line2D.Double(cx, cy + s * .2, cx, cy + s * .36));
                break;
            }
            case "lock":
            case "unlock": {
                boolean open = id.equals("unlock");
                RoundRectangle2D body = new RoundRectangle2D.Double(cx - s * .28, cy - s * .04, s * .56, s * .4, 6, 6);
                g.setColor(Theme.alpha(c, 70)); g.fill(body);
                g.setColor(c); g.draw(body);
                double lift = open ? s * .14 : 0;
                Arc2D arc = new Arc2D.Double(cx - s * .17, cy - s * .38 - lift, s * .34, s * .68, 0, 180, Arc2D.OPEN);
                g.draw(arc);
                g.draw(new Line2D.Double(cx + s * .17, cy - s * .04 - lift, cx + s * .17, cy - s * .04));
                if (!open) g.draw(new Line2D.Double(cx - s * .17, cy - s * .04, cx - s * .17, cy - s * .04 + 1));
                break;
            }
            default:
                g.draw(new Ellipse2D.Double(cx - s * .3, cy - s * .3, s * .6, s * .6));
        }
        g.dispose();
    }
}
