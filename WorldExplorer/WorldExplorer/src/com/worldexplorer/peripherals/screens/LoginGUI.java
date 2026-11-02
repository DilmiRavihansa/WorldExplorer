package com.worldexplorer.peripherals.screens;

import com.worldexplorer.auth.AuthService;
import com.worldexplorer.auth.FileUserRepository;
import com.worldexplorer.auth.SessionManager;
import com.worldexplorer.engine.FishServer;
import com.worldexplorer.engine.GameServer;
import com.worldexplorer.engine.Leaderboard;
import com.worldexplorer.peripherals.ui.Theme;

import javax.swing.*;

/**
 * Start-up class (run this one). It is the "composition root": the only place where the concrete
 * services are created and plugged into the window. The window starts on the login screen,
 * or goes straight to the mode screen if a "Remember me" cookie is still valid.
 */
public class LoginGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.messageFont", Theme.plain(15));
            UIManager.put("OptionPane.buttonFont", Theme.bold(14));
            // dark dialogs to match the game
            UIManager.put("OptionPane.background", Theme.CARD);
            UIManager.put("Panel.background", Theme.CARD);
            UIManager.put("OptionPane.messageForeground", Theme.TEXT);
            UIManager.put("Label.foreground", Theme.TEXT);
            UIManager.put("Button.background", Theme.CARD_HI);
            UIManager.put("Button.foreground", Theme.TEXT);
            UIManager.put("TextField.background", Theme.INPUT_BG);
            UIManager.put("TextField.foreground", Theme.TEXT);
            UIManager.put("TextField.caretForeground", Theme.TEAL);
            UIManager.put("PopupMenu.background", Theme.CARD);
            UIManager.put("PopupMenu.border", javax.swing.BorderFactory.createLineBorder(Theme.BORDER));
            UIManager.put("MenuItem.selectionBackground", Theme.CARD_HI);
            UIManager.put("MenuItem.selectionForeground", Theme.TEAL);
            new GameGUI(new AuthService(new FileUserRepository()), new SessionManager(),
                    new GameServer(), new FishServer(), new Leaderboard()).setVisible(true);
        });
    }
}
