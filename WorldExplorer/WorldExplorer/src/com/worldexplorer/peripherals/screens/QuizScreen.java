package com.worldexplorer.peripherals.screens;

import com.worldexplorer.engine.Game;
import com.worldexplorer.peripherals.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Screens 03-05 - one quiz screen for Guess the Country / Guess the Capital / Identify the Flag.
 * It shows what it is told to show and reports clicks, key presses and "End game" via Listener.
 */
public class QuizScreen extends ScrollPage {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onAnswer(int index);
        void onEndGame();
    }

    private Listener listener;
    private final JLabel title = new JLabel("Guess the Country");
    private final Badge scoreBadge = new Badge("SCORE", Theme.CARD, Theme.BORDER).accent(Theme.TEAL);
    private final Badge levelBadge = new Badge("LEVEL", Theme.CARD, Theme.BORDER).accent(Theme.VIOLET);
    private final Badge questionBadge = new Badge("QUESTION", Theme.CARD, Theme.BORDER).accent(Theme.AMBER);
    private final JLabel progressText = new JLabel("QUESTION 1 OF 15");
    private final JLabel timeText = new JLabel(" ");
    private final ProgressBar progress = new ProgressBar(10);
    private final Illustration illustration = new Illustration();
    private final JTextArea questionText = new JTextArea("Loading question...");
    private final JLabel hint = new JLabel("Select the best answer below.");
    private final JPanel answers = new ResponsiveGrid(2, 330, 20, 14);
    private final List<AnswerButton> buttons = new ArrayList<>();
    private final JLabel keyHint = new JLabel("Tip: use number keys 1\u20136 to answer");
    private boolean locked = true;

    public QuizScreen() {
        setLayout(new BorderLayout());
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(buildHeader());
        col.add(Box.createVerticalStrut(22));
        col.add(buildCard());
        add(new CenterColumn(col, 1296, 20, 22, 30), BorderLayout.CENTER);
        bindKeys();
    }

    public void setListener(Listener l) { this.listener = l; }

    /** Scroll pane for the frame. */
    public JComponent asPage() { return scroller(); }

    private JPanel buildHeader() {
        JPanel p = new JPanel(null) {
            private static final long serialVersionUID = 1L;
            @Override public Dimension getPreferredSize() {
                Component l = getComponent(0), r = getComponent(1);
                int w = getWidth() > 0 ? getWidth() : 1000;
                boolean stack = w < l.getPreferredSize().width + r.getPreferredSize().width + 20;
                return new Dimension(w, stack ? l.getPreferredSize().height + r.getPreferredSize().height + 6
                        : Math.max(l.getPreferredSize().height, r.getPreferredSize().height));
            }
            @Override public void doLayout() {
                Component l = getComponent(0), r = getComponent(1);
                int w = getWidth();
                Dimension ld = l.getPreferredSize(), rd = r.getPreferredSize();
                if (w < ld.width + rd.width + 20) {
                    l.setBounds(0, 0, w, ld.height);
                    r.setBounds(0, ld.height + 6, w, rd.height);
                    ((FlowLayout) ((JPanel) r).getLayout()).setAlignment(FlowLayout.LEFT);
                } else {
                    int h = Math.max(ld.height, rd.height);
                    l.setBounds(0, (h - ld.height) / 2, ld.width, ld.height);
                    r.setBounds(w - rd.width, (h - rd.height) / 2, rd.width, rd.height);
                    ((FlowLayout) ((JPanel) r).getLayout()).setAlignment(FlowLayout.RIGHT);
                }
                if (lastStack != (w < ld.width + rd.width + 20)) { lastStack = !lastStack; javax.swing.SwingUtilities.invokeLater(this::revalidate); }
            }
            boolean lastStack = false;
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(BorderFactory.createEmptyBorder(0, RoundedPanel.SHADOW, 0, RoundedPanel.SHADOW));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel crumb = new JLabel("WORLD EXPLORER / PLAY");
        crumb.setFont(Theme.bold(12));
        crumb.setForeground(Theme.TEAL);
        title.setFont(Theme.bold(28));
        title.setForeground(Theme.TEXT);
        left.add(crumb);
        left.add(Box.createVerticalStrut(4));
        left.add(title);
        p.add(left);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        right.setOpaque(false);
        right.add(scoreBadge);
        right.add(levelBadge);
        right.add(questionBadge);
        p.add(right);
        return p;
    }

    private RoundedPanel buildCard() {
        RoundedPanel card = new RoundedPanel(28, Theme.CARD, new BorderLayout(0, 22)).stroke(Theme.BORDER).glow(Theme.VIOLET).shadow(true);
        card.padding(26, 30, 22, 30);
        card.setAlignmentX(LEFT_ALIGNMENT);

        // progress row
        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        JPanel labels = new JPanel(new BorderLayout());
        labels.setOpaque(false);
        progressText.setFont(Theme.bold(13));
        progressText.setForeground(Theme.SMALL);
        timeText.setFont(Theme.bold(14));
        timeText.setForeground(Theme.TEAL);
        labels.add(progressText, BorderLayout.WEST);
        labels.add(timeText, BorderLayout.EAST);
        top.add(labels, BorderLayout.NORTH);
        top.add(progress, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        // body: illustration | question, then answers
        JPanel body = new JPanel(new BorderLayout(0, 26));
        body.setOpaque(false);
        JPanel upper = new ResponsiveGrid(2, 460, 32, 18, 0.38, 0.62);
        upper.add(illustration);
        upper.add(buildQuestionBlock());
        body.add(upper, BorderLayout.NORTH);

        answers.setOpaque(false);
        body.add(answers, BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);

        // footer
        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        RoundedButton end = new RoundedButton("End game", RoundedButton.Style.SECONDARY);
        end.setPreferredSize(new Dimension(130, 46));
        end.addActionListener(e -> { if (listener != null) listener.onEndGame(); });
        keyHint.setFont(Theme.font(java.awt.Font.PLAIN, 14));
        keyHint.setForeground(Theme.SMALL);
        foot.add(end, BorderLayout.WEST);
        foot.add(keyHint, BorderLayout.CENTER);
        keyHint.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        card.add(foot, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildQuestionBlock() {
        JPanel q = new JPanel(new BorderLayout(0, 12));
        q.setOpaque(false);
        JLabel l = new JLabel("YOUR QUESTION");
        l.setFont(Theme.bold(13));
        l.setForeground(Theme.TEAL);
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        questionText.setFont(Theme.bold(30));
        questionText.setForeground(Theme.TEXT);
        questionText.setOpaque(false);
        questionText.setEditable(false);
        questionText.setFocusable(false);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setBorder(null);
        hint.setFont(Theme.plain(17));
        hint.setForeground(Theme.MUTED);
        q.add(l, BorderLayout.NORTH);
        q.add(questionText, BorderLayout.CENTER);   // takes the free space, so wrapping always has room
        q.add(hint, BorderLayout.SOUTH);            // question hint / feedback message
        return q;
    }

    /** 1..6 (and the numeric keypad) answer the question, like the tip in the design says. */
    private void bindKeys() {
        for (int i = 1; i <= 6; i++) {
            final int idx = i - 1;
            AbstractAction a = new AbstractAction() {
                private static final long serialVersionUID = 1L;
                @Override public void actionPerformed(ActionEvent e) { answer(idx); }
            };
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(String.valueOf(i)), "answer" + i);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + i), "answer" + i);
            getActionMap().put("answer" + i, a);
        }
    }

    private void answer(int index) {
        if (locked || index >= buttons.size() || listener == null) return;
        listener.onAnswer(index);
    }

    // ---- what the controller can ask the screen to do ----

    public void showLoading(String modeTitle) {
        title.setText(modeTitle);
        locked = true;
        questionText.setText("Loading question...");
        hint.setText("Contacting the World Explorer services...");
        hint.setFont(Theme.plain(17));
        hint.setForeground(Theme.MUTED);
        timeText.setText(" ");
    }

    public void showQuestion(String modeTitle, Game g, int score, int level, int questionNo, int total) {
        title.setText(modeTitle);
        scoreBadge.setValue(String.valueOf(score));
        levelBadge.setValue(String.valueOf(level));
        questionBadge.setValue(String.valueOf(questionNo));
        progressText.setText("QUESTION " + questionNo + " OF " + total);
        progress.setValue((double) questionNo / total);
        illustration.setImage(g.getImage());
        questionText.setText(g.getQuestion());
        hint.setText("Select the best answer below.");
        hint.setFont(Theme.plain(17));
        hint.setForeground(Theme.MUTED);
        answers.removeAll();
        buttons.clear();
        for (int i = 0; i < g.getOptions().size(); i++) {
            final int idx = i;
            AnswerButton b = new AnswerButton(String.valueOf(i + 1), g.getOptions().get(i), false);
            b.addActionListener(e -> answer(idx));
            buttons.add(b);
            answers.add(b);
        }
        keyHint.setText("Tip: use number keys 1\u2013" + g.getOptions().size() + " to answer");
        answers.revalidate();
        answers.repaint();
        locked = false;
    }

    /** Colours the buttons and shows the message after an answer. */
    public void showFeedback(int chosen, int correct, boolean ok, String message) {
        locked = true;
        for (int i = 0; i < buttons.size(); i++) {
            AnswerButton b = buttons.get(i);
            if (i == correct) b.setState(AnswerButton.State.CORRECT);
            else if (i == chosen) b.setState(AnswerButton.State.WRONG);
            else b.setState(AnswerButton.State.DIMMED);
        }
        hint.setText(message);
        hint.setForeground(ok ? Theme.GREEN : Theme.RED);
        hint.setFont(Theme.bold(17));
    }

    public void updateStats(int score, int level) {
        scoreBadge.setValue(String.valueOf(score));
        levelBadge.setValue(String.valueOf(level));
    }

    /** seconds < 0 hides the timer. */
    public void setSecondsLeft(int seconds) {
        if (seconds < 0) { timeText.setText(" "); return; }
        timeText.setText(seconds + " sec");
        timeText.setForeground(seconds <= 5 ? Theme.RED : Theme.TEAL);
    }

    /** Left panel: the flag image, or the globe illustration when the question has no picture. */
    private static class Illustration extends JComponent {
        private static final long serialVersionUID = 1L;
        private BufferedImage image;

        void setImage(BufferedImage img) { this.image = img; repaint(); }

        @Override public Dimension getPreferredSize() { return new Dimension(430, 240); }
        @Override public Dimension getMinimumSize() { return new Dimension(200, 200); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            int w = getWidth(), h = getHeight();
            g.setPaint(new java.awt.GradientPaint(0, 0, new Color(0x1B1650), w, h, new Color(0x0C3550)));
            g.fillRoundRect(0, 0, w - 1, h - 1, 40, 40);
            g.setColor(Theme.alpha(Theme.TEAL, 90));
            g.setStroke(new java.awt.BasicStroke(1.4f));
            g.drawRoundRect(0, 0, w - 1, h - 1, 40, 40);
            if (image != null) {
                double s = Math.min((w - 60.0) / image.getWidth(), (h - 70.0) / image.getHeight());
                int iw = (int) (image.getWidth() * s), ih = (int) (image.getHeight() * s);
                int x = (w - iw) / 2, y = (h - 30 - ih) / 2 + 8;
                g.setColor(new Color(0, 0, 0, 90));
                g.fillRoundRect(x + 2, y + 5, iw, ih, 10, 10);
                g.drawImage(image, x, y, iw, ih, null);
                g.setColor(Theme.alpha(Color.WHITE, 90));
                g.drawRect(x, y, iw - 1, ih - 1);
            } else {
                Art.globe(g, w / 2.0, h / 2.0 - 12, Math.min(84, h * 0.33), Theme.MINT, Theme.LAND, Theme.AMBER);
            }
            g.setFont(Theme.bold(13));
            g.setColor(Theme.TEAL);
            String cap = image != null ? "Country flag" : "World Explorer clue";
            g.drawString(cap, (w - g.getFontMetrics().stringWidth(cap)) / 2, h - 18);
            g.dispose();
        }
    }
}
