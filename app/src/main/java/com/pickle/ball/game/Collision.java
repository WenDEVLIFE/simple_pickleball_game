package com.pickle.ball.game;

/** What the ball hit in a given frame. */
public enum Collision {
    NONE,
    WALL,
    PLAYER_PADDLE,
    AI_PADDLE,
    PLAYER_SCORES,
    AI_SCORES
}
