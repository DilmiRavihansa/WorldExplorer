package com.worldexplorer.peripherals.screens;

import com.worldexplorer.peripherals.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/** Screen 01 - Login. Only collects input and reports it through Listener; it never checks passwords itself. */
public class LoginScreen extends ScrollPage {
    private static final long serialVersionUID = 1L;

    public interface Listener {
        void onLogin(String username, char[] password, boolean remember);
        void onForgotPassword();
    }

    private final RoundedPanel card = new RoundedPanel(28, Theme.CARD).stroke(Theme.BORDER).glow(Theme.TEAL).shadow(true);
    private final InputField user = InputField.text("Enter your username");
    private final InputField pass = InputField.password("Enter your password");
    private final FlatCheckBox remember = new FlatCheckBox("Remember me");
    private final JLabel error = new JLabel(" ");
    private final RoundedButton signIn = new RoundedButton("Sign in", RoundedButton.Style.PRIMARY);
    private Listener listener;
    private int padNow = 48;

    public LoginScreen() {
        setLayout(null);
        buildCard();
        add(card);
    }

    public void setListener(Listener l) { this.listener = l; }

    private void buildCard() {
        card.setLayout(new GridBagLayout());
        card.padding(34, 48, 26, 48);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Welcome back");
        title.setFont(Theme.bold(28));
        title.setForeground(Theme.TEXT);
        c.gridy = 0; c.insets = new Insets(0, 0, 4, 0);
        card.add(title, c);

        JLabel sub = new JLabel("Sign in to continue your quest.");
        sub.setFont(Theme.plain(17));
        sub.setForeground(Theme.MUTED);
        c.gridy = 1; c.insets = new Insets(0, 0, 22, 0);
        card.add(sub, c);

        c.gridy = 2; c.insets = new Insets(0, 0, 6, 0);
        card.add(label("USERNAME"), c);
        c.gridy = 3; c.insets = new Insets(0, 0, 16, 0);
        card.add(user, c);
        c.gridy = 4; c.insets = new Insets(0, 0, 6, 0);
        card.add(label("PASSWORD"), c);
        c.gridy = 5; c.insets = new Insets(0, 0, 14, 0);
        card.add(pass, c);

        JPanel row = new JPanel(new java.awt.BorderLayout());
        row.setOpaque(false);
        row.add(remember, java.awt.BorderLayout.WEST);
        JButton forgot = new JButton("Forgot password?");
        forgot.setFont(Theme.bold(14));
        forgot.setForeground(Theme.TEAL);
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setFocusPainted(false);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addActionListener(e -> { if (listener != null) listener.onForgotPassword(); });
        row.add(forgot, java.awt.BorderLayout.EAST);
        c.gridy = 6; c.insets = new Insets(0, 0, 6, 0);
        card.add(row, c);

        error.setFont(Theme.bold(14));
        error.setForeground(Theme.RED);
        c.gridy = 7; c.insets = new Insets(0, 0, 6, 0);
        card.add(error, c);

        signIn.setPreferredSize(new Dimension(100, 54));
        c.gridy = 8; c.insets = new Insets(0, 0, 14, 0);
        card.add(signIn, c);

        JLabel hint = new JLabel("Demo account: player1 / pass1", SwingConstants.CENTER);
        hint.setFont(Theme.font(java.awt.Font.PLAIN, 14));
        hint.setForeground(Theme.SMALL);
        c.gridy = 9; c.insets = new Insets(0, 0, 0, 0);
        card.add(hint, c);

        // Events: button click and Enter key in either field all do the same thing
        signIn.addActionListener(e -> submit());
        user.addActionListener(e -> pass.focusField());
        pass.addActionListener(e -> submit());
        getInputMap(WHEN_IN_FOCUSED_WINDOW);
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(Theme.bold(13));
        l.setForeground(Theme.TEAL);
        return l;
    }

    private void submit() {
        if (listener == null) return;
        showError(" ");
        listener.onLogin(user.getText(), pass.getPassword(), remember.isSelected());
    }

