package com.worldexplorer.peripherals.screens;

import com.worldexplorer.engine.Game;
import com.worldexplorer.peripherals.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Screen 06 - Bonus round: count the fish (picture from the Fish API, buttons 0-9, keys 0-9 also work). */
public class BonusScreen extends ScrollPage {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onAnswer(int index);
    }

    private Listener listener;
    private final Hero hero = new Hero();
    private final FishImage fish = new FishImage();
    private final JLabel question = new JLabel("How many fish are in the picture?", SwingConstants.CENTER);
    private final JLabel hint = new JLabel("Choose a number from 0 to 9", SwingConstants.CENTER);
    private final JPanel row = new ResponsiveGrid(10, 50, 8, 10);
    private final List<AnswerButton> buttons = new ArrayList<>();
    private final JLabel note = new JLabel(" ", SwingConstants.CENTER);
    private boolean locked = true;

    public BonusScreen() {
        setLayout(new BorderLayout());
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        hero.setAlignmentX(LEFT_ALIGNMENT);
        col.add(hero);
        col.add(Box.createVerticalStrut(26));

        RoundedPanel card = new RoundedPanel(28, Theme.CARD).stroke(Theme.BORDER).glow(Theme.AMBER).shadow(true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.padding(30, 24, 26, 24);
        card.setAlignmentX(LEFT_ALIGNMENT);
        question.setFont(Theme.bold(24));
        question.setForeground(Theme.TEXT);
        hint.setFont(Theme.plain(17));
        hint.setForeground(Theme.MUTED);
        note.setFont(Theme.font(java.awt.Font.PLAIN, 13));
        note.setForeground(Theme.SMALL);
        row.setOpaque(false);
        for (int i = 0; i <= 9; i++) {
            final int idx = i;
            AnswerButton b = new AnswerButton(String.valueOf(i), String.valueOf(i), true);
            b.addActionListener(e -> answer(idx));
            buttons.add(b);
            row.add(b);
        }
        for (JComponent c : new JComponent[] { question, fish, hint, row, note }) {
            c.setAlignmentX(CENTER_ALIGNMENT);
            if (c == row) c.setMaximumSize(new Dimension(760, 400));
            card.add(c);
            card.add(Box.createVerticalStrut(c == fish ? 20 : c == question ? 20 : c == hint ? 18 : 16));
        }
        col.add(card);
        add(new CenterColumn(col, 1296, 20, 22, 30), BorderLayout.CENTER);
        bindKeys();
    }

    public void setListener(Listener l) { this.listener = l; }

    public JComponent asPage() { return scroller(); }

    private void bindKeys() {
        for (int i = 0; i <= 9; i++) {
            final int idx = i;
            AbstractAction a = new AbstractAction() {
                private static final long serialVersionUID = 1L;
                @Override public void actionPerformed(ActionEvent e) { answer(idx); }
            };
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(String.valueOf(i)), "fish" + i);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + i), "fish" + i);
            getActionMap().put("fish" + i, a);
        }
    }

    private void answer(int idx) {
        if (!locked && listener != null) listener.onAnswer(idx);
    }

    public void showBonus(Game g, int bonusPoints, String progressText) {
        hero.points = "+" + bonusPoints;
        hero.progress = progressText;
        hero.repaint();
        question.setText("<html><div style='text-align:center'>" + g.getQuestion() + "</div></html>");
        fish.setImage(g.getImage());
        hint.setText("Choose a number from 0 to 9");
        hint.setFont(Theme.plain(17));
        hint.setForeground(Theme.MUTED);
        note.setText("<html><div style='text-align:center'>Correct answer: +" + bonusPoints + " points \u2022 Bonus rounds do not change your question count</div></html>");
        for (AnswerButton b : buttons) b.setState(AnswerButton.State.NORMAL);
        locked = false;
    }

    public void showFeedback(int chosen, int correct, boolean ok, String message) {
        locked = true;
        for (int i = 0; i < buttons.size(); i++) {
            AnswerButton b = buttons.get(i);
            b.setState(i == correct ? AnswerButton.State.CORRECT : i == chosen ? AnswerButton.State.WRONG
                    : AnswerButton.State.DIMMED);
        }
        hint.setText(message);
        hint.setFont(Theme.bold(17));
        hint.setForeground(ok ? Theme.GREEN : Theme.RED);
    }

    /** Gradient banner with the +20 badge. Adapts to narrow windows. */
    private static class Hero extends JComponent {
        private static final long serialVersionUID = 1L;
        String points = "+20", progress = "5 / 5 questions";
        @Override public Dimension getPreferredSize() { return new Dimension(100, 150); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 150); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            float sc = Math.max(0.6f, Math.min(1f, w / 820f));
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 56, 56);
            g.setPaint(new java.awt.GradientPaint(0, 0, new Color(0x4A1D6B), w, h, new Color(0x0B5E86)));
            g.fill(shape);
            g.setClip(shape);
            ScrollPage.glow(g, w - 140, h / 2.0, 240, new Color(0xFF, 0xC2, 0x33, 50));
            g.setClip(null);
            g.setColor(Theme.alpha(Theme.AMBER, 130));
            g.setStroke(new java.awt.BasicStroke(1.4f));
            g.draw(shape);
            int pad = w < 520 ? 20 : 40, d = Math.round(88 * Math.max(sc, 0.8f));
            ScrollPage.glow(g, pad + d / 2.0, h / 2.0, d, Theme.alpha(Theme.AMBER, 90));
            g.setColor(Theme.AMBER);
            g.fill(new Ellipse2D.Double(pad, h / 2.0 - d / 2.0, d, d));
            g.setColor(Theme.INK);
            g.setFont(Theme.bold(Math.round(28 * Math.max(sc, 0.8f))));
            g.drawString(points, pad + d / 2 - g.getFontMetrics().stringWidth(points) / 2, h / 2 + 10);
            int tx = pad + d + 24;
            g.setColor(Color.WHITE);
            g.setFont(Theme.bold(Math.round(36 * sc)));
            g.drawString("Bonus Round", tx, h / 2 - (w < 700 ? -8 : 4));
            if (w >= 700) {
                g.setColor(new Color(0xD8E4F8));
                g.setFont(Theme.plain(Math.round(17 * Math.max(sc, 0.85f))));
                g.drawString("A special challenge from the Fish API. Count carefully!", tx, h / 2 + 30);
                g.setFont(Theme.bold(15));
                int pw = g.getFontMetrics().stringWidth(progress) + 40;
                if (tx + 420 < w - pw - 40) {
                    g.setColor(new Color(255, 255, 255, 40));
                    g.fillRoundRect(w - pw - 44, h / 2 - 20, pw, 40, 40, 40);
                    g.setColor(Color.WHITE);
                    g.drawString(progress, w - pw - 44 + 20, h / 2 + 5);
                }
            }
            g.dispose();
        }
    }

    /** Frame that shows the fish picture. */
    private static class FishImage extends JComponent {
        private static final long serialVersionUID = 1L;
        private BufferedImage image;
        void setImage(BufferedImage i) { image = i; repaint(); }
        @Override public Dimension getPreferredSize() { return new Dimension(720, 250); }
        @Override public Dimension getMinimumSize() { return new Dimension(200, 180); }
        @Override public Dimension getMaximumSize() { return new Dimension(720, 250); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            g.setPaint(new java.awt.GradientPaint(0, 0, new Color(0x0D2A4A), 0, h, new Color(0x08182E)));
            g.fillRoundRect(0, 0, w - 1, h - 1, 44, 44);
            g.setColor(Theme.alpha(Theme.TEAL, 110));
            g.setStroke(new java.awt.BasicStroke(1.4f));
            g.drawRoundRect(0, 0, w - 1, h - 1, 44, 44);
            if (image != null) {
                double s = Math.min((w - 24.0) / image.getWidth(), (h - 24.0) / image.getHeight());
                int iw = (int) (image.getWidth() * s), ih = (int) (image.getHeight() * s);
                g.drawImage(image, (w - iw) / 2, (h - ih) / 2, iw, ih, null);
            }
            g.dispose();
        }
    }
}
