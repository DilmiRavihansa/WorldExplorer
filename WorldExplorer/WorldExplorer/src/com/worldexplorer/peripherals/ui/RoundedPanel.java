package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** A card: rounded corners, optional border, gradient fill and a soft glow (dark theme). */
public class RoundedPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final int SHADOW = 14;

    private final int radius;
    private Color fill;
    private Color gradientEnd = null;
    private Color stroke = null;
    private Color glow = null;
    private boolean shadow = false;

    public RoundedPanel(int radius, Color fill) {
        this.radius = radius;
        this.fill = fill;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color fill, LayoutManager lm) {
        this(radius, fill);
        setLayout(lm);
    }

    public RoundedPanel stroke(Color c) { this.stroke = c; repaint(); return this; }
    public RoundedPanel shadow(boolean on) { this.shadow = on; return this; }
    public RoundedPanel fill(Color c) { this.fill = c; repaint(); return this; }
    /** Coloured neon glow around the card (null = none). */
    public RoundedPanel glow(Color c) { this.glow = c; repaint(); return this; }
    public RoundedPanel gradient(Color from, Color to) { this.fill = from; this.gradientEnd = to; return this; }

    /** Inner padding. The shadow margin is added automatically. */
    public RoundedPanel padding(int top, int left, int bottom, int right) {
        int s = shadow ? SHADOW : 0;
        setBorder(BorderFactory.createEmptyBorder(top + s, left + s, bottom + s, right + s));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        int s = shadow ? SHADOW : 0;
        int x = s, y = s - (shadow ? 4 : 0), w = getWidth() - 2 * s, h = getHeight() - 2 * s;
        if (shadow) Art.shadow(g, x, y, w, h, radius * 2, glow);
        RoundRectangle2D shape = new RoundRectangle2D.Double(x, y, w - 1, h - 1, radius * 2, radius * 2);
        g.setPaint(gradientEnd == null ? fill : new GradientPaint(x, y, fill, x + w, y + h, gradientEnd));
        g.fill(shape);
        if (gradientEnd == null) {   // faint top light, gives the card a lit "glass" feel
            g.setClip(shape);
            g.setPaint(new GradientPaint(0, y, new Color(255, 255, 255, 14), 0, y + Math.min(90, h), new Color(255, 255, 255, 0)));
            g.fillRect(x, y, w, Math.min(90, h));
            g.setClip(null);
        }
        if (stroke != null) {
            g.setColor(stroke);
            g.setStroke(new BasicStroke(1.2f));
            g.draw(shape);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
