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

    public void fadeInWindows(Runnable onComplete) {
        Timer fadeTimer = new Timer(Constants.ANIMATION_TIMER_DELAY_MS, null);
        final int[] frame = {0};

        Point[] startPositions = calculateDiagonalStartPositions();

        fadeTimer.addActionListener(e -> {
            float progress = (float) frame[0] / Constants.FADE_IN_FRAMES;
            float t = easeInOutCubic(progress);

            for (int i = 0; i < 8; i++) {
                state.getFrames()[i].setOpacity(progress);
                int newX = (int) (startPositions[i].x + (state.getPositions()[i].x - startPositions[i].x) * t);
                int newY = (int) (startPositions[i].y + (state.getPositions()[i].y - startPositions[i].y) * t);
                state.getFrames()[i].setLocation(newX, newY);
            }

            frame[0]++;
            if (frame[0] >= Constants.FADE_IN_FRAMES) {
                fadeTimer.stop();
                for (int i = 0; i < 8; i++) {
                    state.getFrames()[i].setOpacity(1.0f);
                    state.getFrames()[i].setLocation(state.getPositions()[i]);
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        fadeTimer.start();
    }

    private Point[] calculateDiagonalStartPositions() {
        Point[] startPositions = new Point[8];

        int[] directions = {
                0, 1, 0, 1,
                2, 3, 2, 3
        };

        for (int i = 0; i < 8; i++) {
            int offsetX = 0, offsetY = 0;

            switch (directions[i]) {
                case 0:
                    offsetX = -Constants.DIAGONAL_OFFSET;
                    offsetY = -Constants.DIAGONAL_OFFSET;
                    break;
                case 1:
                    offsetX = Constants.DIAGONAL_OFFSET;
                    offsetY = -Constants.DIAGONAL_OFFSET;
                    break;
                case 2:
                    offsetX = -Constants.DIAGONAL_OFFSET;
                    offsetY = Constants.DIAGONAL_OFFSET;
                    break;
                case 3:
                    offsetX = Constants.DIAGONAL_OFFSET;
                    offsetY = Constants.DIAGONAL_OFFSET;
                    break;
            }

            startPositions[i] = new Point(
                    state.getPositions()[i].x + offsetX,
                    state.getPositions()[i].y + offsetY
            );
            state.getFrames()[i].setLocation(startPositions[i]);
        }

        return startPositions;
    }

    public void animateHueShift(JLabel label, Runnable onComplete) {
        URL resource = LimboKeys.class.getResource("/images/key.png");
        if (resource == null) return;

        try {
            BufferedImage original = ImageIO.read(resource);
            BufferedImage scaled = imageUtil.toBufferedImage(
                    original.getScaledInstance(Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH)
            );

            Timer timer = new Timer(Constants.HUE_SHIFT_TIMER_DELAY_MS, null);
            final int[] frame = {0};

            timer.addActionListener(e -> {
                float progress = (float) frame[0] / Constants.HUE_SHIFT_FRAMES;

                float hueShift = progress <= 0.5f
                        ? progress * 2 * Constants.HUE_SHIFT_MAX_DEGREES
                        : (1 - progress) * 2 * Constants.HUE_SHIFT_MAX_DEGREES;

                BufferedImage shifted = imageUtil.quickHueShift(scaled, hueShift);
                label.setIcon(new ImageIcon(shifted));

                frame[0]++;
                if (frame[0] >= Constants.HUE_SHIFT_FRAMES) {
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
            startPositions[i] = state.getFrames()[i].getLocation();
            endPositions[i] = state.getPositions()[targetPattern[i]];
        }

        Timer timer = new Timer(Constants.ANIMATION_TIMER_DELAY_MS, null);
        final int[] frame = {0};

        timer.addActionListener(e -> {
            float progress = (float) frame[0] / Constants.ANIMATION_DURATION_FRAMES;
            float t = easeInOutCubic(progress);

            for (int i = 0; i < 8; i++) {
                int newX = (int) (startPositions[i].x + (endPositions[i].x - startPositions[i].x) * t);
                int newY = (int) (startPositions[i].y + (endPositions[i].y - startPositions[i].y) * t);
                state.getFrames()[i].setLocation(newX, newY);
            }

            frame[0]++;
            if (frame[0] >= Constants.ANIMATION_DURATION_FRAMES) {
                timer.stop();
                for (int i = 0; i < 8; i++) {
                    state.getFrames()[i].setLocation(endPositions[i]);
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
            startPositions[i] = state.getFrames()[i].getLocation();
        }

        Timer timer = new Timer(Constants.ANIMATION_TIMER_DELAY_MS, null);
        final int[] frame = {0};

        timer.addActionListener(e -> {
            float progress = (float) frame[0] / Constants.ELLIPSE_ANIMATION_FRAMES;
            float t = easeInOutCubic(progress);

            for (int i = 0; i < 8; i++) {
                int newX = (int) (startPositions[i].x + (ellipsePositions[i].x - startPositions[i].x) * t);
                int newY = (int) (startPositions[i].y + (ellipsePositions[i].y - startPositions[i].y) * t);
                state.getFrames()[i].setLocation(newX, newY);
            }

            frame[0]++;
            if (frame[0] >= Constants.ELLIPSE_ANIMATION_FRAMES) {
                timer.stop();
                for (int i = 0; i < 8; i++) {
                    state.getFrames()[i].setLocation(ellipsePositions[i]);
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
        Timer spinTimer = new Timer(Constants.SPIN_TIMER_DELAY_MS, null);
        final double[] angle = {0};

        spinTimer.addActionListener(e -> {
            if (state.isGameEnded()) {
                spinTimer.stop();
                return;
            }

            angle[0] += Constants.SPIN_SPEED;

            for (int i = 0; i < 8; i++) {
                double windowAngle = angle[0] + (i * 2 * Math.PI / 8);
                int x = (int) (Constants.ELLIPSE_CENTER_X + Constants.ELLIPSE_RADIUS_X * Math.cos(windowAngle)) - Constants.FRAME_SIZE / 2;
                int y = (int) (Constants.ELLIPSE_CENTER_Y + Constants.ELLIPSE_RADIUS_Y * Math.sin(windowAngle)) - Constants.FRAME_SIZE / 2;
                state.getFrames()[i].setLocation(x, y);
            }
        });

        spinTimer.start();
    }

    private void applyRandomHues() {
        try {
            URL resource = LimboKeys.class.getResource("/images/key.png");
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
        pulseFlash(label, Constants.HUE_GREEN, Constants.FLASH_DURATION_MS);
    }

    public void flashWrong(JLabel label) {
        pulseFlash(label, Constants.HUE_RED, Constants.FLASH_DURATION_MS);
    }

    private void pulseFlash(JLabel label, float hue, int durationMs) {
        try {
            URL resource = LimboKeys.class.getResource("/images/key.png");
            if (resource == null) return;

            BufferedImage original = ImageIO.read(resource);
            BufferedImage scaled = imageUtil.toBufferedImage(
                    original.getScaledInstance(Constants.FRAME_SIZE, Constants.FRAME_SIZE, Image.SCALE_SMOOTH)
            );

            BufferedImage coloredVersion = imageUtil.shiftToFullSaturation(scaled, hue);

            Timer flashTimer = new Timer(Constants.FLASH_PULSE_SPEED_MS, null);
            final long startTime = System.currentTimeMillis();
            final boolean[] isColored = {true};

            flashTimer.addActionListener(e -> {
                long elapsed = System.currentTimeMillis() - startTime;

                if (elapsed >= durationMs) {
                    flashTimer.stop();
                    label.setIcon(new ImageIcon(coloredVersion));
                    label.repaint();
                    return;
                }

                if (isColored[0]) {
                    label.setIcon(new ImageIcon(coloredVersion));
                } else {
                    label.setIcon(new ImageIcon(scaled));
                }
                isColored[0] = !isColored[0];
                label.repaint();
            });

            flashTimer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f
                ? 4 * t * t * t
                : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
}
