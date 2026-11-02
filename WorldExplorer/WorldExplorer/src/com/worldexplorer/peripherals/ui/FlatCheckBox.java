package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Check box styled like the "Remember me" control in the design. */
public class FlatCheckBox extends JCheckBox {
    private static final long serialVersionUID = 1L;

    public FlatCheckBox(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setFont(Theme.plain(16));
        setForeground(Theme.MUTED);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(getFontMetrics(getFont()).stringWidth(getText()) + 34, 26);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        int y = (getHeight() - 20) / 2;
        RoundRectangle2D box = new RoundRectangle2D.Double(1, y, 19, 19, 10, 10);
        if (isSelected()) {
            g.setColor(Theme.TEAL);
            g.fill(box);
            g.setColor(Theme.INK);
            g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawPolyline(new int[] { 6, 10, 16 }, new int[] { y + 10, y + 14, y + 6 }, 3);
        } else {
            g.setColor(Theme.INPUT_BG);
            g.fill(box);
            g.setColor(hasFocus() ? Theme.TEAL : Theme.INPUT_BORDER);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(box);
        }
        g.setFont(getFont());
        g.setColor(getForeground());
        g.drawString(getText(), 32, (getHeight() + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2);
        g.dispose();
    }
}