    public void showError(String message) {
        error.setText(message == null || message.isEmpty() ? " " : message);
    }

    /** Clears the password (always after a try) and puts the cursor back in the right field. */
    public void clearPassword() { pass.clear(); pass.focusField(); }

    public void reset() {
        user.clear();
        pass.clear();
        showError(" ");
        remember.setSelected(false);
        user.focusField();
    }

    public void focusUser() { user.focusField(); }

    public void setBusy(boolean busy) {
        signIn.setEnabled(!busy);
        signIn.setText(busy ? "Signing in..." : "Sign in");
    }

    private int heroHeight() { return Math.max(330, Math.min(420, Math.round(getHeight() * 0.38f))); }

    /** Hero text scale: shrinks on narrow windows. */
    private float scale() { return Math.max(0.62f, Math.min(1f, getWidth() / 980f)); }

    @Override public Dimension getPreferredSize() {
        int h = card.getPreferredSize().height;
        return new Dimension(420, 330 - 80 + h + 64);
    }

    @Override
    public void doLayout() {
        int w = Math.min(560, getWidth() - 32);
        int pad = w < 460 ? 20 : 48;
        if (pad != padNow) { padNow = pad; card.padding(30, pad, 24, pad); }
        int h = card.getPreferredSize().height;
        int top = Math.max(20, heroHeight() - 80);
        card.setBounds((getWidth() - w) / 2 - 0, top, w, h);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);          // dark space background
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        int w = getWidth(), h = getHeight(), hh = heroHeight();
        float sc = scale();
        int left = Math.max(24, Math.round(w * 0.08f));

        g.setPaint(new GradientPaint(0, 0, Theme.HERO_A, w, hh, Theme.HERO_B));
        g.fillRect(0, 0, w, hh);
        g.setClip(0, 0, w, hh);
        ScrollPage.glow(g, w - 200, hh * 0.45, 320, new Color(0x7C, 0xF2, 0xE4, 60));
        g.setColor(new Color(255, 255, 255, 10));
        g.fill(new Ellipse2D.Double(left - 110, 90 - 110, 220, 220));
        g.setClip(null);
        g.setPaint(new GradientPaint(0, hh - 3, Theme.VIOLET, w, hh - 3, Theme.TEAL));
        g.fillRect(0, hh - 3, w, 3);

        g.setFont(Theme.bold(Math.round(13 * Math.max(sc, 0.85f))));
        g.setColor(Theme.MINT);
        drawSpaced(g, "LEARN \u2022 EXPLORE \u2022 PLAY", left, 78, 2f);
        g.setFont(Theme.bold(Math.round(40 * sc)));
        g.setColor(Color.WHITE);
        int lh = Math.round(48 * sc);
        g.drawString("Your journey around", left, 78 + lh + 6);
        g.setColor(Theme.TEAL);
        g.drawString("the world starts here.", left, 78 + 2 * lh + 6);
        g.setFont(Theme.plain(Math.round(17 * Math.max(sc, 0.82f))));
        g.setColor(new Color(0xD8E4F8));
        g.drawString("Test your knowledge of countries, capitals and flags.", left, 78 + 2 * lh + 46);

        if (w >= 860) {
            double r = Math.min(131, hh * 0.31);
            Art.globe(g, w - Math.max(150, w * 0.17), hh * 0.45, r, Theme.MINT, Theme.LAND, Theme.AMBER);
        }

        g.setFont(Theme.font(java.awt.Font.PLAIN, 13));
        g.setColor(Theme.SMALL);
        String f = "World Explorer \u2022 A distributed service architecture game";
        int fy = Math.max(h - 22, card.getY() + card.getHeight() + 30);
        g.drawString(f, (w - g.getFontMetrics().stringWidth(f)) / 2, fy);
        g.dispose();
    }

    private static void drawSpaced(Graphics2D g, String s, int x, int y, float spacing) {
        float cx = x;
        for (char ch : s.toCharArray()) {
            g.drawString(String.valueOf(ch), cx, y);
            cx += g.getFontMetrics().charWidth(ch) + spacing;
        }
    }
}
