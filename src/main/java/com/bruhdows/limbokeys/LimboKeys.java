package com.bruhdows.limbokeys;

import com.bruhdows.limbokeys.controller.AnimationController;
import com.bruhdows.limbokeys.model.Constants;
import com.bruhdows.limbokeys.model.GameState;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.net.URL;

public class LimboKeys {
    private final GameState state;
    private final AnimationController animator;
    private boolean clicksEnabled = false;
    private JFrame mainFrame;
    private JPanel gamePanel;

    public LimboKeys() {
        this.state = new GameState();
        this.animator = new AnimationController(state);
    }

    public void start() {
        playMusic();
        initializeWindow();

        Timer delayTimer = new Timer(Constants.GAME_START_DELAY_MS, e -> startGame());
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void initializeWindow() {
        mainFrame = new JFrame("LimboKeys");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new JPanel(null);
        gamePanel.setBackground(new Color(15, 15, 20));
        gamePanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));

        int x = Constants.GRID_START_X;
        int y = Constants.GRID_START_Y;

        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                x = Constants.GRID_START_X;
                y += Constants.SPACING;
            }

            state.getPositions()[i] = new Point(x, y);
            JLabel keyLabel = createKeyLabel(i);
            keyLabel.setBounds(x, y, Constants.FRAME_SIZE, Constants.FRAME_SIZE);
            keyLabel.setOpaque(false);
            state.getLabels()[i] = keyLabel;
            gamePanel.add(keyLabel);

            x += Constants.SPACING;
        }

        mainFrame.setContentPane(gamePanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private JLabel createKeyLabel(final int index) {
        URL resource = LimboKeys.class.getResource("/key.png");
        JLabel imageLabel = new JLabel();

        if (resource != null) {
            ImageIcon originalIcon = new ImageIcon(resource);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH
            );
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            System.out.println("File key.png not found!");
        }

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleGuess(index);
            }
        });

        return imageLabel;
    }

    private void startGame() {
        state.setCorrectKeyIndex(state.generateRandomKeyIndex());
        JLabel keyLabel = state.getLabels()[state.getCorrectKeyIndex()];
        animator.animateHueShift(keyLabel, this::startShufflePhase);
    }

    private void startShufflePhase() {
        animator.shuffleSequence(1, this::startEllipsePhase);
    }

    private void startEllipsePhase() {
        animator.animateToEllipse(() -> {
            clicksEnabled = true;
            animator.startEllipseSpin();
        });
    }

    private void handleGuess(int clickedIndex) {
        if (!clicksEnabled || state.isGameEnded()) {
            return;
        }

        state.setGameEnded(true);
        clicksEnabled = false;

        if (clickedIndex == state.getCorrectKeyIndex()) {
            animator.flashCorrect(state.getLabels()[clickedIndex]);
        } else {
            animator.flashWrong(state.getLabels()[clickedIndex]);
            animator.flashCorrect(state.getLabels()[state.getCorrectKeyIndex()]);
        }

        Timer closeTimer = new Timer(Constants.GAME_END_DELAY_MS, e -> System.exit(0));
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    private void playMusic() {
        new Thread(() -> {
            try {
                URL resource = LimboKeys.class.getResource("/song.wav");
                if (resource != null) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                            new BufferedInputStream(resource.openStream())
                    );
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                }
            } catch (Exception e) {
                System.out.println("Audio not available in browser mode");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LimboKeys().start());
    }
}
