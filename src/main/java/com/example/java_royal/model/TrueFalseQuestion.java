package com.example.java_royal.model;

public class TrueFalseQuestion {
    private final int id;
    private final String questionText;
    private final boolean correctAnswer;

    public TrueFalseQuestion(int id, String questionText, boolean correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }
}

