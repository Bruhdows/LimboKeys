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
import java.io.IOException;
import java.net.URL;

public class LimboKeys {

    /*
    TODO:
     - Main Menu
     - Additional options: more keys, settings, difficulty
     */

    private final GameState state;
    private final AnimationController animator;
    private boolean clicksEnabled = false;

    public LimboKeys() {
        this.state = new GameState();
        this.animator = new AnimationController(state);
    }

    public void start() {
        playMusic();
        initializeWindows();

        Timer delayTimer = new Timer(Constants.FADE_IN_DELAY_MS, e -> startGame());
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void initializeWindows() {
        int x = Constants.GRID_START_X;
        int y = Constants.GRID_START_Y;

        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                x = Constants.GRID_START_X;
                y += Constants.SPACING;
            }

            state.getPositions()[i] = new Point(x, y);
            JFrame frame = createWindow(x, y, i);
            state.getFrames()[i] = frame;
            frame.setOpacity(0.0f);

            x += Constants.SPACING;
        }
    }

    private void startGame() {
        animator.fadeInWindows(() -> {
            state.setCorrectKeyIndex(state.generateRandomKeyIndex());
            JLabel keyLabel = state.getLabels()[state.getCorrectKeyIndex()];
            animator.animateHueShift(keyLabel, this::startShufflePhase);
        });
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
        if (!clicksEnabled) {
            return;
        }
        if (state.isGameEnded()) {
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
                } else {
                    System.out.println("song.wav not found in resources!");
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("Error playing music: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private JFrame createWindow(int x, int y, final int index) {
        JFrame frame = new JFrame();

        frame.setSize(Constants.FRAME_SIZE, Constants.FRAME_SIZE);
        frame.setLocation(x, y);
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusableWindowState(true);
        frame.setAutoRequestFocus(true);

        URL resource = LimboKeys.class.getResource("/images/key.png");
        if (resource != null) {
            ImageIcon originalIcon = new ImageIcon(resource);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH
            );
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);
            state.getLabels()[index] = imageLabel;

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleGuess(index);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleGuess(index);
                }
            };

            imageLabel.addMouseListener(mouseHandler);
            frame.addMouseListener(mouseHandler);
            frame.getContentPane().addMouseListener(mouseHandler);

            frame.add(imageLabel);
        } else {
            System.out.println("File key.png not found!");
        }

        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LimboKeys().start());
    }
}
