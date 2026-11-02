package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A grid that reflows with the window: it puts as many columns as fit (each at least minColWidth wide, at most
 * maxCols) and drops to fewer columns on small windows. Optional weights give columns unequal widths
 * (only used when the full column count is shown). This is what makes the screens responsive.
 */
public class ResponsiveGrid extends JPanel {
    private static final long serialVersionUID = 1L;

    private final int maxCols, minColWidth, hgap, vgap;
    private final double[] weights;
    private int lastCols = -1, measuredW = -1, measuredH = -1;

    public ResponsiveGrid(int maxCols, int minColWidth, int hgap, int vgap, double... weights) {
        super(null);
        this.maxCols = maxCols;
        this.minColWidth = minColWidth;
        this.hgap = hgap;
        this.vgap = vgap;
        this.weights = weights.length == maxCols ? weights : null;
        setOpaque(false);
        setLayout(new LayoutManager() {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(minColWidth, 40); }
            @Override public Dimension preferredLayoutSize(Container p) { return measure(p, p.getWidth() > 0 ? p.getWidth() : 1000); }
            @Override public void layoutContainer(Container p) { doIt(p); }
        });
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (columns(getWidth()) != lastCols) SwingUtilities.invokeLater(() -> {
                    lastCols = columns(getWidth());
                    for (Container c = ResponsiveGrid.this; c != null; c = c.getParent()) c.invalidate();
                    revalidate();
                });
            }
        });
    }

    private int columns(int width) {
        int n = Math.max(1, Math.min(maxCols, (width + hgap) / (minColWidth + hgap)));
        return Math.min(n, Math.max(1, getComponentCount()));
    }

    private Dimension measure(Container p, int width) {
        int n = getComponentCount();
        if (n == 0) return new Dimension(0, 0);
        int cols = columns(width), h = 0;
        for (int i = 0; i < n; i += cols) {
            int rh = 0;
            for (int j = i; j < Math.min(n, i + cols); j++) rh = Math.max(rh, getComponent(j).getPreferredSize().height);
            h += rh + (i > 0 ? vgap : 0);
        }
        return new Dimension(width, h);
    }

    /** Real height once the width is known (wrapping text needs the final width to report its height). */
    @Override public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (measuredW == getWidth() && measuredH >= 0) return new Dimension(d.width, measuredH);
        return d;
    }

    private void doIt(Container p) {
        int n = getComponentCount();
        if (n == 0) return;
        Insets in = p.getInsets();
        int width = p.getWidth() - in.left - in.right;
        int cols = columns(width);
        lastCols = cols;
        double[] wt = new double[cols];
        double sum = 0;
        for (int i = 0; i < cols; i++) { wt[i] = (weights != null && cols == maxCols) ? weights[i] : 1; sum += wt[i]; }
        double avail = width - (cols - 1) * hgap;
        int y = in.top;
        for (int i = 0; i < n; i += cols) {
            int rh = 0;
            // pass 1: give every child its final width and lay it out so wrapped text reports a true height
            for (int j = i; j < Math.min(n, i + cols); j++) {
                Component c = getComponent(j);
                int cw = (int) Math.round(avail * wt[j - i] / sum);
                c.setSize(cw, Math.max(1, c.getHeight()));
                c.doLayout();
                if (c instanceof Container) ((Container) c).validate();
                rh = Math.max(rh, c.getPreferredSize().height);
            }
            double x = in.left;
            for (int j = i; j < Math.min(n, i + cols); j++) {
                int cw = (int) Math.round(avail * wt[j - i] / sum);
                getComponent(j).setBounds((int) Math.round(x), y, cw, rh);
                x += cw + hgap;
            }
            y += rh + vgap;
        }
        int total = y - vgap + in.bottom;
        measuredW = p.getWidth();
        if (total != measuredH) {
            measuredH = total;
            SwingUtilities.invokeLater(() -> {
                for (Container c = this; c != null; c = c.getParent()) c.invalidate();   // drop cached sizes up the tree
                revalidate();
            });
        }
    }
}
