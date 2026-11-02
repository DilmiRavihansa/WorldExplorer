package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Dark "space" page background (neon glows + stars) that always matches the window width and
 * scrolls vertically when the window is too small.
 */
public class ScrollPage extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    public ScrollPage() { setBackground(Theme.BG); }

    /** Wraps the page in a borderless scroll pane. */
    public JScrollPane scroller() {
        JScrollPane sp = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.BG);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        return sp;
    }

    /** Soft radial glow. */
    public static void glow(Graphics2D g, double cx, double cy, double r, Color c) {
        g.setPaint(new RadialGradientPaint((float) cx, (float) cy, (float) r, new float[] { 0f, 1f },
                new Color[] { c, Theme.alpha(c, 0) }));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
    }

    /** Paints the shared background: glows and a fixed star field. */
    public static void paintSpace(Graphics2D g, int w, int h) {
        g.setColor(Theme.BG);
        g.fillRect(0, 0, w, h);
        glow(g, w * 0.88, 80, 380, new Color(0x8B, 0x5C, 0xF6, 70));
        glow(g, w * 0.08, h * 0.85, 440, new Color(0x22, 0xD3, 0xEE, 46));
        Random r = new Random(7);
        for (int i = 0; i < 70; i++) {
            int x = r.nextInt(2400), y = r.nextInt(1800);
            float a = 0.15f + r.nextFloat() * 0.45f;
            int d = r.nextInt(5) == 0 ? 3 : 2;
            if (x < w && y < h) {
                g.setColor(new Color(1f, 1f, 1f, a));
                g.fillOval(x, y, d, d);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        paintSpace(g, getWidth(), getHeight());
        g.dispose();
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return o == SwingConstants.VERTICAL ? r.height - 40 : r.width - 40;
    }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() {
        return getParent() != null && getParent().getHeight() > getPreferredSize().height;
    }

    /** Slim dark scroll bar. */
    private static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { trackColor = Theme.BG; thumbColor = Theme.BORDER; }
        @Override protected javax.swing.JButton createDecreaseButton(int o) { return zero(); }
        @Override protected javax.swing.JButton createIncreaseButton(int o) { return zero(); }
        private javax.swing.JButton zero() {
            javax.swing.JButton b = new javax.swing.JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintTrack(Graphics g, javax.swing.JComponent c, Rectangle r) {
            g.setColor(Theme.BG);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        @Override protected void paintThumb(Graphics g0, javax.swing.JComponent c, Rectangle r) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            g.setColor(isThumbRollover() ? Theme.TEAL_DARK : new Color(0x34, 0x44, 0x7A));
            g.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g.dispose();
        }
    }
}
