package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Custom painted button with hover / pressed / focus states. */
public class RoundedButton extends JButton {
    private static final long serialVersionUID = 1L;

    public enum Style { PRIMARY, SECONDARY, TEAL, GHOST }

    private final Style style;
    private int arc = 28;

    public RoundedButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(Theme.bold(16));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public RoundedButton arc(int a) { this.arc = a; return this; }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) return super.getPreferredSize();
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 48, 48);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        int w = getWidth(), h = getHeight();
        boolean hover = getModel().isRollover() && isEnabled();
        boolean down = getModel().isPressed() && isEnabled();
        RoundRectangle2D shape = new RoundRectangle2D.Double(1, 1, w - 3, h - 3, arc, arc);
        Color text = Theme.INK;

        switch (style) {
            case PRIMARY:
                Color a = hover ? new Color(0xFFC02E) : Theme.AMBER, b = hover ? new Color(0xFF9420) : Theme.ORANGE;
                if (down) { a = a.darker(); b = b.darker(); }
                if (isEnabled()) for (int i = 4; i >= 1; i--) {
                    g.setColor(Theme.alpha(b, hover ? 20 : 12));
                    g.setStroke(new BasicStroke(i * 2.6f));
                    g.draw(shape);
                }
                g.setPaint(new GradientPaint(0, 0, a, w, h, b));
                g.fill(shape);
                break;
            case TEAL:
                text = Theme.INK;
                g.setColor(down ? Theme.TEAL_DARK.darker() : hover ? new Color(0x67E8F9) : Theme.TEAL);
                g.fill(shape);
                break;
            case SECONDARY:
                text = Theme.TEXT;
                g.setColor(down ? new Color(0x0F3A4D) : hover ? Theme.CARD_HI : Theme.CARD);
                g.fill(shape);
                g.setColor(hover ? Theme.TEAL : Theme.BORDER);
                g.setStroke(new BasicStroke(1.4f));
                g.draw(shape);
                break;
            default: // GHOST
                text = Theme.TEAL;
                if (hover) { g.setColor(Theme.CARD_HI); g.fill(shape); }
        }
        if (hasFocus() && isFocusPainted()) {
            g.setColor(Theme.alpha(Theme.TEAL, 140));
            g.setStroke(new BasicStroke(2f));
            g.draw(new RoundRectangle2D.Double(3, 3, w - 7, h - 7, arc - 4, arc - 4));
        }
        g.setFont(getFont());
        g.setColor(isEnabled() ? text : Theme.alpha(text, 110));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                (h + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
