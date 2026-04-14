package com.example.java_royal.pendu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class HangmanGameEngine {

    public enum GuessResult {
        INVALID,
        ALREADY_USED,
        CORRECT,
        INCORRECT,
        ROUND_WON,
        ROUND_LOST
    }

    private final HangmanDifficulty difficulty;
    private final String targetWord;
    private final String category;
    private final String hint;
    private final char[] revealed;

    private final Set<Character> guessedLetters = new LinkedHashSet<>();
    private final Set<Character> wrongLetters = new LinkedHashSet<>();

    private int remainingLives;
    private int combo;
    private int score;
    private boolean hintUsed;
    private boolean finished;
    private boolean won;

    public HangmanGameEngine(HangmanWord word, HangmanDifficulty difficulty) {
        this.difficulty = difficulty;
        this.targetWord = normalize(word.value());
        this.category = word.category();
        this.hint = word.hint();
        this.remainingLives = difficulty.getMaxLives();
        this.revealed = initMask(targetWord);
    }

    public GuessResult guess(char rawLetter) {
        if (finished) {
            return won ? GuessResult.ROUND_WON : GuessResult.ROUND_LOST;
        }

        char letter = Character.toUpperCase(rawLetter);
        if (letter < 'A' || letter > 'Z') {
            return GuessResult.INVALID;
        }

        if (guessedLetters.contains(letter)) {
            return GuessResult.ALREADY_USED;
        }

        guessedLetters.add(letter);

        boolean found = reveal(letter);
        if (found) {
            combo++;
            score += difficulty.getCorrectLetterPoints() + (combo * 2);
            if (isSolved()) {
                finished = true;
                won = true;
                score += 45;
                return GuessResult.ROUND_WON;
            }
            return GuessResult.CORRECT;
        }

        combo = 0;
        remainingLives--;
        wrongLetters.add(letter);
        score = Math.max(0, score - difficulty.getWrongLetterPenalty());

        if (remainingLives <= 0) {
            finished = true;
            won = false;
            return GuessResult.ROUND_LOST;
        }

        return GuessResult.INCORRECT;
    }

    public boolean useHint(Random random) {
        if (hintUsed || finished) {
            return false;
        }

        List<Character> hiddenLetters = new ArrayList<>();
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            if (isPlayableLetter(c) && revealed[i] == '_') {
                hiddenLetters.add(c);
            }
        }

        if (hiddenLetters.isEmpty()) {
            return false;
        }

        char revealedLetter = hiddenLetters.get(random.nextInt(hiddenLetters.size()));
        guessedLetters.add(revealedLetter);
        reveal(revealedLetter);
        hintUsed = true;
        combo = 0;
        score = Math.max(0, score - difficulty.getHintPenalty());

        if (isSolved()) {
            finished = true;
            won = true;
            score += 30;
        }

        return true;
    }

    private boolean reveal(char letter) {
        boolean found = false;
        for (int i = 0; i < targetWord.length(); i++) {
            if (targetWord.charAt(i) == letter) {
                revealed[i] = letter;
                found = true;
            }
        }
        return found;
    }

    private boolean isSolved() {
        for (char c : revealed) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    private static char[] initMask(String word) {
        char[] mask = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            mask[i] = isPlayableLetter(c) ? '_' : c;
        }
        return mask;
    }

    private static boolean isPlayableLetter(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static String normalize(String rawWord) {
        String upper = rawWord == null ? "" : rawWord.toUpperCase(Locale.ROOT);
        String normalized = Normalizer.normalize(upper, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}+", "");
    }

    public String getDisplayWord() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < revealed.length; i++) {
            builder.append(revealed[i]);
            if (i < revealed.length - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    public String getTargetWord() {
        return targetWord;
    }

    public String getCategory() {
        return category;
    }

    public String getHint() {
        return hint;
    }

    public String getWrongLettersDisplay() {
        if (wrongLetters.isEmpty()) {
            return "Aucune";
        }
        return wrongLetters.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + " - " + b)
                .orElse("Aucune");
    }

    public boolean isLetterUsed(char c) {
        return guessedLetters.contains(Character.toUpperCase(c));
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public int getMaxLives() {
        return difficulty.getMaxLives();
    }

    public int getCombo() {
        return combo;
    }

    public int getScore() {
        return score;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }
}
