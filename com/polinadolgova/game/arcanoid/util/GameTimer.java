package com.polinadolgova.game.arcanoid.util;

public class GameTimer implements Runnable {

    private Thread timerThread;
    private long time;
    private boolean running;
    private boolean paused = true;

    @Override
    public void run() {
        while (running) {
            try {
                if (paused)
                    synchronized (this) {
                        wait();
                    }
                else {
                    time++;
                    synchronized (this) {
                        wait(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pause() {
        paused = !paused;
        notify();
    }

    public void start() {
        running = true;
        paused = true;
        timerThread = new Thread(this);
        timerThread.start();
    }

    public void stop() {
        time = 0;
        running = false;
        paused = true;
    }

    public long getTime() {
        return time;
    }
}
