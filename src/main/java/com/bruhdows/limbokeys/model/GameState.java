package com.bruhdows.limbokeys.model;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameState {
    private final JLabel[] labels = new JLabel[8];
    private final Point[] positions = new Point[8];
    private final float[] keyHues = new float[8];
    private int correctKeyIndex;
    private boolean gameEnded = false;

    private int[][] shufflePatterns = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {1, 0, 3, 2, 5, 4, 7, 6},
            {0, 2, 1, 4, 3, 6, 5, 7},
            {2, 0, 3, 1, 6, 4, 7, 5},
            {1, 3, 0, 2, 5, 7, 4, 6},
            {4, 5, 6, 7, 0, 1, 2, 3},
            {0, 4, 1, 5, 2, 6, 3, 7},
            {4, 0, 5, 1, 6, 2, 7, 3},
            {2, 3, 0, 1, 6, 7, 4, 5},
            {1, 0, 3, 2, 4, 5, 6, 7},
            {6, 2, 0, 4, 7, 3, 1, 5},
            {2, 6, 4, 0, 3, 7, 5, 1},
            {7, 6, 5, 4, 3, 2, 1, 0},
            {3, 2, 1, 0, 7, 6, 5, 4},
            {5, 1, 6, 2, 3, 7, 0, 4},
            {3, 1, 2, 0, 7, 5, 6, 4},
            {4, 1, 2, 7, 0, 5, 6, 3},
            {1, 2, 3, 0, 5, 6, 7, 4},
            {3, 0, 1, 2, 7, 4, 5, 6},
            {2, 0, 6, 4, 1, 3, 5, 7},
            {0, 4, 1, 5, 3, 7, 2, 6},
            {0, 2, 4, 6, 1, 3, 5, 7},
            {3, 2, 1, 0, 4, 5, 6, 7},
            {0, 1, 2, 3, 7, 6, 5, 4},
            {4, 0, 5, 1, 6, 2, 7, 3},
            {6, 7, 2, 3, 4, 5, 0, 1},
            {1, 4, 3, 6, 5, 0, 7, 2},
            {5, 7, 1, 3, 2, 4, 0, 6}
    };

    private final Random random = new Random();

    public GameState() {
        shufflePatterns();
        initializeKeyHues();
    }

    private void shufflePatterns() {
        List<int[]> patternList = new ArrayList<>(Arrays.asList(shufflePatterns));
        int[] firstPattern = patternList.remove(0);
        Collections.shuffle(patternList, random);
        patternList.add(0, firstPattern);
        shufflePatterns = patternList.toArray(new int[0][]);
    }

    private void initializeKeyHues() {
        for (int i = 0; i < 8; i++) {
            keyHues[i] = random.nextFloat() * 360f;
        }
    }

    public void setCorrectKeyIndex(int index) {
        this.correctKeyIndex = index;
    }

    public int getCorrectKeyIndex() {
        return correctKeyIndex;
    }

    public int generateRandomKeyIndex() {
        return random.nextInt(8);
    }

    public JLabel[] getLabels() {
        return labels;
    }

    public Point[] getPositions() {
        return positions;
    }

    public float[] getKeyHues() {
        return keyHues;
    }

    public int[][] getShufflePatterns() {
        return shufflePatterns;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean ended) {
        this.gameEnded = ended;
    }
}
