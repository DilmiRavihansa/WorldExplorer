package com.worldexplorer.peripherals.ui;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Colours, fonts and drawing helpers taken from the Figma UI kit. One place to restyle the whole game. */
public final class Theme {
    // ---- dark "game HUD" palette ----
    public static final Color BG = new Color(0x080C1A);          // page background (deep space)
    public static final Color CARD = new Color(0x121A33);        // panels / cards
    public static final Color CARD_HI = new Color(0x1A2547);      // raised / hover surface
    public static final Color TEXT = new Color(0xEAF0FF);        // primary text
    public static final Color INK = new Color(0x0A1020);         // dark text on bright buttons
    public static final Color TEAL = new Color(0x22D3EE);        // main neon accent (cyan)
    public static final Color TEAL_DARK = new Color(0x0FA5C2);
    public static final Color TEAL_LIGHT = new Color(0x13253F);  // subtle accent tint surface
    public static final Color VIOLET = new Color(0x8B5CF6);      // secondary accent
    public static final Color MUTED = new Color(0xA3B0D0);
    public static final Color SMALL = new Color(0x7F8DB3);
    public static final Color BORDER = new Color(0x27335C);
    public static final Color INPUT_BG = new Color(0x0C1328);
    public static final Color INPUT_BORDER = new Color(0x2C3A66);
    public static final Color AMBER = new Color(0xFFC233);
    public static final Color ORANGE = new Color(0xFF8A1F);
    public static final Color HERO_A = new Color(0x2A1466);
    public static final Color HERO_B = new Color(0x0B5E86);
    public static final Color MINT = new Color(0x7CF2E4);
    public static final Color LAND = new Color(0x34D399);
    public static final Color GOLD_BG = new Color(0x3B2D0C);
    public static final Color GOLD_TEXT = new Color(0xFFD25E);
    public static final Color AVATAR_BG = new Color(0x26346A);
    public static final Color GREEN = new Color(0x34D399);
    public static final Color GREEN_BG = new Color(0x0E3A30);
    public static final Color RED = new Color(0xFF6B7A);
    public static final Color RED_BG = new Color(0x40182A);
    public static final Color ROW_ALT = new Color(0x0F1730);

    private static final String FAMILY = pickFamily();

    private Theme() { }

    private static String pickFamily() {
        Set<String> have = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[] { "Inter", "Segoe UI", "SF Pro Text", "Helvetica Neue", "Arial" })
            if (have.contains(f)) return f;
        return Font.SANS_SERIF;
    }

    public static Font font(int style, float size) { return new Font(FAMILY, style, Math.round(size)); }
    public static Font bold(float size) { return font(Font.BOLD, size); }
    public static Font plain(float size) { return font(Font.PLAIN, size); }

    /** Window-width helper used by screens to switch to compact layouts. */
    public static final int COMPACT = 900, NARROW = 640;

    public static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }

    public static Graphics2D quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        return g;
    }
}
