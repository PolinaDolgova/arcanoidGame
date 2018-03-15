package com.polinadolgova.game.arcanoid.entity;

import com.polinadolgova.game.arcanoid.game.Game;
import com.polinadolgova.game.arcanoid.util.GameTimer;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

public class Ball implements Runnable {

    public static final int BALL_RADIUS = 8;

    private final int UPPER_TIME = 8;
    private final int DELAY = 10;

    private double angle;
    private Thread ballThread;
    private Point position;
    private float speed;
    private boolean inGame;
    private boolean paused;
    private Platform platform;
    private Game game;
    private GameTimer timer;
    private static Random random = new Random();

    public Ball(Point position, float speed, Platform platform, Game game, GameTimer timer, double angle) {
        this.position = position;
        this.angle = angle;
        this.speed = speed;
        this.platform = platform;
        this.game = game;
        this.timer = timer;
    }

    public static double getAngle(List<Ball> balls, double angle) {
        while (true) {
            boolean isFind = false;
            for (Ball ball : balls) {
                if (ball.angle == angle) {
                    isFind = true;
                }
            }
            if (isFind) {
                int randomAngle = random.nextInt(1) * 90 + 10 + random.nextInt(70);
                angle = randomAngle * Math.PI / 180 + Math.PI / 2;
                getAngle(balls, angle);
            } else {
                return angle;
            }
        }
    }

    public synchronized void pause() {
        paused = !paused;
        notify();
    }

    private synchronized void moveBall() {
        position.translate((int) Math.round(speed * Math.sin(angle)), (int) Math.round(speed * Math.cos(angle)));

        if (position.x < BALL_RADIUS) {
            position.x = BALL_RADIUS;
            angle = 2 * Math.PI - angle;
        }

        if (position.x >= Game.GAME_FIELD_WIDTH - BALL_RADIUS) {
            position.x = Game.GAME_FIELD_WIDTH - BALL_RADIUS;
            angle = 2 * Math.PI - angle;
        }

        if (position.y < BALL_RADIUS) {
            position.y = BALL_RADIUS;
            recalculateBounce();
        }

        Rectangle platformRect = new Rectangle(platform.getX() - Platform.PLATFORM_WIDTH / 2
                , platform.getY() - Platform.PLATFORM_HEIGHT / 2, Platform.PLATFORM_WIDTH, Platform.PLATFORM_HEIGHT);
        Rectangle ballRect = new Rectangle((int) position.getX() - BALL_RADIUS, (int) position.getY() - BALL_RADIUS
                , BALL_RADIUS * 2, BALL_RADIUS * 2);

        if (platformRect.intersects(ballRect)) {
            recalculateBounce();
            if (position.y >= platform.getY() - Platform.PLATFORM_HEIGHT / 2) {
                stop();
            }
        }

        if (position.y >= Game.GAME_FIELD_HEIGHT - BALL_RADIUS) {
            stop();
        }
    }

    private void recalculateBounce() {
        if (angle < Math.PI) {
            angle = Math.PI - angle;
        } else {
            angle = 3 * Math.PI - angle;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(position.x - BALL_RADIUS, position.y - BALL_RADIUS,
                BALL_RADIUS * 2, BALL_RADIUS * 2);
    }

    public void play() {
        if (!inGame) {
            inGame = true;
            ballThread = new Thread(this);
            ballThread.start();
        }
    }

    public void stop() {
        synchronized (this) {
            if (paused) {
                notify();
            }
        }
        inGame = false;
        paused = false;
        game.removeBall(this);
    }

    @Override
    public void run() {
        while (inGame) {
            if (paused) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                moveBall();
                try {
                    if (!Thread.interrupted()) {
                        Thread.sleep((UPPER_TIME - timer.getTime() / DELAY) > 0
                                ? (UPPER_TIME - timer.getTime() / DELAY)
                                : 1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
