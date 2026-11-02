package com.worldexplorer.peripherals.ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Vector illustrations from the UI kit, drawn with Java2D (no image files needed). */
public final class Art {
    private Art() { }

    private static Path2D poly(double x, double y, double... d) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(x, y);
        double cx = x, cy = y;
        for (int i = 0; i < d.length; i += 2) {
            cx += d[i];
            cy += d[i + 1];
            p.lineTo(cx, cy);
        }
        p.closePath();
        return p;
    }

    /** World Explorer globe with continents and a dotted route. (r = radius in pixels) */
    public static void globe(Graphics2D g0, double cx, double cy, double r, Color line, Color land, Color pin) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.translate(cx, cy);
        double s = r / 105.0;
        g.scale(s, s);
        g.translate(-170, -100);

        g.setColor(land);
        g.fill(poly(104, 57, 23, -17, 31, 6, 14, 19, -11, 15, -30, -2, -11, 20, -21, -14));
        g.fill(poly(194, 50, 35, 4, 20, 18, -13, 19, -28, -2, -15, -16));
        g.fill(poly(170, 118, 27, 5, 18, 29, -18, 36, -22, -12, -4, -27, -18, -11));

        g.setColor(Theme.alpha(line, 190));
        g.setStroke(new BasicStroke(3f));
        g.draw(new Ellipse2D.Double(65, -5, 210, 210));
        g.setStroke(new BasicStroke(2f));
        g.setColor(Theme.alpha(line, 165));
        g.draw(new Ellipse2D.Double(65, 61, 210, 78));
        g.draw(new Ellipse2D.Double(128, -5, 84, 210));
        g.setColor(Theme.alpha(line, 100));
        g.draw(new Ellipse2D.Double(98, -5, 144, 210));

        Path2D route = new Path2D.Double();
        route.moveTo(127, 68);
        route.quadTo(172, 35, 218, 83);
        route.quadTo(238, 115, 195, 153);
        g.setColor(pin);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 6f, 8f }, 0f));
        g.draw(route);
        g.setStroke(new BasicStroke(1f));
        for (double[] p : new double[][] { { 127, 68 }, { 218, 83 }, { 195, 153 } })
            g.fill(new Ellipse2D.Double(p[0] - 7, p[1] - 7, 14, 14));
        g.dispose();
    }

    /** Round brand mark used in the navigation bar. */
    public static void brand(Graphics2D g0, int x, int y, int d) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.setPaint(new java.awt.GradientPaint(x, y, Theme.VIOLET, x + d, y + d, Theme.TEAL));
        g.fill(new Ellipse2D.Double(x, y, d, d));
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        double c = d / 2.0, r = d / 4.0;
        g.draw(new Ellipse2D.Double(x + c - r, y + c - r, 2 * r, 2 * r));
        g.drawLine((int) (x + c - r), (int) (y + c), (int) (x + c + r), (int) (y + c));
        g.drawLine((int) (x + c), (int) (y + c - r), (int) (x + c), (int) (y + c + r));
        g.dispose();
    }

    /** Round avatar with an initial letter. */
    public static void avatar(Graphics2D g0, int x, int y, int d, String name) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.setColor(Theme.AVATAR_BG);
        g.fill(new Ellipse2D.Double(x, y, d, d));
        g.setColor(Theme.alpha(Theme.TEAL, 150));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Ellipse2D.Double(x + 0.75, y + 0.75, d - 1.5, d - 1.5));
        g.setColor(Theme.TEAL);
        g.setFont(Theme.bold(d * 0.42f));
        String s = (name == null || name.isEmpty()) ? "?" : name.substring(0, 1).toUpperCase();
        int w = g.getFontMetrics().stringWidth(s);
        g.drawString(s, x + (d - w) / 2, y + (d + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2);
        g.dispose();
    }

    /** Icons for the three game modes: 0 = country (globe), 1 = capital (pin), 2 = flag. */
    public static void modeIcon(Graphics2D g0, int kind, double cx, double cy, double r) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        g.setColor(new Color(0x1C2A55));
        g.fill(new Ellipse2D.Double(cx - r - 5, cy - r - 5, 2 * r + 10, 2 * r + 10));
        g.setColor(Theme.alpha(Theme.TEAL, 60));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Ellipse2D.Double(cx - r - 5, cy - r - 5, 2 * r + 10, 2 * r + 10));
        g.setColor(new Color(0x131E42));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setColor(Theme.TEAL);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double k = r / 54.0;
        if (kind == 0) {
            g.draw(new Ellipse2D.Double(cx - 30 * k, cy - 30 * k, 60 * k, 60 * k));
            g.draw(new Ellipse2D.Double(cx - 30 * k, cy - 11 * k, 60 * k, 22 * k));
            g.draw(new Ellipse2D.Double(cx - 12 * k, cy - 30 * k, 24 * k, 60 * k));
        } else if (kind == 1) {
            Path2D pin = new Path2D.Double();
            pin.moveTo(cx, cy + 32 * k);
            pin.curveTo(cx - 40 * k, cy - 4 * k, cx - 26 * k, cy - 34 * k, cx, cy - 34 * k);
            pin.curveTo(cx + 26 * k, cy - 34 * k, cx + 40 * k, cy - 4 * k, cx, cy + 32 * k);
            g.draw(pin);
            g.setColor(Theme.AMBER);
            g.fill(new Ellipse2D.Double(cx - 9 * k, cy - 20 * k, 18 * k, 18 * k));
        } else {
            g.drawLine((int) (cx - 24 * k), (int) (cy - 32 * k), (int) (cx - 24 * k), (int) (cy + 32 * k));
            Path2D flag = new Path2D.Double();
            flag.moveTo(cx - 24 * k, cy - 30 * k);
            flag.curveTo(cx - 6 * k, cy - 40 * k, cx + 6 * k, cy - 20 * k, cx + 28 * k, cy - 28 * k);
            flag.lineTo(cx + 28 * k, cy + 4 * k);
            flag.curveTo(cx + 6 * k, cy + 12 * k, cx - 6 * k, cy - 8 * k, cx - 24 * k, cy + 2 * k);
            flag.closePath();
            g.setColor(Theme.AMBER);
            g.fill(flag);
            g.setColor(Theme.TEAL);
            g.draw(flag);
        }
        g.dispose();
    }

    /** Soft drop shadow / neon glow drawn inside the given bounds (surface is inset by SHADOW px). */
    public static void shadow(Graphics2D g0, int x, int y, int w, int h, int arc, Color glow) {
        Graphics2D g = (Graphics2D) g0.create();
        Color base = glow == null ? new Color(0, 0, 0) : glow;
        int a = glow == null ? 38 : 22;
        for (int i = RoundedPanel.SHADOW - 2; i >= 0; i--) {
            g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.max(2, a / 5 + (glow == null ? 0 : 2))));
            g.fill(new RoundRectangle2D.Double(x - i, y - i + (glow == null ? 6 : 2), w + 2 * i, h + 2 * i, arc + 2 * i, arc + 2 * i));
        }
        g.dispose();
    }
}
