package com.example.java_royal.model;

public class Card {
    private final int pairId;
    private final String imagePath;
    private boolean flipped;
    private boolean matched;

    public Card(int pairId, String imagePath) {
        this.pairId = pairId;
        this.imagePath = imagePath;
    }

    public int getPairId() {
        return pairId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}

