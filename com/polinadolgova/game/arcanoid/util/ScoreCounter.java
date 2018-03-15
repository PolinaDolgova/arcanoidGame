package com.polinadolgova.game.arcanoid.util;

import com.polinadolgova.game.arcanoid.entity.Ball;

import java.util.List;

public class ScoreCounter implements Runnable {

    private long score;
    private Thread scoreCounterThread;
    private GameTimer timer;
    private List<Ball> ballList;
    private boolean running;
    private boolean paused = true;

    public ScoreCounter(GameTimer timer, List<Ball> ballList) {
        this.timer = timer;
        this.ballList = ballList;
    }

    public void start() {
        running = true;
        paused = true;
        scoreCounterThread = new Thread(this);
        scoreCounterThread.start();
    }

    public void stop() {
        score = 0;
        running = false;
        paused = true;
    }

    public synchronized void pause() {
        paused = !paused;
        notify();
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (paused) {
                    synchronized (this) {
                        wait();
                    }
                } else {
                    score += timer.getTime() * ballList.size();
                    synchronized (this) {
                        wait(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getScore() {
        return score;
    }
}