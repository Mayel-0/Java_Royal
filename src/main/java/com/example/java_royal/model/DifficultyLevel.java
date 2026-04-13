package com.example.java_royal.model;

public enum DifficultyLevel {
    EASY("Facile", 4, 3, 6),
    MEDIUM("Moyen", 5, 4, 10),
    HARD("Difficile", 6, 4, 12);

    private final String label;
    private final int columns;
    private final int rows;
    private final int pairs;

    DifficultyLevel(String label, int columns, int rows, int pairs) {
        this.label = label;
        this.columns = columns;
        this.rows = rows;
        this.pairs = pairs;
    }

    public String getLabel() {
        return label;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getPairs() {
        return pairs;
    }

    @Override
    public String toString() {
        return label;
    }
}

