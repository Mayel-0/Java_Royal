package com.example.java_royal.pendu;

public enum HangmanDifficulty {
    FACILE("Facile", 8, 150, 12, 6, 20),
    NORMAL("Normal", 7, 120, 18, 8, 28),
    EXPERT("Expert", 6, 90, 24, 10, 35);

    private final String label;
    private final int maxLives;
    private final int roundSeconds;
    private final int correctLetterPoints;
    private final int wrongLetterPenalty;
    private final int hintPenalty;

    HangmanDifficulty(
            String label,
            int maxLives,
            int roundSeconds,
            int correctLetterPoints,
            int wrongLetterPenalty,
            int hintPenalty
    ) {
        this.label = label;
        this.maxLives = maxLives;
        this.roundSeconds = roundSeconds;
        this.correctLetterPoints = correctLetterPoints;
        this.wrongLetterPenalty = wrongLetterPenalty;
        this.hintPenalty = hintPenalty;
    }

    public String getLabel() {
        return label;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getRoundSeconds() {
        return roundSeconds;
    }

    public int getCorrectLetterPoints() {
        return correctLetterPoints;
    }

    public int getWrongLetterPenalty() {
        return wrongLetterPenalty;
    }

    public int getHintPenalty() {
        return hintPenalty;
    }

    public static HangmanDifficulty fromLabel(String label) {
        for (HangmanDifficulty difficulty : values()) {
            if (difficulty.label.equalsIgnoreCase(label)) {
                return difficulty;
            }
        }
        return NORMAL;
    }
}
