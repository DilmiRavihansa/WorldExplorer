package com.worldexplorer.peripherals.ui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/** Rounded text / password input with a placeholder and (for passwords) a Show/Hide toggle. */
public class InputField extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextComponent field;
    private final JPasswordField password;   // null for plain text fields
    private boolean focused = false;

    private static void placeholder(JTextComponent c, Graphics g0, String hint) {
        if (c.getDocument().getLength() > 0 || hint == null) return;
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        g.setColor(Theme.SMALL);
        g.setFont(Theme.plain(17));
        g.drawString(hint, c.getInsets().left, (c.getHeight() + g.getFontMetrics().getAscent()
                - g.getFontMetrics().getDescent()) / 2);
        g.dispose();
    }

    private static class TextInput extends JTextField {
        private static final long serialVersionUID = 1L;
        private final String hint;
        TextInput(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); placeholder(this, g, hint); }
    }

    private static class PasswordInput extends JPasswordField {
        private static final long serialVersionUID = 1L;
        private final String hint;
        PasswordInput(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); placeholder(this, g, hint); }
    }

    private InputField(boolean isPassword, String hint) {
        super(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 16));
        if (isPassword) {
            password = new PasswordInput(hint);
            password.setEchoChar('\u2022');
            field = password;
        } else {
            password = null;
            field = new TextInput(hint);
        }
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setFont(Theme.plain(17));
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.TEAL);
        field.setSelectionColor(Theme.alpha(Theme.TEAL, 110));
        field.setSelectedTextColor(Theme.TEXT);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
        });
        add(field, BorderLayout.CENTER);

        if (isPassword) {
            JButton toggle = new JButton("Show");
            toggle.setFont(Theme.bold(14));
            toggle.setForeground(Theme.TEAL);
            toggle.setBorderPainted(false);
            toggle.setContentAreaFilled(false);
            toggle.setFocusable(false);
            toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            toggle.addActionListener(e -> {
                boolean hidden = password.getEchoChar() != 0;
                password.setEchoChar(hidden ? (char) 0 : '\u2022');
                toggle.setText(hidden ? "Hide" : "Show");
            });
            add(toggle, BorderLayout.EAST);
        }
    }

    public static InputField text(String hint) { return new InputField(false, hint); }
    public static InputField password(String hint) { return new InputField(true, hint); }

    public String getText() { return field.getText(); }
    public char[] getPassword() { return password == null ? new char[0] : password.getPassword(); }
    public void clear() { field.setText(""); }
    public void focusField() { field.requestFocusInWindow(); }
    public void addActionListener(ActionListener l) {
        if (password != null) password.addActionListener(l);
        else ((JTextField) field).addActionListener(l);
    }
    public JTextComponent getField() { return field; }

    @Override
    public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 54); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.quality((Graphics2D) g0.create());
        RoundRectangle2D r = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);
        if (focused) {
            g.setColor(Theme.alpha(Theme.TEAL, 28));
            g.setStroke(new BasicStroke(6f));
            g.draw(r);
        }
        g.setColor(focused ? new Color(0x101A36) : Theme.INPUT_BG);
        g.fill(r);
        g.setColor(focused ? Theme.TEAL : Theme.INPUT_BORDER);
        g.setStroke(new BasicStroke(focused ? 2f : 1f));
        g.draw(r);
        g.dispose();
    }
}
