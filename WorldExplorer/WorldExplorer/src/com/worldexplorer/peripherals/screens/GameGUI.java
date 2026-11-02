package com.worldexplorer.peripherals.screens;

import com.worldexplorer.auth.AuthResult;
import com.worldexplorer.auth.AuthService;
import com.worldexplorer.auth.SessionManager;
import com.worldexplorer.engine.*;
import com.worldexplorer.peripherals.ui.NavBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main window and the ONLY class that knows all screens and all services (the "controller").
 * Screens report events through small listener interfaces; this class decides what happens next.
 * Services (auth, questions, scores) are handed in through the constructor, so they can be swapped or tested.
 */
public class GameGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String LOGIN = "login", MODES = "modes", QUIZ = "quiz", BONUS = "bonus", RESULTS = "results", HELP = "help";
    private static final int FEEDBACK_MILLIS = 1300;

    // services
    private final AuthService auth;
    private final SessionManager sessions;
    private final QuestionProvider questions;
    private final BonusProvider bonuses;
    private final ScoreRepository scores;

    // views
    private final CardLayout cards = new CardLayout();
    private final JPanel deck = new JPanel(cards);
    private final NavBar nav = new NavBar();
    private final LoginScreen login = new LoginScreen();
    private final ModeSelectScreen modes = new ModeSelectScreen();
    private final QuizScreen quiz = new QuizScreen();
    private final BonusScreen bonus = new BonusScreen();
    private final ResultsScreen results = new ResultsScreen();
    private final HowtoPlay help = new HowtoPlay();

    // state
    private String player = null;
    private GameEngine engine = null;
    private boolean gameActive = false;
    private boolean busy = false;
    private String lastMode = null;
    private int secondsLeft = 0;
    private final Timer clock = new Timer(1000, e -> tick());
    private Timer feedbackDelay = null;

    public GameGUI(AuthService auth, SessionManager sessions, QuestionProvider questions, BonusProvider bonuses,
                   ScoreRepository scores) {
        super("World Explorer");
        this.auth = auth;
        this.sessions = sessions;
        this.questions = questions;
        this.bonuses = bonuses;
        this.scores = scores;

        setLayout(new BorderLayout());
        deck.add(login.scroller(), LOGIN);
        deck.add(modes, MODES);
        deck.add(quiz.asPage(), QUIZ);
        deck.add(bonus.asPage(), BONUS);
        deck.add(results.asPage(), RESULTS);
        deck.add(help.asPage(), HELP);
        add(nav, BorderLayout.NORTH);
        add(deck, BorderLayout.CENTER);
        wireEvents();

        setSize(1280, 860);
        setMinimumSize(new Dimension(420, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { if (gameActive && engine != null) engine.endGame(); }
        });
        startUp();
    }

    // ------------------------------------------------------------------ events

    private void wireEvents() {
        nav.setListener(new NavBar.Listener() {
            @Override public void onNavigate(NavBar.Target t) { navigate(t); }
            @Override public void onSignOut() { signOut(); }
        });
        login.setListener(new LoginScreen.Listener() {
            @Override public void onLogin(String u, char[] p, boolean remember) { doLogin(u, p, remember); }
            @Override public void onForgotPassword() {
                JOptionPane.showMessageDialog(GameGUI.this,
                        "Password reset is not available in this offline version.\nAsk the game administrator to reset your account.",
                        "Forgot password", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        modes.setListener(this::startMode);
        help.setListener(this::showModes);
        quiz.setListener(new QuizScreen.Listener() {
            @Override public void onAnswer(int index) { answer(index, false); }
            @Override public void onEndGame() { finishGame(); }
        });
        bonus.setListener(index -> answer(index, false));
        results.setListener(new ResultsScreen.Listener() {
            @Override public void onPlayAgain() {
                if (lastMode != null && results.isShowingFinal()) startMode(lastMode); else showModes();
            }
            @Override public void onChangeMode() { showModes(); }
            @Override public void onSignOut() { signOut(); }
        });
    }

    // ------------------------------------------------------------------ session / login

    private void startUp() {
        // "Remember me": a valid session cookie signs the player in again
        sessions.restore().filter(auth::userExists).ifPresentOrElse(this::enterApp, this::showLogin);
    }

    private void showLogin() {
        nav.setVisible(false);
        login.reset();
        cards.show(deck, LOGIN);
        login.focusUser();
    }

    private void doLogin(String username, char[] password, boolean remember) {
        login.setBusy(true);
        new SwingWorker<AuthResult, Void>() {
            @Override protected AuthResult doInBackground() { return auth.login(username, password); }
            @Override protected void done() {
                login.setBusy(false);
                try {
                    AuthResult r = get();
                    login.clearPassword();
                    if (r.isSuccess()) {
                        sessions.start(r.getUsername(), remember);
                        enterApp(r.getUsername());
                    } else {
                        login.showError(r.getMessage());
                    }
                } catch (Exception ex) {
                    login.showError("Sign in failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void enterApp(String user) {
        player = user;
        nav.setUser(user);
        nav.setVisible(true);
        showModes();
    }

    private void signOut() {
        abandonGame(true);
        sessions.end();
        player = null;
        showLogin();
    }

    // ------------------------------------------------------------------ navigation

    private void navigate(NavBar.Target target) {
        if (gameActive) {
            int ok = JOptionPane.showConfirmDialog(this, "Leave the current game?\nYour score so far will be saved.",
                    "World Explorer", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            abandonGame(true);
        }
        if (target == NavBar.Target.LEADERBOARD) showLeaderboard();
        else if (target == NavBar.Target.HELP) showHelp();
        else showModes();
    }

    private void showModes() {
        nav.setActive(NavBar.Target.HOME);
        cards.show(deck, MODES);
    }

    private void showLeaderboard() {
        nav.setActive(NavBar.Target.LEADERBOARD);
        results.showLeaderboardOnly(player, scores.top(10));
        cards.show(deck, RESULTS);
    }

    private void showHelp() {
        nav.setActive(NavBar.Target.HELP);
        help.scrollToTop();
        cards.show(deck, HELP);
    }

    // ------------------------------------------------------------------ game flow

    private void startMode(String mode) {
        lastMode = mode;
        engine = new GameEngine(player, mode, questions, bonuses, scores);
        gameActive = true;
        nav.setActive(NavBar.Target.PLAY);
        quiz.showLoading(mode);
        cards.show(deck, QUIZ);
        loadNext(true);
    }

    /** Fetches the next question on a background thread so the window never freezes while the APIs answer. */
    private void loadNext(boolean first) {
        busy = true;
        final GameEngine e = engine;
        new SwingWorker<Game, Void>() {
            @Override protected Game doInBackground() { return e.nextGame(); }
            @Override protected void done() {
                busy = false;
                if (e != engine || !gameActive) return;   // the player already left
                Game g = null;
                try { g = get(); } catch (Exception ex) { /* handled below */ }
                if (g == null) {
                    JOptionPane.showMessageDialog(GameGUI.this, (first ? "Could not get data from the REST Countries API."
                            : "Could not get the next question.") + "\n\nReason: " + e.getLastError());
                    if (first) { abandonGame(false); showModes(); } else finishGame();
                    return;
                }
                showGame(g);
            }
        }.execute();
    }

    private void showGame(Game g) {
        if (g.isBonus()) {
            int every = engine.getBonusEvery();
            int inCycle = engine.getAnswered() % every == 0 ? every : engine.getAnswered() % every;
            bonus.showBonus(g, engine.getBonusPoints(), inCycle + " / " + every + " questions");
            cards.show(deck, BONUS);
            clock.stop();
            return;
        }
        quiz.showQuestion(engine.getMode(), g, engine.getScore(), engine.getLevel(), engine.getAnswered() + 1,
                engine.getRoundLength());
        cards.show(deck, QUIZ);
        if (engine.getSecondsPerQuestion() > 0) {
            secondsLeft = engine.getSecondsPerQuestion();
            quiz.setSecondsLeft(secondsLeft);
            clock.restart();
        } else {
            quiz.setSecondsLeft(-1);
        }
    }

    private void tick() {
        if (busy || !gameActive) return;
        secondsLeft--;
        quiz.setSecondsLeft(Math.max(secondsLeft, 0));
        if (secondsLeft <= 0) answer(-1, true);
    }

    private void answer(int index, boolean timedOut) {
        if (busy || !gameActive || engine == null) return;
        busy = true;
        clock.stop();
        Game old = engine.getCurrent();
        int levelBefore = engine.getLevel();
        boolean ok = engine.checkSolution(index);
        String msg;
        if (ok) {
            msg = old.isBonus() ? "Correct! +" + engine.getLastPoints() + " bonus points!" : "Correct! +" + engine.getLastPoints();
            if (engine.getLevel() > levelBefore) msg += "  Level " + engine.getLevel() + " unlocked!";
        } else if (old.isBonus()) {
            msg = "Wrong. There were " + old.getCorrectAnswer() + " fish.";
        } else {
            msg = (timedOut ? "Time's up! The answer was: " : "Wrong. The answer was: ") + old.getCorrectAnswer();
        }
        if (old.isBonus()) bonus.showFeedback(index, old.getSolution(), ok, msg);
        else {
            quiz.showFeedback(index, old.getSolution(), ok, msg);
            quiz.updateStats(engine.getScore(), engine.getLevel());
        }
        feedbackDelay = new Timer(FEEDBACK_MILLIS, ev -> {
            busy = false;
            if (!gameActive) return;
            if (engine.isRoundOver()) finishGame();
            else {
                quiz.showLoading(engine.getMode());
                cards.show(deck, QUIZ);
                loadNext(false);
            }
        });
        feedbackDelay.setRepeats(false);
        feedbackDelay.start();
    }

    /** Ends the round normally: saves the score and shows the results. */
    private void finishGame() {
        if (engine == null) return;
        clock.stop();
        if (feedbackDelay != null) feedbackDelay.stop();
        GameSummary summary = engine.endGame();
        gameActive = false;
        busy = false;
        nav.setActive(NavBar.Target.LEADERBOARD);
        results.showFinal(summary, scores.top(10));
        cards.show(deck, RESULTS);
    }

    /** Leaves a game without showing results. save = keep the score so far. */
    private void abandonGame(boolean save) {
        clock.stop();
        if (feedbackDelay != null) feedbackDelay.stop();
        if (gameActive && engine != null && save) engine.endGame();
        gameActive = false;
        busy = false;
        engine = null;
    }
}
