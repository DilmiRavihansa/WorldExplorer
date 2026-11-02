package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;

/** Keeps its content centred and at most maxWidth wide, like the 1296px column in the design. */
public class CenterColumn extends JPanel {
    private static final long serialVersionUID = 1L;

    public CenterColumn(Component content, int maxWidth, int sidePad, int topPad, int bottomPad) {
        super(null);
        setOpaque(false);
        add(content);
        setLayout(new LayoutManager() {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension preferredLayoutSize(Container p) {
                Dimension d = content.getPreferredSize();
                return new Dimension(maxWidth + 2 * sidePad, d.height + topPad + bottomPad);
            }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(400, 200); }
            @Override public void layoutContainer(Container p) {
                Insets in = p.getInsets();
                int w = Math.min(maxWidth, p.getWidth() - 2 * sidePad - in.left - in.right);
                int x = (p.getWidth() - w) / 2;
                content.setBounds(x, topPad, w, p.getHeight() - topPad - bottomPad);
            }
        });
    }
}
