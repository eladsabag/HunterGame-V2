package com.example.huntergame_v2.objects;

public class GameManager {

    private int score = 0;
    private int lives = 3;

    public GameManager() {};

    public void setScore() { this.score = 0; }

    public int getScore() {
        return score;
    }

    public void addToScore() {
        score += 1;
    }

    public void addCoinToScore() { score += 10; }

    public int getLives() {
        return lives;
    }

    public void reduceLives() {
        lives--;
    }

    public boolean isDead() {
        return lives <= 0;
    }
}
