package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EnumMap;
import java.util.Map;

/**
 * Top navigation bar shared by all screens after login (dark game HUD style).
 * It only reports what the player clicked through the Listener (event delegation);
 * it knows nothing about the other screens. On narrow windows the links fold into a "Menu" button.
 */
public class NavBar extends JPanel {
    private static final long serialVersionUID = 1L;

    public enum Target { HOME, PLAY, LEADERBOARD, HELP }

    public interface Listener {
        void onNavigate(Target target);
        void onSignOut();
    }

    private static final String[] LABELS = { "Home", "Play", "Leaderboard", "How to Play" };

    private final Map<Target, JButton> links = new EnumMap<>(Target.class);
    private final JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 18));
    private final MenuButton menuButton = new MenuButton();
    private final Brand brand = new Brand();
    private final Profile profile = new Profile();
    private Target active = Target.HOME;
    private Listener listener;
    private boolean compact = false;

    public NavBar() {
        super(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(100, 72));
        setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        add(brand, BorderLayout.WEST);

        center.setOpaque(false);
        for (Target t : Target.values()) addLink(center, t, LABELS[t.ordinal()]);
        add(center, BorderLayout.CENTER);

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        east.setOpaque(false);
        menuButton.setVisible(false);
        east.add(menuButton);
        east.add(profile);
        add(east, BorderLayout.EAST);

        JPopupMenu account = new JPopupMenu();
        account.add(item("Sign out", () -> { if (listener != null) listener.onSignOut(); }));
        profile.addActionListener(e -> account.show(profile, 0, profile.getHeight()));

        JPopupMenu pages = new JPopupMenu();
        for (Target t : Target.values()) pages.add(item(LABELS[t.ordinal()], () -> { if (listener != null) listener.onNavigate(t); }));
        pages.addSeparator();
        pages.add(item("Sign out", () -> { if (listener != null) listener.onSignOut(); }));
        menuButton.addActionListener(e -> pages.show(menuButton, menuButton.getWidth() - 170, menuButton.getHeight()));

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { adapt(); }
        });
    }

    private static JMenuItem item(String text, Runnable r) {
        JMenuItem m = new JMenuItem(text);
        m.setFont(Theme.bold(15));
        m.setBackground(Theme.CARD);
        m.setForeground(Theme.TEXT);
        m.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 28));
        m.addActionListener(e -> r.run());
        return m;
    }

    /** Switch between full links and the compact menu depending on the available width. */
    private void adapt() {
        boolean c = getWidth() < 860;
        if (c != compact) {
            compact = c;
            center.setVisible(!c);
            menuButton.setVisible(c);
            revalidate();
        }
        profile.showName = getWidth() >= 560;
        brand.showText = getWidth() >= 620 || !compact;
        revalidate();
    }

    public void setListener(Listener l) { this.listener = l; }

    public void setUser(String name) { profile.name = name == null ? "" : name; profile.repaint(); revalidate(); }

    public void setActive(Target t) { this.active = t; repaint(); links.values().forEach(JButton::repaint); }

    private void addLink(JPanel parent, Target t, String text) {
        JButton b = new JButton(text) {
            private static final long serialVersionUID = 1L;
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Theme.quality((Graphics2D) g0.create());
                boolean on = active == t, hover = getModel().isRollover();
                if (on) {
                    g.setColor(Theme.alpha(Theme.TEAL, 28));
                    g.fillRoundRect(0, 2, getWidth() - 1, getHeight() - 5, 24, 24);
                    g.setColor(Theme.alpha(Theme.TEAL, 110));
                    g.drawRoundRect(0, 2, getWidth() - 1, getHeight() - 5, 24, 24);
                } else if (hover) {
                    g.setColor(Theme.alpha(Color.WHITE, 14));
                    g.fillRoundRect(0, 2, getWidth() - 1, getHeight() - 5, 24, 24);
                }
                g.setFont(Theme.font(on ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 15));
                g.setColor(on ? Theme.TEAL : hover ? Theme.TEXT : Theme.MUTED);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(getFontMetrics(Theme.bold(15)).stringWidth(getText()) + 32, 38);
            }
        };
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> { if (listener != null) listener.onNavigate(t); });
        links.put(t, b);
        parent.add(b);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        g.setPaint(new GradientPaint(0, 0, new Color(0x0D1330), 0, getHeight(), new Color(0x0A1024)));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setPaint(new GradientPaint(0, 0, Theme.alpha(Theme.VIOLET, 200), getWidth(), 0, Theme.alpha(Theme.TEAL, 200)));
        g.fillRect(0, getHeight() - 2, getWidth(), 2);
        g.dispose();
    }

    private static class Brand extends JPanel {
        private static final long serialVersionUID = 1L;
        boolean showText = true;
        Brand() { setOpaque(false); }
        @Override public Dimension getPreferredSize() { return new Dimension(showText ? 262 : 50, 72); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            ScrollPage.glow(g, 22, 36, 34, Theme.alpha(Theme.TEAL, 60));
            Art.brand(g, 0, 14, 44);
            if (showText) {
                g.setFont(Theme.bold(20));
                g.setColor(Theme.TEXT);
                g.drawString("WORLD", 58, 36);
                g.setColor(Theme.TEAL);
                g.drawString("EXPLORER", 58 + g.getFontMetrics().stringWidth("WORLD ") , 36);
                g.setFont(Theme.bold(10));
                g.setColor(Theme.SMALL);
                g.drawString("GEOGRAPHY QUEST", 59, 52);
            }
            g.dispose();
        }
    }

    private static class MenuButton extends JButton {
        private static final long serialVersionUID = 1L;
        MenuButton() {
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Menu");
        }
        @Override public Dimension getPreferredSize() { return new Dimension(104, 72); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            boolean hover = getModel().isRollover();
            g.setColor(hover ? Theme.CARD_HI : Theme.CARD);
            g.fillRoundRect(0, 16, getWidth() - 1, 40, 22, 22);
            g.setColor(hover ? Theme.TEAL : Theme.BORDER);
            g.drawRoundRect(0, 16, getWidth() - 1, 40, 22, 22);
            g.setColor(Theme.TEXT);
            for (int i = 0; i < 3; i++) g.fillRoundRect(16, 28 + i * 6, 16, 2, 2, 2);
            g.setFont(Theme.bold(14));
            g.drawString("Menu", 42, 41);
            g.dispose();
        }
    }

    private static class Profile extends JButton {
        private static final long serialVersionUID = 1L;
        String name = "";
        boolean showName = true;
        Profile() {
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Account menu");
        }
        @Override public Dimension getPreferredSize() {
            return new Dimension(showName ? getFontMetrics(Theme.bold(15)).stringWidth(name) + 76 : 48, 72);
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.quality((Graphics2D) g0.create());
            Art.avatar(g, 6, 18, 36, name);
            if (showName) {
                g.setFont(Theme.bold(15));
                g.setColor(Theme.TEXT);
                g.drawString(name, 52, 41);
            }
            g.dispose();
        }
    }
}
