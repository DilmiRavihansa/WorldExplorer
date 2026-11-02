package com.worldexplorer.peripherals.screens;

import com.worldexplorer.engine.GameEngine;
import com.worldexplorer.engine.GameServer;
import com.worldexplorer.peripherals.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/** Screen 02 - Mode select. Reports the chosen mode through Listener. */
public class ModeSelectScreen extends JPanel {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onModeSelected(String mode);
    }

    private Listener listener;
    private final JScrollPane scroller;

    public ModeSelectScreen() {
        super(new BorderLayout());
        ScrollPage page = new ScrollPage();
        page.setLayout(new BorderLayout());

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(new Hero());
        col.add(Box.createVerticalStrut(24));

        JPanel cards = new ResponsiveGrid(3, 300, 28, 28);
        cards.setOpaque(false);
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.add(new ModeCard(0, GameServer.MODE_COUNTRY, "Identify the country from its capital."));
        cards.add(new ModeCard(1, GameServer.MODE_CAPITAL, "Identify the capital of a country."));
        cards.add(new ModeCard(2, GameServer.MODE_FLAG, "Match a flag to its country."));
        col.add(cards);
        col.add(Box.createVerticalStrut(22));
        col.add(new Tip());

        page.add(new CenterColumn(col, 1296, 20, 22, 30), BorderLayout.CENTER);
        scroller = page.scroller();
        add(scroller, BorderLayout.CENTER);
    }

    public void setListener(Listener l) { this.listener = l; }

    private void choose(String mode) { if (listener != null) listener.onModeSelected(mode); }

    /** Gradient banner with kicker, title and globe. Text shrinks and the globe hides on narrow windows. */
    private static class Hero extends JComponent {
        private static final long serialVersionUID = 1L;
        Hero() { setAlignmentX(LEFT_ALIGNMENT); }
        @Override public Dimension getPreferredSize() { return new Dimension(100, 170); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 170); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            float sc = Math.max(0.6f, Math.min(1f, w / 760f));
            int pad = w < 520 ? 24 : 44;
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 60, 60);
            g.setPaint(new java.awt.GradientPaint(0, 0, Theme.HERO_A, w, h, Theme.HERO_B));
            g.fill(shape);
            g.setClip(shape);
            ScrollPage.glow(g, w - 120, h / 2.0, 260, new Color(0x7C, 0xF2, 0xE4, 55));
            g.setClip(null);
            g.setColor(Theme.alpha(Theme.TEAL, 120));
            g.setStroke(new java.awt.BasicStroke(1.4f));
            g.draw(shape);
            g.setFont(Theme.bold(Math.round(13 * Math.max(sc, 0.85f))));
            g.setColor(Theme.MINT);
            float x = pad;
            for (char ch : (w < 520 ? "NEXT ADVENTURE?" : "READY FOR YOUR NEXT ADVENTURE?").toCharArray()) {
                g.drawString(String.valueOf(ch), x, 46);
                x += g.getFontMetrics().charWidth(ch) + 2f;
            }
            g.setFont(Theme.bold(Math.round(38 * sc)));
            g.setColor(Color.WHITE);
            g.drawString("Choose your challenge", pad, 46 + Math.round(48 * sc));
            g.setFont(Theme.plain(Math.round(17 * Math.max(sc, 0.8f))));
            g.setColor(new Color(0xD8E4F8));
            String sub = w < 600 ? "Pick a mode and start exploring." : "Pick a mode and explore the world one question at a time.";
            g.drawString(sub, pad, 46 + Math.round(48 * sc) + 38);
            if (w >= 700) Art.globe(g, w - 130, h / 2.0, 70, Theme.MINT, Theme.LAND, Theme.AMBER);
            g.dispose();
        }
    }

    /** One of the three clickable mode cards (hover highlight, click anywhere or press the button). */
    private class ModeCard extends RoundedPanel {
        private static final long serialVersionUID = 1L;
        private final int kind;

        ModeCard(int kind, String mode, String desc) {
            super(24, Theme.CARD);
            this.kind = kind;
            stroke(Theme.BORDER).shadow(true);
            setLayout(new BorderLayout());
            padding(140, 28, 24, 28);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            JLabel t = new JLabel(mode);
            t.setFont(Theme.bold(26));
            t.setForeground(Theme.TEXT);
            JLabel d = new JLabel("<html>" + desc + "</html>");
            d.setFont(Theme.plain(16));
            d.setForeground(Theme.MUTED);
            text.add(t);
            text.add(Box.createVerticalStrut(8));
            text.add(d);
            text.add(Box.createVerticalStrut(18));

            JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            badges.setOpaque(false);
            badges.setAlignmentX(LEFT_ALIGNMENT);
            badges.add(new Pill(GameEngine.POINTS_PER_ANSWER + " pts"));
            JLabel diff = new JLabel("   " + GameEngine.MIN_OPTIONS + " options \u2022 Adaptive");
            diff.setFont(Theme.font(java.awt.Font.PLAIN, 14));
            diff.setForeground(Theme.SMALL);
            badges.add(diff);
            text.add(badges);
            t.setAlignmentX(LEFT_ALIGNMENT);
            d.setAlignmentX(LEFT_ALIGNMENT);
            add(text, BorderLayout.CENTER);

            RoundedButton start = new RoundedButton("Start mode  \u2192", RoundedButton.Style.PRIMARY);
            start.setPreferredSize(new Dimension(100, 52));
            start.addActionListener(e -> choose(mode));
            JPanel south = new JPanel(new BorderLayout());
            south.setOpaque(false);
            south.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            south.add(start, BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { stroke(Theme.TEAL).glow(Theme.TEAL); }
                @Override public void mouseExited(MouseEvent e) {
                    if (!contains(e.getPoint())) stroke(Theme.BORDER).glow(null);
                }
                @Override public void mouseClicked(MouseEvent e) { choose(mode); }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            Art.modeIcon(g, kind, getWidth() / 2.0, RoundedPanel.SHADOW + 76, 50);
            g.dispose();
        }
    }

    /** Small yellow pill, e.g. "10 pts". */
    private static class Pill extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String text;
        Pill(String text) { this.text = text; }
        @Override public Dimension getPreferredSize() {
            return new Dimension(getFontMetrics(Theme.bold(14)).stringWidth(text) + 32, 30);
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            g.setColor(Theme.GOLD_BG);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g.setFont(Theme.bold(14));
            g.setColor(Theme.GOLD_TEXT);
            g.drawString(text, 16, 20);
            g.dispose();
        }
    }

    /** "How scoring works" panel. The numbers come from the engine so they can never go out of date. */
    private static class Tip extends RoundedPanel {
        private static final long serialVersionUID = 1L;
        Tip() {
            super(22, Theme.TEAL_LIGHT, new BorderLayout(20, 0));
            stroke(Theme.alpha(Theme.TEAL, 70));
            setAlignmentX(LEFT_ALIGNMENT);
            setPreferredSize(new Dimension(100, 120));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            padding(0, 28, 0, 28);
            JComponent icon = new JComponent() {
                private static final long serialVersionUID = 1L;
                @Override public Dimension getPreferredSize() { return new Dimension(44, 44); }
                @Override protected void paintComponent(Graphics g0) {
                    Graphics2D g = Theme.quality((Graphics2D) g0.create());
                    g.setColor(Theme.TEAL);
                    g.fillOval(0, getHeight() / 2 - 22, 44, 44);
                    g.setColor(Theme.INK);
                    g.setFont(Theme.bold(22));
                    g.drawString("i", 19, getHeight() / 2 + 8);
                    g.dispose();
                }
            };
            add(icon, BorderLayout.WEST);
            JPanel text = new JPanel();
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setOpaque(false);
            JLabel head = new JLabel("How scoring works");
            head.setFont(Theme.bold(19));
            head.setForeground(Theme.TEXT);
            JLabel body = new JLabel("<html>Correct answers earn " + GameEngine.POINTS_PER_ANSWER + " points. Every "
                    + GameEngine.POINTS_PER_LEVEL + " points unlocks a new level with more answer options.</html>");
            body.setFont(Theme.plain(16));
            body.setForeground(Theme.MUTED);
            text.add(Box.createVerticalGlue());
            text.add(head);
            text.add(Box.createVerticalStrut(6));
            text.add(body);
            text.add(Box.createVerticalGlue());
            add(text, BorderLayout.CENTER);
        }
    }
}
