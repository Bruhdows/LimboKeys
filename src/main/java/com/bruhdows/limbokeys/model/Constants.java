package com.bruhdows.limbokeys.model;

public class Constants {
    public static final int FRAME_SIZE = 100;
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    public static final int SCREEN_CENTER_X = WINDOW_WIDTH / 2;
    public static final int SCREEN_CENTER_Y = WINDOW_HEIGHT / 2;

    public static final int SPACING = 200;
    public static final int GRID_WIDTH = 4 * SPACING;
    public static final int GRID_HEIGHT = 2 * SPACING;
    public static final int GRID_START_X = SCREEN_CENTER_X - GRID_WIDTH / 2 + 50;
    public static final int GRID_START_Y = SCREEN_CENTER_Y - GRID_HEIGHT / 2;

    public static final int GAME_START_DELAY_MS = 2800;
    public static final int SHUFFLE_DELAY_MS = 20;
    public static final int ANIMATION_DURATION_FRAMES = 17;
    public static final int ANIMATION_TIMER_DELAY_MS = 16;

    public static final int HUE_SHIFT_FRAMES = 50;
    public static final int HUE_SHIFT_TIMER_DELAY_MS = 20;
    public static final float HUE_SHIFT_MAX_DEGREES = 240f;

    public static final int ELLIPSE_CENTER_X = SCREEN_CENTER_X;
    public static final int ELLIPSE_CENTER_Y = SCREEN_CENTER_Y;
    public static final int ELLIPSE_RADIUS_X = 400;
    public static final int ELLIPSE_RADIUS_Y = 250;
    public static final int ELLIPSE_ANIMATION_FRAMES = 50;

    public static final int SPIN_TIMER_DELAY_MS = 16;
    public static final double SPIN_SPEED = 0.5;

    public static final int GAME_END_DELAY_MS = 2000;
    public static final float HUE_GREEN = 120f;
    public static final float HUE_RED = 0f;
    public static final int FLASH_DURATION_MS = 10000;
    public static final int FLASH_PULSE_SPEED_MS = 200;
}
