package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Answer choice: a numbered row ("1  Australia") or, when compact, a square number button (bonus round).
 * The state changes colour after the player has answered (green = right, red = wrong).
 */
public class AnswerButton extends JButton {
    private static final long serialVersionUID = 1L;

    public enum State { NORMAL, CORRECT, WRONG, DIMMED }

    private final String number;
    private final boolean compact;
    private State state = State.NORMAL;

    public AnswerButton(String number, String text, boolean compact) {
        super(text);
        this.number = number;
        this.compact = compact;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(compact ? new Dimension(56, 56) : new Dimension(200, 62));
        setToolTipText(compact ? null : "Press " + number);
    }

    public void setState(State s) { this.state = s; repaint(); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        int w = getWidth(), h = getHeight();
        boolean hover = getModel().isRollover() && isEnabled() && state == State.NORMAL;
        Color fill = Theme.CARD, line = Theme.BORDER, numBg = Theme.CARD_HI, text = Theme.TEXT;
        switch (state) {
            case CORRECT: fill = Theme.GREEN_BG; line = Theme.GREEN; numBg = Theme.GREEN; break;
            case WRONG: fill = Theme.RED_BG; line = Theme.RED; numBg = Theme.RED; break;
            case DIMMED: text = Theme.alpha(Theme.TEXT, 90); line = Theme.alpha(Theme.BORDER, 120); break;
            default:
                if (hover) { fill = Theme.CARD_HI; line = Theme.TEAL; numBg = Theme.TEAL; }
                if (getModel().isPressed()) fill = new Color(0x0F3A4D);
        }
        RoundRectangle2D shape = new RoundRectangle2D.Double(1, 1, w - 3, h - 3, compact ? 30 : 28, compact ? 30 : 28);
        if (hover || state == State.CORRECT || state == State.WRONG) {
            Color gc = state == State.CORRECT ? Theme.GREEN : state == State.WRONG ? Theme.RED : Theme.TEAL;
            for (int i = 5; i >= 1; i--) {
                g.setColor(Theme.alpha(gc, 14));
                g.setStroke(new BasicStroke(i * 2.4f));
                g.draw(shape);
            }
        }
        g.setColor(fill);
        g.fill(shape);
        g.setColor(line);
        g.setStroke(new BasicStroke(hover || state == State.CORRECT || state == State.WRONG ? 2f : 1.2f));
        g.draw(shape);
        if (hasFocus() && state == State.NORMAL) {
            g.setColor(Theme.alpha(Theme.TEAL, 120));
            g.setStroke(new BasicStroke(2f));
            g.draw(new RoundRectangle2D.Double(4, 4, w - 9, h - 9, 22, 22));
        }

        if (compact) {
            g.setFont(Theme.bold(22));
            g.setColor(hover ? Theme.TEAL : text);
            if (state == State.CORRECT || state == State.WRONG) { g.setColor(state == State.CORRECT ? Theme.GREEN : Theme.RED); }
            FontMetrics fm = g.getFontMetrics();
            g.drawString(getText(), (w - fm.stringWidth(getText())) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
        } else {
            g.setColor(numBg);
            g.fill(new Ellipse2D.Double(14, h / 2.0 - 15, 30, 30));
            g.setFont(Theme.bold(15));
            g.setColor(state == State.CORRECT || state == State.WRONG || hover ? Theme.INK : Theme.TEAL);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(number, 29 - fm.stringWidth(number) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
            g.setFont(Theme.font(java.awt.Font.BOLD, 18));
            g.setColor(text);
            fm = g.getFontMetrics();
            String t = getText();
            int max = w - 60 - 16;
            while (t.length() > 1 && fm.stringWidth(t) > max) t = t.substring(0, t.length() - 2) + "\u2026";
            g.drawString(t, 60, (h + fm.getAscent() - fm.getDescent()) / 2);
        }
        g.dispose();
    }
}
