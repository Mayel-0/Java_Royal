package com.example.java_royal.model;

import java.util.Random;

public class SudokuGame {
    private final int[][] solution;
    private final int[][] puzzle;
    private final boolean[][] fixedCells;

    public SudokuGame(Random random, int clues) {
        SudokuGrid grid = SudokuGrid.generate(random, clues);
        this.solution = grid.getSolution();
        this.puzzle = grid.getPuzzle();
        this.fixedCells = new boolean[SudokuGrid.SIZE][SudokuGrid.SIZE];
        for (int r = 0; r < SudokuGrid.SIZE; r++) {
            for (int c = 0; c < SudokuGrid.SIZE; c++) {
                fixedCells[r][c] = puzzle[r][c] != 0;
            }
        }
    }

    public int[][] getPuzzle() {
        return SudokuGrid.copyGrid(puzzle);
    }

    public int[][] getSolution() {
        return SudokuGrid.copyGrid(solution);
    }

    public boolean isFixedCell(int row, int col) {
        return fixedCells[row][col];
    }

    public boolean isCorrectValue(int row, int col, int value) {
        return solution[row][col] == value;
    }

    public boolean isValidPlacement(int[][] current, int row, int col, int value) {
        return SudokuGrid.isValidPlacement(current, row, col, value);
    }

    public boolean isSolved(int[][] current) {
        for (int r = 0; r < SudokuGrid.SIZE; r++) {
            for (int c = 0; c < SudokuGrid.SIZE; c++) {
                if (current[r][c] == 0 || current[r][c] != solution[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }
}

