package com.bruhdows.limbokeys.model;

import java.awt.*;

public class Constants {
    public static final int FRAME_SIZE = 100;

    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final Dimension screenSize = toolkit.getScreenSize();

    public static final int SCREEN_CENTER_X = screenSize.width / 2;
    public static final int SCREEN_CENTER_Y = screenSize.height / 2;

    public static final int SPACING = 200;

    public static final int GRID_WIDTH = 4 * SPACING;
    public static final int GRID_HEIGHT = 2 * SPACING;
    public static final int GRID_START_X = SCREEN_CENTER_X - GRID_WIDTH / 2;
    public static final int GRID_START_Y = SCREEN_CENTER_Y - GRID_HEIGHT / 2;

    public static final int DIAGONAL_OFFSET = 40;

    public static final int SHUFFLE_DELAY_MS = 20;
    public static final int ANIMATION_DURATION_FRAMES = 20;
    public static final int ANIMATION_TIMER_DELAY_MS = 16;

    public static final int FADE_IN_DELAY_MS = 1500;
    public static final int FADE_IN_FRAMES = 60;

    public static final int HUE_SHIFT_FRAMES = 50;
    public static final int HUE_SHIFT_TIMER_DELAY_MS = 20;
    public static final float HUE_SHIFT_MAX_DEGREES = 240f;

    public static final int ELLIPSE_CENTER_X = SCREEN_CENTER_X;
    public static final int ELLIPSE_CENTER_Y = SCREEN_CENTER_Y;
    public static final int ELLIPSE_RADIUS_X = 400;
    public static final int ELLIPSE_RADIUS_Y = 250;
    public static final int ELLIPSE_ANIMATION_FRAMES = 50;

    public static final int SPIN_TIMER_DELAY_MS = 30;
    public static final double SPIN_SPEED = 0.015;

    public static final int GAME_END_DELAY_MS = 2000;

    public static final float HUE_GREEN = 120f;
    public static final float HUE_RED = 0f;

    public static final int FLASH_DURATION_MS = 10000;
    public static final int FLASH_PULSE_SPEED_MS = 200;
}
