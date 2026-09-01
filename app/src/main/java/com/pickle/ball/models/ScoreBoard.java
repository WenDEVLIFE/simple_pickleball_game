package com.pickle.ball.models;

/** Tracks the current score and serving side. */
public class ScoreBoard {
    private int playerScore;
    private int aiScore;
    private final int winScore;
    private boolean playerServing;
    private int rallyCount;

    public ScoreBoard() {
        this(0, 0, 11, true, 0);
    }

    public ScoreBoard(int playerScore, int aiScore, int winScore, boolean playerServing, int rallyCount) {
        this.playerScore = playerScore;
        this.aiScore = aiScore;
        this.winScore = winScore;
        this.playerServing = playerServing;
        this.rallyCount = rallyCount;
    }

    public void playerWinsRally() { playerScore++; rallyCount++; }
    public void aiWinsRally()     { aiScore++;     rallyCount++; }

    public boolean isGameOver() {
        int diff = playerScore - aiScore;
        return (playerScore >= winScore && diff >= 2) ||
               (aiScore >= winScore && -diff >= 2);
    }

    public String winner() {
        if (!isGameOver()) return null;
        return playerScore > aiScore ? "Player" : "AI";
    }

    public void switchServer() { playerServing = !playerServing; }

    public void reset() {
        playerScore = 0;
        aiScore = 0;
        playerServing = true;
        rallyCount = 0;
    }

    public String scoreText() {
        return playerScore + "  –  " + aiScore;
    }

    // Getters and setters
    public int getPlayerScore() { return playerScore; }
    public int getAiScore() { return aiScore; }
    public int getWinScore() { return winScore; }
    public boolean isPlayerServing() { return playerServing; }
    public void setPlayerServing(boolean playerServing) { this.playerServing = playerServing; }
    public int getRallyCount() { return rallyCount; }
}
