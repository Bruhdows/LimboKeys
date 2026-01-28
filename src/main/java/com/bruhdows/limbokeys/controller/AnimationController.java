package com.bruhdows.limbokeys.controller;

import com.bruhdows.limbokeys.LimboKeys;
import com.bruhdows.limbokeys.model.Constants;
import com.bruhdows.limbokeys.model.GameState;
import com.bruhdows.limbokeys.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class AnimationController {
    private final GameState state;
    private final ImageUtil imageUtil;

    public AnimationController(GameState state) {
        this.state = state;
        this.imageUtil = new ImageUtil();
    }

    public void animateHueShift(JLabel label, Runnable onComplete) {
        URL resource = LimboKeys.class.getResource("/key.png");
        if (resource == null) return;

        try {
            BufferedImage original = ImageIO.read(resource);
            BufferedImage scaled = imageUtil.toBufferedImage(
                    original.getScaledInstance(Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH)
            );

            final long startTime = System.currentTimeMillis();
            final long duration = (long)(Constants.HUE_SHIFT_FRAMES * Constants.HUE_SHIFT_TIMER_DELAY_MS);
            Timer timer = new Timer(Constants.HUE_SHIFT_TIMER_DELAY_MS, null);

            timer.addActionListener(e -> {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float)elapsed / duration);
                float hueShift = progress <= 0.5f
                        ? progress * 2 * Constants.HUE_SHIFT_MAX_DEGREES
                        : (1 - progress) * 2 * Constants.HUE_SHIFT_MAX_DEGREES;

                BufferedImage shifted = imageUtil.quickHueShift(scaled, hueShift);
                label.setIcon(new ImageIcon(shifted));

                if (progress >= 1.0f) {
                    timer.stop();
                    label.setIcon(new ImageIcon(scaled));
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            timer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void shuffleSequence(int patternIndex, Runnable onComplete) {
        if (patternIndex >= state.getShufflePatterns().length) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        animateShuffle(state.getShufflePatterns()[patternIndex], () -> {
            Timer delayTimer = new Timer(Constants.SHUFFLE_DELAY_MS,
                    e -> shuffleSequence(patternIndex + 1, onComplete));
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
    }

    private void animateShuffle(int[] targetPattern, Runnable onComplete) {
        Point[] startPositions = new Point[8];
        Point[] endPositions = new Point[8];

        for (int i = 0; i < 8; i++) {
            startPositions[i] = state.getLabels()[i].getLocation();
            endPositions[i] = state.getPositions()[targetPattern[i]];
        }

        final long startTime = System.currentTimeMillis();
        final long duration = (long)(Constants.ANIMATION_DURATION_FRAMES * Constants.ANIMATION_TIMER_DELAY_MS);
        Timer timer = new Timer(Constants.ANIMATION_TIMER_DELAY_MS, null);

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / duration);
            float t = easeInOutCubic(progress);

            for (int i = 0; i < 8; i++) {
                int newX = (int) (startPositions[i].x + (endPositions[i].x - startPositions[i].x) * t);
                int newY = (int) (startPositions[i].y + (endPositions[i].y - startPositions[i].y) * t);
                state.getLabels()[i].setLocation(newX, newY);
            }

            if (progress >= 1.0f) {
                timer.stop();
                for (int i = 0; i < 8; i++) {
                    state.getLabels()[i].setLocation(endPositions[i]);
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        timer.start();
    }

    public void animateToEllipse(Runnable onComplete) {
        applyRandomHues();
        Point[] ellipsePositions = calculateEllipsePositions();
        Point[] startPositions = new Point[8];

        for (int i = 0; i < 8; i++) {
            startPositions[i] = state.getLabels()[i].getLocation();
        }

        final long startTime = System.currentTimeMillis();
        final long duration = (long)(Constants.ELLIPSE_ANIMATION_FRAMES * Constants.ANIMATION_TIMER_DELAY_MS);
        Timer timer = new Timer(Constants.ANIMATION_TIMER_DELAY_MS, null);

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / duration);
            float t = easeInOutCubic(progress);

            for (int i = 0; i < 8; i++) {
                int newX = (int) (startPositions[i].x + (ellipsePositions[i].x - startPositions[i].x) * t);
                int newY = (int) (startPositions[i].y + (ellipsePositions[i].y - startPositions[i].y) * t);
                state.getLabels()[i].setLocation(newX, newY);
            }

            if (progress >= 1.0f) {
                timer.stop();
                for (int i = 0; i < 8; i++) {
                    state.getLabels()[i].setLocation(ellipsePositions[i]);
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        timer.start();
    }

    private Point[] calculateEllipsePositions() {
        Point[] positions = new Point[8];
        for (int i = 0; i < 8; i++) {
            double angle = i * 2 * Math.PI / 8;
            int x = (int) (Constants.ELLIPSE_CENTER_X + Constants.ELLIPSE_RADIUS_X * Math.cos(angle)) - Constants.FRAME_SIZE / 2;
            int y = (int) (Constants.ELLIPSE_CENTER_Y + Constants.ELLIPSE_RADIUS_Y * Math.sin(angle)) - Constants.FRAME_SIZE / 2;
            positions[i] = new Point(x, y);
        }
        return positions;
    }

    public void startEllipseSpin() {
        final long[] lastTime = {System.currentTimeMillis()};
        Timer spinTimer = new Timer(Constants.SPIN_TIMER_DELAY_MS, null);
        final double[] angle = {0};

        spinTimer.addActionListener(e -> {
            if (state.isGameEnded()) {
                spinTimer.stop();
                return;
            }

            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastTime[0];
            lastTime[0] = currentTime;

            angle[0] += Constants.SPIN_SPEED * (deltaTime / 1000.0);

            for (int i = 0; i < 8; i++) {
                double windowAngle = angle[0] + (i * 2 * Math.PI / 8);
                int x = (int) (Constants.ELLIPSE_CENTER_X + Constants.ELLIPSE_RADIUS_X * Math.cos(windowAngle)) - Constants.FRAME_SIZE / 2;
                int y = (int) (Constants.ELLIPSE_CENTER_Y + Constants.ELLIPSE_RADIUS_Y * Math.sin(windowAngle)) - Constants.FRAME_SIZE / 2;
                state.getLabels()[i].setLocation(x, y);
            }
        });
        spinTimer.start();
    }

    private void applyRandomHues() {
        try {
            URL resource = LimboKeys.class.getResource("/key.png");
            if (resource == null) return;
            BufferedImage original = ImageIO.read(resource);

            for (int i = 0; i < 8; i++) {
                BufferedImage scaled = imageUtil.toBufferedImage(
                        original.getScaledInstance(Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH)
                );
                BufferedImage coloredKey = imageUtil.quickHueShift(scaled, state.getKeyHues()[i]);
                state.getLabels()[i].setIcon(new ImageIcon(coloredKey));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void flashCorrect(JLabel label) {
        flashLabel(label, Constants.HUE_GREEN);
    }

    public void flashWrong(JLabel label) {
        flashLabel(label, Constants.HUE_RED);
    }

    private void flashLabel(JLabel label, float targetHue) {
        URL resource = LimboKeys.class.getResource("/key.png");
        if (resource == null) return;

        try {
            BufferedImage original = ImageIO.read(resource);
            BufferedImage scaled = imageUtil.toBufferedImage(
                    original.getScaledInstance(Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH)
            );

            Timer flashTimer = new Timer(Constants.FLASH_PULSE_SPEED_MS, null);
            final boolean[] bright = {true};

            flashTimer.addActionListener(e -> {
                BufferedImage colored = imageUtil.quickHueShift(scaled, bright[0] ? targetHue : 0);
                label.setIcon(new ImageIcon(colored));
                bright[0] = !bright[0];
            });
            flashTimer.start();

            Timer stopTimer = new Timer(Constants.FLASH_DURATION_MS, e -> flashTimer.stop());
            stopTimer.setRepeats(false);
            stopTimer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
}
