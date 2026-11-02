package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;

/** Small stat chip such as SCORE 120. */
public class Badge extends JComponent {
    private static final long serialVersionUID = 1L;
    private final String label;
    private final Color fill, stroke;
    private String value = "0";
    private Color accent = null;

    /** Colour of the small label (e.g. cyan for SCORE). */
    public Badge accent(Color c) { this.accent = c; return this; }

    public Badge(String label, Color fill, Color stroke) {
        this.label = label;
        this.fill = fill;
        this.stroke = stroke;
    }

    public void setValue(String v) { this.value = v; revalidate(); repaint(); }

    @Override public Dimension getPreferredSize() {
        int w = Math.max(getFontMetrics(Theme.bold(11)).stringWidth(label), getFontMetrics(Theme.bold(20)).stringWidth(value));
        return new Dimension(Math.max(84, w + 34), 52);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        g.setColor(fill);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 32, 32);
        g.setColor(stroke != null ? stroke : Theme.BORDER);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 32, 32);
        g.setFont(Theme.bold(11));
        g.setColor(accent != null ? accent : Theme.SMALL);
        g.drawString(label, 17, 19);
        g.setFont(Theme.bold(20));
        g.setColor(Theme.TEXT);
        g.drawString(value, 17, 42);
        g.dispose();
    }
}
