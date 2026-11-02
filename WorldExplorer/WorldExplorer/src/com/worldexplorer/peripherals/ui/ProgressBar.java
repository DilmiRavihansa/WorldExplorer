package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;

/** Thin rounded progress bar (value 0..1). */
public class ProgressBar extends JComponent {
    private static final long serialVersionUID = 1L;
    private double value = 0;
    private Color color = Theme.TEAL;

    /** End colour of the bar gradient (default cyan). */
    public void setColor(Color c) { this.color = c; repaint(); }
    private final int thickness;
    private final Color track = new Color(0x1B2547);

    public ProgressBar(int thickness) { this.thickness = thickness; }

    public void setValue(double v) { this.value = Math.max(0, Math.min(1, v)); repaint(); }

    @Override public Dimension getPreferredSize() { return new Dimension(100, thickness + 4); }
    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, thickness); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        g.setColor(track);
        g.fillRoundRect(0, 0, getWidth(), thickness, thickness, thickness);
        int w = (int) Math.round(getWidth() * value);
        if (w > 0) {
            int bw = Math.max(w, thickness);
            g.setColor(Theme.alpha(color, 40));
            g.fillRoundRect(-2, -2, bw + 4, thickness + 4, thickness + 4, thickness + 4);
            g.setPaint(new java.awt.GradientPaint(0, 0, Theme.VIOLET, bw, 0, color));
            g.fillRoundRect(0, 0, bw, thickness, thickness, thickness);
        }
        g.dispose();
    }
}
