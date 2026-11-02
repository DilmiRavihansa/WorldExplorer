package com.worldexplorer.peripherals.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.worldexplorer.engine.GameEngine;
import com.worldexplorer.peripherals.ui.Art;
import com.worldexplorer.peripherals.ui.CenterColumn;
import com.worldexplorer.peripherals.ui.ResponsiveGrid;
import com.worldexplorer.peripherals.ui.RoundedButton;
import com.worldexplorer.peripherals.ui.RoundedPanel;
import com.worldexplorer.peripherals.ui.ScrollPage;
import com.worldexplorer.peripherals.ui.Theme;

/**
 * Screen 08 - How to Play. Replaces the old JOptionPane help dialog.
 * Pure view: it only shows the rules (numbers come from GameEngine, so text and rules cannot disagree)
 * and reports the "Choose a mode" button through Listener.
 */
public class HowtoPlay extends ScrollPage {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onChooseMode();
    }

    private Listener listener;
    private JScrollPane pane;

    public HowtoPlay() {
        setLayout(new BorderLayout());
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(new Hero());
        col.add(Box.createVerticalStrut(24));
        col.add(buildSteps());
        col.add(Box.createVerticalStrut(24));
        col.add(buildLevels());
        col.add(Box.createVerticalStrut(24));
        col.add(buildCallToAction());

        add(new CenterColumn(col, 1296, 20, 22, 36), BorderLayout.CENTER);
    }

    public void setListener(Listener l) { this.listener = l; }

    /** Scroll pane for the frame (created once, so scrollToTop works on the same pane). */
    public JScrollPane asPage() {
        if (pane == null) pane = scroller();
        return pane;
    }

    /** Called every time the screen is opened, so the player always starts at the hero banner. */
    public void scrollToTop() {
        SwingUtilities.invokeLater(() -> asPage().getVerticalScrollBar().setValue(0));
    }

    // ------------------------------------------------------------------ sections

    private JPanel buildSteps() {
        JPanel grid = new ResponsiveGrid(3, 300, 24, 24);
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.add(new StepCard(1, Theme.TEAL, "Pick a mode",
                "Choose Guess the Country, Guess the Capital or Identify the Flag."));
        grid.add(new StepCard(2, Theme.VIOLET, "Beat the timer",
                "Click an answer or press its number key before the timer reaches zero."));
        grid.add(new StepCard(3, Theme.AMBER, "Earn points",
                "Every correct answer earns " + GameEngine.POINTS_PER_ANSWER + " points. Every "
                        + GameEngine.POINTS_PER_LEVEL + " points unlocks a new level."));
        grid.add(new StepCard(4, Theme.MINT, "Fish bonus round",
                "After every few questions, count the fish in the picture for extra points."));
        grid.add(new StepCard(5, Theme.TEAL, "Reach the leaderboard",
                "Your final score is saved so you can compare it with the best explorers."));
        grid.add(new TipCard());
        return grid;
    }

    /** "How levels work": the numbers are computed from the engine rules. */
    private JPanel buildLevels() {
        RoundedPanel panel = new RoundedPanel(24, Theme.CARD).stroke(Theme.BORDER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.padding(24, 26, 26, 26);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = label("How levels work", Theme.bold(24), Theme.TEXT);
        JLabel sub = label("More points, more answer options.", Theme.plain(16), Theme.MUTED);
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(18));

        JPanel chips = new ResponsiveGrid(3, 220, 18, 16);
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);
        int levels = GameEngine.MAX_OPTIONS - GameEngine.MIN_OPTIONS + 1;
        Color[] accents = { Theme.TEAL, Theme.VIOLET, Theme.AMBER };
        for (int lv = 1; lv <= levels; lv++) {
            int from = (lv - 1) * GameEngine.POINTS_PER_LEVEL;
            String range = lv == levels ? from + "+ points" : from + "\u2013" + (from + GameEngine.POINTS_PER_LEVEL - 1) + " points";
            chips.add(new LevelChip(lv, GameEngine.optionsForLevel(lv), range, accents[(lv - 1) % accents.length]));
        }
        panel.add(chips);
        return panel;
    }

    private JPanel buildCallToAction() {
        RoundedPanel bar = new RoundedPanel(22, Theme.TEAL_LIGHT, new BorderLayout());
        bar.stroke(Theme.alpha(Theme.TEAL, 70));
        bar.setAlignmentX(LEFT_ALIGNMENT);
        bar.padding(24, 28, 24, 28);

        // two columns on wide windows, stacked on narrow ones (same ResponsiveGrid as the other screens)
        JPanel row = new ResponsiveGrid(2, 330, 20, 16, 0.65, 0.35);
        row.setOpaque(false);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setPreferredSize(new Dimension(100, 56));
        text.add(Box.createVerticalGlue());
        text.add(label("Ready to explore?", Theme.bold(22), Theme.TEXT));
        text.add(Box.createVerticalStrut(4));
        text.add(label("Pick a mode and start your first round.", Theme.plain(16), Theme.MUTED));
        text.add(Box.createVerticalGlue());
        row.add(text);

        RoundedButton go = new RoundedButton("Choose a mode  \u2192", RoundedButton.Style.PRIMARY);
        go.addActionListener(e -> { if (listener != null) listener.onChooseMode(); });
        JPanel east = new JPanel(new BorderLayout());
        east.setOpaque(false);
        east.setPreferredSize(new Dimension(100, 56));
        go.setPreferredSize(new Dimension(100, 54));
        east.add(go, BorderLayout.CENTER);
        row.add(east);

        bar.add(row, BorderLayout.CENTER);
        return bar;
    }

    private static JLabel label(String s, java.awt.Font font, Color color) {
        JLabel l = new JLabel(s);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JTextArea wrapped(String s, java.awt.Font font, Color color) {
        JTextArea t = new JTextArea(s);
        t.setFont(font);
        t.setForeground(color);
        t.setOpaque(false);
        t.setEditable(false);
        t.setFocusable(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setBorder(null);
        return t;
    }

    // ------------------------------------------------------------------ small components

    /** One numbered rule card: coloured number badge, title and a short explanation. */
    private static class StepCard extends RoundedPanel {
        private static final long serialVersionUID = 1L;

        StepCard(int number, Color accent, String title, String body) {
            super(24, Theme.CARD, new BorderLayout(0, 10));
            stroke(Theme.BORDER);
            padding(24, 26, 22, 26);
            setPreferredSize(new Dimension(100, 214));

            JPanel head = new JPanel(new BorderLayout(0, 14));
            head.setOpaque(false);
            head.add(new NumberBadge(number, accent), BorderLayout.NORTH);
            JLabel t = new JLabel(title);
            t.setFont(Theme.bold(22));
            t.setForeground(Theme.TEXT);
            head.add(t, BorderLayout.CENTER);
            add(head, BorderLayout.NORTH);
            add(wrapped(body, Theme.plain(16), Theme.MUTED), BorderLayout.CENTER);
        }
    }

    /** Sixth card in the grid: keyboard shortcuts. */
    private static class TipCard extends RoundedPanel {
        private static final long serialVersionUID = 1L;

        TipCard() {
            super(24, Theme.TEAL_LIGHT, new BorderLayout(0, 10));
            stroke(Theme.alpha(Theme.TEAL, 70));
            padding(24, 26, 22, 26);
            setPreferredSize(new Dimension(100, 214));

            JPanel head = new JPanel(new BorderLayout(0, 14));
            head.setOpaque(false);
            head.add(new InfoBadge(), BorderLayout.NORTH);
            JLabel t = new JLabel("Keyboard shortcuts");
            t.setFont(Theme.bold(22));
            t.setForeground(Theme.TEXT);
            head.add(t, BorderLayout.CENTER);
            add(head, BorderLayout.NORTH);
            add(wrapped("Press 1\u2013" + GameEngine.MAX_OPTIONS + " to answer a quiz question and 0\u20139 in the fish round. "
                    + "Press Enter to sign in.", Theme.plain(16), Theme.MUTED), BorderLayout.CENTER);
        }
    }

    /** Level tile: "LEVEL 2 / 5 options / 50-99 points". */
    private static class LevelChip extends RoundedPanel {
        private static final long serialVersionUID = 1L;

        LevelChip(int level, int options, String range, Color accent) {
            super(20, Theme.CARD_HI, new BorderLayout());
            stroke(Theme.alpha(accent, 110));
            padding(16, 22, 16, 22);
            setPreferredSize(new Dimension(100, 118));
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.add(label("LEVEL " + level, Theme.bold(12), accent));
            p.add(Box.createVerticalStrut(6));
            p.add(label(options + " options", Theme.bold(26), Theme.TEXT));
            p.add(Box.createVerticalStrut(4));
            p.add(label(range, Theme.plain(15), Theme.MUTED));
            add(p, BorderLayout.CENTER);
        }
    }

    private static class NumberBadge extends JComponent {
        private static final long serialVersionUID = 1L;
        private final int number;
        private final Color accent;
        NumberBadge(int number, Color accent) { this.number = number; this.accent = accent; }
        @Override public Dimension getPreferredSize() { return new Dimension(48, 48); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            ScrollPage.glow(g, 24, 24, 30, Theme.alpha(accent, 70));
            g.setColor(accent);
            g.fillOval(0, 0, 47, 47);
            g.setColor(Theme.INK);
            g.setFont(Theme.bold(22));
            String s = String.valueOf(number);
            g.drawString(s, 24 - g.getFontMetrics().stringWidth(s) / 2, 32);
            g.dispose();
        }
    }

    private static class InfoBadge extends JComponent {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredSize() { return new Dimension(48, 48); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            g.setColor(Theme.TEAL);
            g.fillRoundRect(0, 0, 47, 47, 16, 16);
            g.setColor(Theme.INK);
            g.setFont(Theme.bold(22));
            String s = "\u2328";   // keyboard glyph; falls back to "i" if the font has no glyph
            if (!g.getFont().canDisplay('\u2328')) s = "i";
            g.drawString(s, 24 - g.getFontMetrics().stringWidth(s) / 2, 32);
            g.dispose();
        }
    }

    /** Gradient banner with kicker, title and globe (same look as the mode-select banner). */
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
            g.setPaint(new GradientPaint(0, 0, Theme.HERO_A, w, h, Theme.HERO_B));
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
            for (char ch : "LEARN THE RULES".toCharArray()) {
                g.drawString(String.valueOf(ch), x, 46);
                x += g.getFontMetrics().charWidth(ch) + 2f;
            }
            g.setFont(Theme.bold(Math.round(38 * sc)));
            g.setColor(Color.WHITE);
            g.drawString("How to Play", pad, 46 + Math.round(48 * sc));
            g.setFont(Theme.plain(Math.round(17 * Math.max(sc, 0.8f))));
            g.setColor(new Color(0xD8E4F8));
            String sub = w < 600 ? "Five quick steps to get started." : "Five quick steps to start your journey around the world.";
            g.drawString(sub, pad, 46 + Math.round(48 * sc) + 38);
            if (w >= 700) Art.globe(g, w - 130, h / 2.0, 70, Theme.MINT, Theme.LAND, Theme.AMBER);
            g.dispose();
        }
    }
}