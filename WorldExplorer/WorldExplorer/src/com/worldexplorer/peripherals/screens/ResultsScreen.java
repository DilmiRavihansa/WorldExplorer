package com.worldexplorer.peripherals.screens;

import com.worldexplorer.engine.GameSummary;
import com.worldexplorer.engine.ScoreEntry;
import com.worldexplorer.peripherals.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/** Screen 07 - Results and leaderboard. Also used on its own for the "Leaderboard" menu item. */
public class ResultsScreen extends ScrollPage {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onPlayAgain();
        void onChangeMode();
        void onSignOut();
    }

    private Listener listener;
    private boolean showingFinal = false;
    private final Hero hero = new Hero();
    private final JPanel stats = new ResponsiveGrid(3, 250, 20, 18, 0.30, 0.30, 0.40);
    private final JLabel scoreValue = new JLabel("0");
    private final JLabel levelValue = new JLabel("1");
    private final JLabel accValue = new JLabel("0%");
    private final JLabel accCopy = new JLabel(" ");
    private final ProgressBar accBar = new ProgressBar(9);
    private final JPanel table = new JPanel();
    private final RoundedButton playAgain = new RoundedButton("Play again", RoundedButton.Style.PRIMARY);
    private final RoundedButton changeMode = new RoundedButton("Change mode", RoundedButton.Style.SECONDARY);
    private final RoundedButton signOut = new RoundedButton("Sign out", RoundedButton.Style.SECONDARY);

    public ResultsScreen() {
        setLayout(new BorderLayout());
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        hero.setAlignmentX(LEFT_ALIGNMENT);
        col.add(hero);
        col.add(Box.createVerticalStrut(24));
        buildStats();
        stats.setAlignmentX(LEFT_ALIGNMENT);
        col.add(stats);
        col.add(Box.createVerticalStrut(24));
        JPanel bottom = new ResponsiveGrid(2, 330, 24, 22, 0.64, 0.36);
        bottom.setAlignmentX(LEFT_ALIGNMENT);
        bottom.add(buildLeaderboardCard());
        bottom.add(buildActionsCard());
        col.add(bottom);
        add(new CenterColumn(col, 1296, 20, 22, 36), BorderLayout.CENTER);

        playAgain.addActionListener(e -> { if (listener != null) listener.onPlayAgain(); });
        changeMode.addActionListener(e -> { if (listener != null) listener.onChangeMode(); });
        signOut.addActionListener(e -> { if (listener != null) listener.onSignOut(); });
    }

    public void setListener(Listener l) { this.listener = l; }

    /** True when the last thing shown was a finished game (not just the leaderboard). */
    public boolean isShowingFinal() { return showingFinal; }

    public JComponent asPage() { return scroller(); }

    private void buildStats() {
        stats.setOpaque(false);

        RoundedPanel score = statCard(Theme.VIOLET, null).glow(Theme.VIOLET).gradient(new Color(0x6D28D9), new Color(0x0E7490));
        score.add(small("FINAL SCORE", Theme.MINT));
        score.add(Box.createVerticalStrut(6));
        big(scoreValue, Color.WHITE);
        score.add(scoreValue);
        score.add(small("XP points", new Color(0xD8E4F8)));
        stats.add(score);

        RoundedPanel level = statCard(Theme.CARD, Theme.BORDER);
        level.add(small("LEVEL REACHED", Theme.VIOLET));
        level.add(Box.createVerticalStrut(6));
        big(levelValue, Theme.TEXT);
        level.add(levelValue);
        level.add(small("Adaptive difficulty", Theme.MUTED));
        stats.add(level);

        RoundedPanel acc = statCard(Theme.CARD, Theme.BORDER);
        acc.add(small("PERFORMANCE", Theme.TEAL));
        acc.add(Box.createVerticalStrut(6));
        big(accValue, Theme.TEXT);
        acc.add(accValue);
        accBar.setAlignmentX(LEFT_ALIGNMENT);
        acc.add(accBar);
        acc.add(Box.createVerticalStrut(8));
        acc.add(small(" ", Theme.MUTED, accCopy));
        stats.add(acc);
    }

    private RoundedPanel statCard(Color fill, Color stroke) {
        RoundedPanel p = new RoundedPanel(24, fill);
        if (stroke != null) p.stroke(stroke);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.padding(24, 28, 24, 28);
        p.setPreferredSize(new Dimension(100, 176));
        return p;
    }

    private JLabel small(String text, Color color) { return small(text, color, new JLabel(text)); }

    private JLabel small(String text, Color color, JLabel l) {
        l.setText(text);
        l.setFont(Theme.bold(12));
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void big(JLabel l, Color color) {
        l.setFont(Theme.bold(54));
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
    }

    private RoundedPanel buildLeaderboardCard() {
        RoundedPanel card = new RoundedPanel(24, Theme.CARD).stroke(Theme.BORDER);
        card.setLayout(new BorderLayout(0, 14));
        card.padding(24, 22, 20, 22);
        card.setPreferredSize(new Dimension(500, 440));
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Leaderboard");
        t.setFont(Theme.bold(26));
        t.setForeground(Theme.TEXT);
        JLabel s = new JLabel("Top explorers by score");
        s.setFont(Theme.plain(16));
        s.setForeground(Theme.MUTED);
        head.add(t);
        head.add(s);
        card.add(head, BorderLayout.NORTH);
        table.setOpaque(false);
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        card.add(table, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel buildActionsCard() {
        RoundedPanel card = new RoundedPanel(24, Theme.TEAL_LIGHT).stroke(Theme.alpha(Theme.TEAL, 70));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.padding(28, 28, 24, 28);
        card.setPreferredSize(new Dimension(300, 440));
        JLabel t = new JLabel("What next?");
        t.setFont(Theme.bold(26));
        t.setForeground(Theme.TEXT);
        JLabel d = new JLabel("<html>Keep exploring or challenge yourself with a different mode.</html>");
        d.setFont(Theme.plain(16));
        d.setForeground(Theme.MUTED);
        for (JComponent c : new JComponent[] { t, d, playAgain, changeMode, signOut }) c.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(d);
        card.add(Box.createVerticalStrut(26));
        for (RoundedButton b : new RoundedButton[] { playAgain, changeMode, signOut }) {
            b.setPreferredSize(new Dimension(100, 54));
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            b.setAlignmentX(LEFT_ALIGNMENT);
            card.add(b);
            card.add(Box.createVerticalStrut(14));
        }
        return card;
    }

    /** After a game: show the result cards and highlight this score in the table. */
    public void showFinal(GameSummary s, java.util.List<ScoreEntry> top) {
        showingFinal = true;
        hero.kicker = "ADVENTURE COMPLETE";
        hero.title = "Great run, " + s.getPlayer() + "!";
        hero.subtitle = "Your score has been saved to the World Explorer leaderboard.";
        hero.repaint();
        scoreValue.setText(String.valueOf(s.getScore()));
        levelValue.setText(String.valueOf(s.getLevel()));
        accValue.setText(s.getAccuracy() + "%");
        accBar.setValue(s.getAccuracy() / 100.0);
        accCopy.setText(s.getCorrect() + " correct \u2022 " + s.getIncorrect() + " incorrect");
        stats.setVisible(true);
        playAgain.setText("Play again");
        changeMode.setVisible(true);
        fillTable(top, s.getPlayer(), s.getScore());
    }

    /** From the menu: only the leaderboard. */
    public void showLeaderboardOnly(String player, java.util.List<ScoreEntry> top) {
        showingFinal = false;
        hero.kicker = "HALL OF FAME";
        hero.title = "Leaderboard";
        hero.subtitle = "See how you compare with the best explorers.";
        hero.repaint();
        stats.setVisible(false);
        playAgain.setText("Play now");
        changeMode.setVisible(false);
        fillTable(top, player, -1);
    }

    private void fillTable(List<ScoreEntry> top, String me, int myScore) {
        table.removeAll();
        table.add(new Row(null, "RANK", "PLAYER", "POINTS", false, false));
        boolean marked = false;
        for (int i = 0; i < top.size(); i++) {
            ScoreEntry e = top.get(i);
            boolean mine = !marked && myScore >= 0 && e.getPlayer().equals(me) && e.getScore() == myScore;
            if (mine) marked = true;
            table.add(new Row(i, String.valueOf(i + 1), e.getPlayer(), String.valueOf(e.getScore()), mine, i % 2 == 0));
        }
        if (top.isEmpty()) {
            JLabel none = new JLabel("No scores yet - be the first!");
            none.setFont(Theme.plain(16));
            none.setForeground(Theme.MUTED);
            none.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 12, 0, 0));
            table.add(none);
        }
        table.revalidate();
        table.repaint();
    }

    /** One table row (header when rank index is null). */
    private static class Row extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Integer index;
        private final String rank, name, points;
        private final boolean mine, shaded;

        Row(Integer index, String rank, String name, String points, boolean mine, boolean shaded) {
            this.index = index;
            this.rank = rank;
            this.name = name;
            this.points = points;
            this.mine = mine;
            this.shaded = shaded;
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override public Dimension getPreferredSize() { return new Dimension(100, index == null ? 34 : 46); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, getPreferredSize().height); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            if (index == null) {
                g.setFont(Theme.bold(12));
                g.setColor(Theme.SMALL);
                g.drawString(rank, 14, 22);
                g.drawString(name, w < 420 ? 82 : 120, 22);
                g.drawString(points, w - 14 - g.getFontMetrics().stringWidth(points), 22);
                g.dispose();
                return;
            }
            if (mine) {
                g.setColor(Theme.GOLD_BG);
                g.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, 20, 20));
                g.setColor(Theme.alpha(Theme.AMBER, 140));
                g.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 3, 20, 20));
            } else if (shaded) {
                g.setColor(Theme.ROW_ALT);
                g.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, 20, 20));
            }
            int cy = (h - 2) / 2;
            if (index < 3) {
                Color[] medal = { new Color(0xFFB703), new Color(0xB8C4CC), new Color(0xD98E73) };
                g.setColor(medal[index]);
                g.fill(new Ellipse2D.Double(10, cy - 13, 26, 26));
                g.setColor(Theme.INK);
            } else {
                g.setColor(Theme.MUTED);
            }
            g.setFont(Theme.bold(15));
            g.drawString(rank, 23 - g.getFontMetrics().stringWidth(rank) / 2, cy + 5);
            int ax = w < 420 ? 62 : 100;
            Art.avatar(g, ax, cy - 15, 30, name);
            g.setFont(Theme.font(mine ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 16));
            g.setColor(mine ? Theme.GOLD_TEXT : Theme.TEXT);
            g.drawString(name + (mine ? "  (you)" : ""), ax + 42, cy + 5);
            g.setFont(Theme.bold(16));
            g.setColor(mine ? Theme.GOLD_TEXT : Theme.TEAL);
            g.drawString(points, w - 14 - g.getFontMetrics().stringWidth(points), cy + 5);
            g.dispose();
        }
    }

    /** Gradient header with kicker, title and subtitle. Adapts to narrow windows. */
    private static class Hero extends JComponent {
        private static final long serialVersionUID = 1L;
        String kicker = "ADVENTURE COMPLETE", title = "Great run!", subtitle = " ";
        Hero() { setAlignmentX(LEFT_ALIGNMENT); }
        @Override public Dimension getPreferredSize() { return new Dimension(100, 170); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 170); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            float sc = Math.max(0.6f, Math.min(1f, w / 780f));
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
            for (char ch : kicker.toCharArray()) {
                g.drawString(String.valueOf(ch), x, 48);
                x += g.getFontMetrics().charWidth(ch) + 2f;
            }
            g.setFont(Theme.bold(Math.round(40 * sc)));
            g.setColor(Color.WHITE);
            g.drawString(title, pad, 48 + Math.round(52 * sc));
            g.setFont(Theme.plain(Math.round(17 * Math.max(sc, 0.8f))));
            g.setColor(new Color(0xD8E4F8));
            String sub = subtitle;
            int max = w - pad * 2 - (w >= 700 ? 150 : 0);
            while (sub.length() > 4 && g.getFontMetrics().stringWidth(sub) > max) sub = sub.substring(0, sub.length() - 2) + "\u2026";
            g.drawString(sub, pad, 48 + Math.round(52 * sc) + 36);
            if (w >= 700) Art.globe(g, w - 120, h / 2.0, 62, Theme.MINT, Theme.LAND, Theme.AMBER);
            g.dispose();
        }
    }
}
