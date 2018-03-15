package com.polinadolgova.game.arcanoid.game;

import com.polinadolgova.game.arcanoid.controller.MouseController;
import com.polinadolgova.game.arcanoid.entity.Ball;
import com.polinadolgova.game.arcanoid.entity.Platform;
import com.polinadolgova.game.arcanoid.util.GameTimer;
import com.polinadolgova.game.arcanoid.util.ScoreCounter;
import com.polinadolgova.game.arcanoid.window.Display;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends JPanel implements Runnable {

    public static final int GAME_FIELD_WIDTH = 400;
    public static final int GAME_FIELD_HEIGHT = 500;

    private final int LOWER_INPUT_BOUND = 0;
    private final int UPPER_INPUT_BOUND = 10;

    private boolean running;
    private boolean paused = true;
    private Thread gameThread;
    private Platform platform;
    private List<Ball> ballList;
    private int ballAmount, maxSpeed;
    private Random random;

    private GameTimer timer = new GameTimer();
    private ScoreCounter scoreCounter;

    private JLabel time, ballAmountLbl, maxSpeedLbl;
    private JTextField inputFieldAmount;
    private JTextField inputFieldSpeed;
    private JPanel toolPanel;
    private JButton submit;
    private JButton start;
    private JButton pause;
    private JButton stop;

    public Game() {
        ballList = new CopyOnWriteArrayList<>();
        random = new Random();

        platform = new Platform();
        scoreCounter = new ScoreCounter(timer, ballList);
        time = new JLabel("Time: " + timer.getTime() + "| Score: " + 0);
        ballAmountLbl = new JLabel("Ball amount:");
        inputFieldAmount = new JTextField("1");
        maxSpeedLbl = new JLabel("Max speed:");
        inputFieldSpeed = new JTextField("5");
        toolPanel = new JPanel();
        submit = new JButton("Submit");
        start = new JButton("Start");
        pause = new JButton("Pause");
        stop = new JButton("Stop");

        start.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
        setEnvironment();
    }

    public void play() {
        initialization();
        if (ballAmount > 0) {
            start();
        }
    }

    private void initialization() {
        inputFieldAmount.setEnabled(true);
        inputFieldSpeed.setEnabled(true);
        submit.setEnabled(true);
    }

    private synchronized void start() {
        if (running) {
            return;
        }
        inputFieldAmount.setEnabled(false);
        inputFieldSpeed.setEnabled(false);
        submit.setEnabled(false);
        start.setEnabled(false);
        for (int i = 0; i < ballAmount; i++) {
            int randomAngle = random.nextInt(1) * 90 + 15 + random.nextInt(65);
            double angle = Ball.getAngle(ballList, (randomAngle * Math.PI / 180 + Math.PI / 2));
            Ball ball = new Ball(new Point(Game.GAME_FIELD_WIDTH / 2, Game.GAME_FIELD_HEIGHT / 2)
                    , (maxSpeed == 1) ? 1f : random.nextInt(maxSpeed - 1) + 1f
                    , platform
                    , this
                    , timer
                    , angle);
            ballList.add(ball);
        }

        running = true;
        paused = false;
        togglePause();
        repaint();
        gameThread = new Thread(this);
        timer.start();
        scoreCounter.start();
        gameThread.start();
    }

    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        paused = true;

        long tempTime = timer.getTime();
        long tempScore = scoreCounter.getScore();

        inputFieldAmount.setEnabled(true);
        inputFieldSpeed.setEnabled(true);
        submit.setEnabled(true);
        timer.stop();
        scoreCounter.stop();
        platform.setX(GAME_FIELD_WIDTH / 2);
        ballList.forEach(Ball::stop);
        repaint();
        JOptionPane.showMessageDialog(this, "Your time: " + tempTime
                + " | Your score: " + tempScore, "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        pause.setEnabled(false);
        stop.setEnabled(false);
        System.out.println();
    }

    @Override
    public void run() {
        while (running) {
            if (paused) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.err.print("Exception in pausing");
                }
            } else {
                try {
                    ballList.forEach(Ball::play);
                    repaint();
                    synchronized (this) {
                        wait(3);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ballList.isEmpty()) {
                stop();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate((getWidth() - Game.GAME_FIELD_WIDTH) / 2, (getHeight() - Game.GAME_FIELD_HEIGHT) / 4);
        g.setColor(Color.white);
        g.fillRect(0, 0, Game.GAME_FIELD_WIDTH, Game.GAME_FIELD_HEIGHT);
        g.drawRect(0, 0, Game.GAME_FIELD_WIDTH, Game.GAME_FIELD_HEIGHT);

        ballList.forEach(x -> x.render(g));
        platform.render(g);
        time.setText("Time: " + timer.getTime() + " | Score: " + scoreCounter.getScore());
    }

    public synchronized void togglePause() {
        paused = !paused;
        notify();
        timer.pause();
        scoreCounter.pause();
        ballList.forEach(Ball::pause);
    }

    public void removeBall(Ball ball) {
        ballList.remove(ball);
    }

    private void setSubmit() {
        submit.addActionListener(e -> {
            try {
                int amount = Integer.valueOf(inputFieldAmount.getText());
                int inputMaxSpeed = Integer.valueOf(inputFieldSpeed.getText());
                if (amount > LOWER_INPUT_BOUND && amount <= UPPER_INPUT_BOUND
                        && inputMaxSpeed > LOWER_INPUT_BOUND && inputMaxSpeed <= UPPER_INPUT_BOUND) {
                    togglePause();
                    running = false;
                    ballAmount = Integer.valueOf(amount);
                    maxSpeed = Integer.valueOf(inputMaxSpeed);

                    inputFieldAmount.setEnabled(false);
                    inputFieldSpeed.setEnabled(false);
                    submit.setEnabled(false);
                    start.setEnabled(true);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exc) {
                JOptionPane.showMessageDialog(Display.getGame(), "Wrong type of input data!\nMax ball amount =" + UPPER_INPUT_BOUND
                        + "\nMax ball speed = " + UPPER_INPUT_BOUND + "\nPlease, try again", "Wrong values", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void setStart() {
        start.addActionListener(e -> {
            if (!running) {
                play();
                togglePause();
                start.setEnabled(false);
                pause.setEnabled(true);
                stop.setEnabled(true);
            }
        });
    }

    private void setPause() {
        pause.addActionListener(e -> {
            togglePause();
            if (pause.getText().equals("Pause")) {
                pause.setText("Resume");
            } else {
                pause.setText("Pause");
            }
        });
    }

    private void setStop() {
        stop.addActionListener(e -> {
            stop();
        });
    }

    private void setTime() {
        time.setFont(new Font("Courier New", Font.BOLD, 20));
        time.setHorizontalAlignment(JLabel.CENTER);
    }

    private void setGame() {
        setSize(new Dimension(Game.GAME_FIELD_WIDTH, Game.GAME_FIELD_HEIGHT));
        setFocusable(true);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void setLabels() {
        ballAmountLbl.setFont(new Font("Courier New", Font.BOLD, 15));
        maxSpeedLbl.setFont(new Font("Courier New", Font.BOLD, 15));
    }

    private void setInputFields() {
        inputFieldAmount.setHorizontalAlignment(JTextField.CENTER);
        inputFieldAmount.setPreferredSize(new Dimension(100, 20));

        inputFieldSpeed.setHorizontalAlignment(JTextField.CENTER);
        inputFieldSpeed.setPreferredSize(new Dimension(100, 20));
    }

    private void setToolPanel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 0, 0, 10);
        toolPanel.setLayout(layout);

        setToolPanelParam(gridBagConstraints, 0, ballAmountLbl);
        setToolPanelParam(gridBagConstraints, 1, inputFieldAmount);
        setToolPanelParam(gridBagConstraints, 2, maxSpeedLbl);
        setToolPanelParam(gridBagConstraints, 3, inputFieldSpeed);
        setToolPanelParam(gridBagConstraints, 4, submit);
        setToolPanelParam(gridBagConstraints, 5, start);
        setToolPanelParam(gridBagConstraints, 6, pause);
        setToolPanelParam(gridBagConstraints, 7, stop);
    }

    private void setToolPanelParam(GridBagConstraints gridBagConstraints, int gridy, Component component) {
        gridBagConstraints.gridy = gridy;
        toolPanel.add(component, gridBagConstraints);
    }

    private void addDataGame() {
        add(time, BorderLayout.SOUTH);
        add(toolPanel, BorderLayout.EAST);

        addMouseMotionListener(new MouseController(platform, this));
    }

    private void setEnvironment() {
        setGame();
        setSubmit();
        setStart();
        setPause();
        setStop();
        setTime();
        setLabels();
        setInputFields();
        setToolPanel();
        addDataGame();
    }
}