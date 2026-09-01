package com.pickle.ball.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.pickle.ball.ai.AIOpponent;
import com.pickle.ball.input.TouchController;
import com.pickle.ball.models.Ball;
import com.pickle.ball.models.Court;
import com.pickle.ball.models.Difficulty;
import com.pickle.ball.models.GamePhase;
import com.pickle.ball.models.Paddle;
import com.pickle.ball.models.ScoreBoard;

/** Central game orchestrator — owns all game objects, manages phase transitions. */
public class PickleBallGame {

    private Court court = new Court(0, 0, 0, 0);
    private final Ball ball = new Ball();
    private Paddle playerPaddle = new Paddle();
    private Paddle aiPaddle = new Paddle();
    private final ScoreBoard score = new ScoreBoard();
    private final PhysicsEngine physics = new PhysicsEngine();
    private final AIOpponent ai = new AIOpponent();
    private final TouchController touch = new TouchController();
    private final GameRenderer renderer = new GameRenderer();

    private GamePhase phase = GamePhase.MENU;
    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private String scoredBy = "";
    private long scoredTimer;
    private float screenW, screenH;
    private long serveTimer;

    private static final long SCORED_PAUSE_MS = 1400L;
    private static final long SERVE_DELAY_MS  = 700L;
    private static final float SERVE_SPEED    = 7f;

    // ---- called by SurfaceView ----

    public void init(float w, float h) {
        screenW = w;
        screenH = h;
        buildCourt();
    }

    private void buildCourt() {
        float pad = screenW * 0.06f;
        court = new Court(pad, pad * 1.6f, screenW - pad, screenH - pad * 1.6f);

        float pw = court.getWidth() * 0.022f;
        float ph = court.getHeight() * 0.12f;
        float pSpeed = court.getHeight() * 0.014f;

        playerPaddle = new Paddle(
            court.getLeft() + court.getWidth() * 0.06f,
            court.getCenterY(),
            pw, ph, pSpeed, pw / 2f
        );
        aiPaddle = new Paddle(
            court.getRight() - court.getWidth() * 0.06f - pw,
            court.getCenterY(),
            pw, ph, pSpeed, pw / 2f
        );
    }

    /** Called every frame before render. */
    public void update() {
        switch (phase) {
            case SERVING:
                if (System.currentTimeMillis() - serveTimer >= SERVE_DELAY_MS) {
                    autoServe();
                }
                break;

            case PLAYING:
                ai.update(ball, aiPaddle, court.getTop(), court.getBottom());
                Collision col = physics.tick(ball, court, playerPaddle, aiPaddle);
                handleCollision(col);
                break;

            case SCORED:
                if (System.currentTimeMillis() - scoredTimer >= SCORED_PAUSE_MS) {
                    phase = GamePhase.SERVING;
                    serveTimer = System.currentTimeMillis();
                }
                break;

            default:
                break;
        }
    }

    /** Called every frame after update. */
    public void render(Canvas canvas) {
        int w = (int) screenW;
        int h = (int) screenH;
        switch (phase) {
            case MENU:
                renderer.drawMenu(canvas, w, h, selectedDifficulty);
                break;
            case SERVING:
            case PLAYING:
                renderer.drawGame(canvas, court, ball, playerPaddle, aiPaddle, score, phase == GamePhase.SERVING, w, h);
                break;
            case SCORED:
                renderer.drawScored(canvas, w, h, scoredBy, score);
                break;
            case GAME_OVER:
                String winner = score.winner();
                renderer.drawGameOver(canvas, w, h, winner != null ? winner : "", score);
                break;
        }
    }

    /** Forward touch events. */
    public void handleTouch(MotionEvent event) {
        switch (phase) {
            case MENU:
                handleMenuTouch(event);
                break;
            case SERVING:
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    autoServe();
                }
                break;
            case PLAYING:
                touch.handleTouch(event, playerPaddle, court.getTop(), court.getBottom());
                break;
            case GAME_OVER:
                if (touch.isTapInRect(event, renderer.getPlayAgainRect())) {
                    startNewGame();
                }
                break;
            default:
                break;
        }
    }

    // ---- private helpers ----

    private void handleMenuTouch(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) return;
        if (touch.isTapInRect(event, renderer.getEasyBtnRect())) {
            selectedDifficulty = Difficulty.EASY;
        } else if (touch.isTapInRect(event, renderer.getMediumBtnRect())) {
            selectedDifficulty = Difficulty.MEDIUM;
        } else if (touch.isTapInRect(event, renderer.getHardBtnRect())) {
            selectedDifficulty = Difficulty.HARD;
        } else if (touch.isTapInRect(event, renderer.getStartBtnRect())) {
            startNewGame();
        }
    }

    private void startNewGame() {
        score.reset();
        ai.setDifficulty(selectedDifficulty);
        buildCourt();
        phase = GamePhase.SERVING;
        serveTimer = System.currentTimeMillis();
    }

    private void autoServe() {
        float dir = score.isPlayerServing() ? 1f : -1f;
        float bx = score.isPlayerServing()
                ? court.getLeft() + court.getWidth() * 0.15f
                : court.getRight() - court.getWidth() * 0.15f;
        float by = court.getCenterY() + court.getHeight() * ((float) Math.random() - 0.5f) * 0.3f;
        ball.reset(bx, by);
        ball.serve(dir, SERVE_SPEED);
        phase = GamePhase.PLAYING;
    }

    private void handleCollision(Collision col) {
        switch (col) {
            case PLAYER_SCORES:
                awardPoint("Player");
                break;
            case AI_SCORES:
                awardPoint("AI");
                break;
            default:
                break;
        }
    }

    private void awardPoint(String winner) {
        if ("Player".equals(winner)) {
            score.playerWinsRally();
        } else {
            score.aiWinsRally();
        }
        scoredBy = winner;
        scoredTimer = System.currentTimeMillis();
        phase = score.isGameOver() ? GamePhase.GAME_OVER : GamePhase.SCORED;
    }
}
