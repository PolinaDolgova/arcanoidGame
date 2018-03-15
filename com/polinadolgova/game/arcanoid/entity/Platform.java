package com.polinadolgova.game.arcanoid.entity;

import com.polinadolgova.game.arcanoid.game.Game;

import java.awt.*;

public class Platform {

    public static final int PLATFORM_WIDTH = 50;
    public static final int PLATFORM_HEIGHT = 8;

    private final int SPACE_UNDER_PLATFORM = 20;

    private Point position;

    public Platform() {
        position = new Point(Game.GAME_FIELD_WIDTH / 2, Game.GAME_FIELD_HEIGHT - SPACE_UNDER_PLATFORM);
    }

    public void render(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(position.x - Platform.PLATFORM_WIDTH / 2, position.y - Platform.PLATFORM_HEIGHT / 2
                , Platform.PLATFORM_WIDTH, Platform.PLATFORM_HEIGHT);
    }

    public int getX() {
        return position.x;
    }

    public void setX(int x) {
        position.x = x;
    }

    public int getY() {
        return position.y;
    }

    public void setY(int y) {
        position.y = y;
    }
}