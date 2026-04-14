package com.example.javaroyal.games.t_or_f;

public class TrueFalseQuestion {
    private String text;
    private boolean answer;

    public TrueFalseQuestion(String text, boolean answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public boolean isAnswer() {
        return answer;
    }
}

