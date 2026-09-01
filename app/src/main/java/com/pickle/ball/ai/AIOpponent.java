package com.pickle.ball.ai;

import com.pickle.ball.models.Ball;
import com.pickle.ball.models.Difficulty;
import com.pickle.ball.models.Paddle;

/** Simple AI controller that tracks the ball with configurable reaction delay. */
public class AIOpponent {
    private Difficulty difficulty;
    private float targetY;
    private int frameCounter;

    public AIOpponent() {
        this(Difficulty.MEDIUM);
    }

    public AIOpponent(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.targetY = 0f;
        this.frameCounter = 0;
    }

    private int getReactionFrames() {
        switch (difficulty) {
            case EASY:   return 18;
            case MEDIUM: return 9;
            case HARD:   return 3;
            default:     return 9;
        }
    }

    private float getSpeedFactor() {
        switch (difficulty) {
            case EASY:   return 0.55f;
            case MEDIUM: return 0.80f;
            case HARD:   return 1.00f;
            default:     return 0.80f;
        }
    }

    /** Call once per game-frame. Moves the AI paddle toward the ball. */
    public void update(Ball ball, Paddle paddle, float courtTop, float courtBottom) {
        frameCounter++;
        if (frameCounter >= getReactionFrames()) {
            targetY = ball.getY();
            frameCounter = 0;
        }

        float adjustedSpeed = paddle.getSpeed() * getSpeedFactor();
        float clamped = Math.max(courtTop + paddle.getHeight() / 2f,
                         Math.min(courtBottom - paddle.getHeight() / 2f, targetY));

        if (paddle.getY() < clamped) {
            paddle.setY(Math.min(paddle.getY() + adjustedSpeed, clamped));
        } else if (paddle.getY() > clamped) {
            paddle.setY(Math.max(paddle.getY() - adjustedSpeed, clamped));
        }
    }

    public void setDifficulty(Difficulty d) { this.difficulty = d; }
    public Difficulty getDifficulty() { return difficulty; }
}
