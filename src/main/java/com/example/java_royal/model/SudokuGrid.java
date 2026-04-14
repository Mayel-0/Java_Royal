package com.example.java_royal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuGrid {
    public static final int SIZE = 9;
    private final int[][] solution;
    private final int[][] puzzle;

    private SudokuGrid(int[][] solution, int[][] puzzle) {
        this.solution = solution;
        this.puzzle = puzzle;
    }

    public static SudokuGrid generate(Random random, int clues) {
        int[][] solution = generateSolved(random);
        int[][] puzzle = copyGrid(solution);
        int toRemove = Math.max(0, SIZE * SIZE - clues);
        while (toRemove > 0) {
            int r = random.nextInt(SIZE);
            int c = random.nextInt(SIZE);
            if (puzzle[r][c] != 0) {
                puzzle[r][c] = 0;
                toRemove--;
            }
        }
        return new SudokuGrid(solution, puzzle);
    }

    public int[][] getSolution() {
        return copyGrid(solution);
    }

    public int[][] getPuzzle() {
        return copyGrid(puzzle);
    }

    public static boolean isValidPlacement(int[][] grid, int row, int col, int value) {
        if (value == 0) {
            return true;
        }
        for (int i = 0; i < SIZE; i++) {
            if (i != col && grid[row][i] == value) {
                return false;
            }
            if (i != row && grid[i][col] == value) {
                return false;
            }
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if ((r != row || c != col) && grid[r][c] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Position> findConflicts(int[][] grid, int row, int col, int value) {
        List<Position> conflicts = new ArrayList<>();
        if (value == 0) {
            return conflicts;
        }
        for (int i = 0; i < SIZE; i++) {
            if (i != col && grid[row][i] == value) {
                conflicts.add(new Position(row, i));
            }
            if (i != row && grid[i][col] == value) {
                conflicts.add(new Position(i, col));
            }
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if ((r != row || c != col) && grid[r][c] == value) {
                    conflicts.add(new Position(r, c));
                }
            }
        }
        return conflicts;
    }

    private static int[][] generateSolved(Random random) {
        int[][] grid = new int[SIZE][SIZE];
        fillGrid(grid, 0, 0, random);
        return grid;
    }

    private static boolean fillGrid(int[][] grid, int row, int col, Random random) {
        if (row == SIZE) {
            return true;
        }
        int nextRow = col == SIZE - 1 ? row + 1 : row;
        int nextCol = col == SIZE - 1 ? 0 : col + 1;

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            numbers.add(i);
        }
        for (int i = numbers.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = numbers.get(i);
            numbers.set(i, numbers.get(j));
            numbers.set(j, temp);
        }

        for (int value : numbers) {
            if (isValidPlacement(grid, row, col, value)) {
                grid[row][col] = value;
                if (fillGrid(grid, nextRow, nextCol, random)) {
                    return true;
                }
                grid[row][col] = 0;
            }
        }
        return false;
    }

    public static int[][] copyGrid(int[][] original) {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(original[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }

    public record Position(int row, int col) {
    }
}
