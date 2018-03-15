package com.polinadolgova.game.arcanoid.window;

import com.polinadolgova.game.arcanoid.game.Game;

import javax.swing.*;
import java.awt.*;

public abstract class Display {

    private static final String TITLE = "Arcanoid";

    public static final int WIDTH = 700;
    private static final int HEIGHT = 600;

    private static JFrame window;
    private static boolean created;
    private static Game game;

    public static void run() {
        if (created) {
            return;
        }
        window = new JFrame(Display.TITLE);
        game = new Game();
        setWindow();
        created = true;
        game.play();
    }

    private static void setWindow() {
        window.setPreferredSize(new Dimension(Display.WIDTH, Display.HEIGHT));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(game);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        setCenterMainWindow();
    }

    public static Game getGame() {
        return game;
    }

    private static void setCenterMainWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((screenSize.width - Display.WIDTH) / 2, (screenSize.height - Display.HEIGHT) / 2);
    }
}
