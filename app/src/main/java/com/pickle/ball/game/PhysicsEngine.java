package com.pickle.ball.game;

import com.pickle.ball.models.Ball;
import com.pickle.ball.models.Court;
import com.pickle.ball.models.Paddle;

/** Handles ball movement, wall bounces, paddle collisions, and scoring. */
public class PhysicsEngine {

    public static final float SPEED_INCREMENT  = 0.4f;
    public static final float MAX_SPEED        = 22f;
    public static final float SERVE_SPEED      = 7f;
    public static final float ANGLE_DEFLECTION = 50f;

    /** Returns what the ball hit this frame. */
    public Collision tick(Ball ball, Court court, Paddle playerPaddle, Paddle aiPaddle) {
        ball.update();

        // --- top / bottom walls ---
        if (ball.getY() - ball.getRadius() < court.getTop()) {
            ball.setY(court.getTop() + ball.getRadius());
            ball.setVy(Math.abs(ball.getVy()));
            return Collision.WALL;
        }
        if (ball.getY() + ball.getRadius() > court.getBottom()) {
            ball.setY(court.getBottom() - ball.getRadius());
            ball.setVy(-Math.abs(ball.getVy()));
            return Collision.WALL;
        }

        // --- player paddle (left side) ---
        if (ball.getVx() < 0f && checkPaddleCollision(ball, playerPaddle, true)) {
            deflect(ball, playerPaddle, true);
            return Collision.PLAYER_PADDLE;
        }

        // --- AI paddle (right side) ---
        if (ball.getVx() > 0f && checkPaddleCollision(ball, aiPaddle, false)) {
            deflect(ball, aiPaddle, false);
            return Collision.AI_PADDLE;
        }

        // --- scoring (ball past paddle) ---
        if (ball.getX() - ball.getRadius() < court.getLeft())  return Collision.AI_SCORES;
        if (ball.getX() + ball.getRadius() > court.getRight()) return Collision.PLAYER_SCORES;

        return Collision.NONE;
    }

    private boolean checkPaddleCollision(Ball ball, Paddle paddle, boolean fromLeft) {
        float px = fromLeft ? paddle.getRight() : paddle.getLeft();
        boolean overlapX;
        if (fromLeft) {
            overlapX = ball.getX() - ball.getRadius() < px && ball.getX() + ball.getRadius() > paddle.getLeft();
        } else {
            overlapX = ball.getX() + ball.getRadius() > px && ball.getX() - ball.getRadius() < paddle.getRight();
        }
        return overlapX
                && ball.getY() + ball.getRadius() > paddle.getTop()
                && ball.getY() - ball.getRadius() < paddle.getBottom();
    }

    private void deflect(Ball ball, Paddle paddle, boolean directionRight) {
        // Reposition ball outside paddle
        ball.setX(directionRight ? paddle.getRight() + ball.getRadius() : paddle.getLeft() - ball.getRadius());

        // Angle based on hit position (-1..+1)
        float hitRatio = Math.max(-1f, Math.min(1f, (ball.getY() - paddle.getY()) / (paddle.getHeight() / 2f)));
        double angleRad = Math.toRadians(hitRatio * ANGLE_DEFLECTION);

        float currentSpeed = Math.max(4f, Math.min(MAX_SPEED, ball.speed())) + SPEED_INCREMENT;
        float cappedSpeed = Math.min(currentSpeed, MAX_SPEED);

        ball.setVx((directionRight ? 1 : -1) * cappedSpeed * (float) Math.cos(angleRad));
        ball.setVy(cappedSpeed * (float) Math.sin(angleRad));
    }
}
