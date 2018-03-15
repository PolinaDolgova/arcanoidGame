package com.polinadolgova.game.arcanoid.controller;

import com.polinadolgova.game.arcanoid.entity.Platform;
import com.polinadolgova.game.arcanoid.game.Game;
import com.polinadolgova.game.arcanoid.window.Display;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter {

    private final int DELTA = 50;

    private Platform platform;
    private JPanel panel;

    public MouseController(Platform platform, JPanel panel) {
        this.platform = platform;
        this.panel = panel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isOnGameField(e.getX()) && isHitTheMark(e.getX(), e.getY())) {
            platform.setX(e.getX() - (Display.WIDTH - Game.GAME_FIELD_WIDTH) / 2);
            panel.repaint();
        }
    }

    private boolean isOnGameField(int x) {
        return x + Platform.PLATFORM_WIDTH / 2 <= Display.WIDTH - (Display.WIDTH - Game.GAME_FIELD_WIDTH) / 2
                && x - Platform.PLATFORM_WIDTH / 2 >= (Display.WIDTH - Game.GAME_FIELD_WIDTH) / 2;
    }

    private boolean isHitTheMark(int x, int y) {
        return y >= Game.GAME_FIELD_HEIGHT - DELTA && y <= Game.GAME_FIELD_HEIGHT + DELTA
                && x >= platform.getX() - Platform.PLATFORM_WIDTH / 2 + (Display.WIDTH - Game.GAME_FIELD_WIDTH) / 2
                && x <= platform.getX() + Platform.PLATFORM_WIDTH / 2 + (Display.WIDTH - Game.GAME_FIELD_WIDTH) / 2;
    }
}
