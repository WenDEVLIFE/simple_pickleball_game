package com.pickle.ball.game;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.pickle.ball.models.Ball;
import com.pickle.ball.models.Court;
import com.pickle.ball.models.Difficulty;
import com.pickle.ball.models.Paddle;
import com.pickle.ball.models.ScoreBoard;

/** Draws every frame of the game onto a Canvas. */
public class GameRenderer {

    // Colour palette
    private static final String CLR_BG       = "#121212";
    private static final String CLR_COURT    = "#1B5E20";
    private static final String CLR_KITCHEN  = "#2E7D32";
    private static final String CLR_LINES    = "#B9F6CA";
    private static final String CLR_PLAYER   = "#42A5F5";
    private static final String CLR_AI       = "#EF5350";
    private static final String CLR_BALL     = "#FFEB3B";
    private static final String CLR_BALL_GLOW= "#FFF176";
    private static final String CLR_ACCENT   = "#00E676";
    private static final String CLR_BUTTON   = "#1B5E20";
    private static final String CLR_BUTTON_SEL = "#2E7D32";

    // Reusable paints
    private final Paint bgPaint, courtPaint, kitchenPaint, linePaint, netPaint;
    private final Paint playerPPaint, aiPPaint, ballPaint, ballGlow;
    private final Paint titlePaint, subtitlePaint, scorePaint, hudPaint, serveHintPaint;
    private final Paint btnTextPaint, btnPaint, btnSelPaint, overlayPaint, courtStroke;

    // Button rects (set during drawMenu)
    private RectF easyBtnRect   = new RectF();
    private RectF mediumBtnRect = new RectF();
    private RectF hardBtnRect   = new RectF();
    private RectF startBtnRect  = new RectF();
    private RectF playAgainRect = new RectF();

    public GameRenderer() {
        bgPaint      = makePaint(Color.parseColor(CLR_BG), false);
        courtPaint   = makePaint(Color.parseColor(CLR_COURT), true);
        kitchenPaint = makePaint(Color.parseColor(CLR_KITCHEN), true);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor(CLR_LINES));
        linePaint.setStrokeWidth(3f);
        linePaint.setStyle(Paint.Style.STROKE);

        netPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        netPaint.setColor(Color.WHITE);
        netPaint.setStrokeWidth(5f);
        netPaint.setStyle(Paint.Style.STROKE);
        netPaint.setPathEffect(new DashPathEffect(new float[]{14f, 10f}, 0f));

        playerPPaint = makePaint(Color.parseColor(CLR_PLAYER), true);
        aiPPaint     = makePaint(Color.parseColor(CLR_AI), true);
        ballPaint    = makePaint(Color.parseColor(CLR_BALL), true);

        ballGlow = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballGlow.setColor(Color.parseColor(CLR_BALL_GLOW));
        ballGlow.setMaskFilter(new BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL));

        titlePaint     = makeTextPaint(72f, Color.WHITE, Paint.Align.CENTER, Typeface.DEFAULT_BOLD);
        subtitlePaint  = makeTextPaint(36f, Color.parseColor(CLR_ACCENT), Paint.Align.CENTER, null);
        scorePaint     = makeTextPaint(54f, Color.WHITE, Paint.Align.CENTER, Typeface.DEFAULT_BOLD);
        hudPaint       = makeTextPaint(28f, Color.parseColor(CLR_LINES), Paint.Align.CENTER, null);
        serveHintPaint = makeTextPaint(30f, Color.parseColor(CLR_BALL), Paint.Align.CENTER, null);
        btnTextPaint   = makeTextPaint(34f, Color.WHITE, Paint.Align.CENTER, Typeface.DEFAULT_BOLD);

        btnPaint  = makePaint(Color.parseColor(CLR_BUTTON), true);
        btnSelPaint = makePaint(Color.parseColor(CLR_BUTTON_SEL), true);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(160, 0, 0, 0));

        courtStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        courtStroke.setColor(Color.parseColor(CLR_LINES));
        courtStroke.setStrokeWidth(4f);
        courtStroke.setStyle(Paint.Style.STROKE);
    }

    // =========================== MENU ===========================

    public void drawMenu(Canvas canvas, int w, int h, Difficulty selectedDifficulty) {
        canvas.drawColor(Color.parseColor(CLR_BG));

        canvas.drawText("PICKLEBALL", w / 2f, h * 0.30f, titlePaint);
        canvas.drawText("Player vs AI", w / 2f, h * 0.37f, subtitlePaint);

        Paint labelPaint = makeTextPaint(30f, Color.parseColor(CLR_LINES), Paint.Align.CENTER, null);
        canvas.drawText("SELECT DIFFICULTY", w / 2f, h * 0.46f, labelPaint);

        float bw = w * 0.55f;
        float bh = 64f;
        float cx = w / 2f;
        float startY = h * 0.52f;
        float gap = 80f;

        Difficulty[] diffs = Difficulty.values();
        for (int i = 0; i < diffs.length; i++) {
            float top = startY + i * gap;
            RectF rect = new RectF(cx - bw / 2, top, cx + bw / 2, top + bh);
            Paint p = (diffs[i] == selectedDifficulty) ? btnSelPaint : btnPaint;
            canvas.drawRoundRect(rect, 16f, 16f, p);
            canvas.drawText(diffs[i].getLabel().toUpperCase(), cx, top + bh / 2 + 12f, btnTextPaint);

            switch (i) {
                case 0: easyBtnRect = rect;   break;
                case 1: mediumBtnRect = rect; break;
                case 2: hardBtnRect = rect;   break;
            }
        }

        float sy = startY + diffs.length * gap + 40f;
        startBtnRect = new RectF(cx - bw / 2, sy, cx + bw / 2, sy + bh + 10f);
        canvas.drawRoundRect(startBtnRect, 16f, 16f, btnSelPaint);
        canvas.drawText("START GAME", cx, sy + (bh + 10f) / 2 + 12f, btnTextPaint);
    }

    // =========================== GAME ===========================

    public void drawGame(Canvas canvas, Court court, Ball ball,
                         Paddle playerPaddle, Paddle aiPaddle,
                         ScoreBoard score, boolean serving, int w, int h) {
        canvas.drawColor(Color.parseColor(CLR_BG));

        // Court fill
        RectF courtRect = new RectF(court.getLeft(), court.getTop(), court.getRight(), court.getBottom());
        canvas.drawRoundRect(courtRect, 12f, 12f, courtPaint);

        // Kitchen zones
        canvas.drawRect(court.getPlayerKitchenLeft(), court.getTop(), court.getPlayerKitchenRight(), court.getBottom(), kitchenPaint);
        canvas.drawRect(court.getAiKitchenLeft(), court.getTop(), court.getAiKitchenRight(), court.getBottom(), kitchenPaint);

        // Court boundary
        canvas.drawRect(court.getLeft(), court.getTop(), court.getRight(), court.getBottom(), courtStroke);

        // Center service line (dashed)
        Paint svcLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        svcLinePaint.setColor(Color.parseColor(CLR_LINES));
        svcLinePaint.setStrokeWidth(2f);
        svcLinePaint.setStyle(Paint.Style.STROKE);
        svcLinePaint.setPathEffect(new DashPathEffect(new float[]{8f, 8f}, 0f));
        canvas.drawLine(court.getLeft(), court.getCenterY(), court.getRight(), court.getCenterY(), svcLinePaint);

        // Kitchen lines
        canvas.drawLine(court.getPlayerKitchenLeft(), court.getTop(), court.getPlayerKitchenLeft(), court.getBottom(), linePaint);
        canvas.drawLine(court.getAiKitchenRight(), court.getTop(), court.getAiKitchenRight(), court.getBottom(), linePaint);

        // Net
        canvas.drawLine(court.getNetX(), court.getTop(), court.getNetX(), court.getBottom(), netPaint);

        // Paddles
        drawPaddle(canvas, playerPaddle, playerPPaint);
        drawPaddle(canvas, aiPaddle, aiPPaint);

        // Ball
        if (ball.getVx() != 0f || ball.getVy() != 0f) {
            canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius() + 4f, ballGlow);
            canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius(), ballPaint);
        }

        // HUD — score
        canvas.drawText(score.scoreText(), w / 2f, court.getTop() - 20f, scorePaint);

        // Rally count
        canvas.drawText("Rally: " + score.getRallyCount(), w / 2f, court.getTop() - 50f, hudPaint);

        // Server indicator
        String serverLabel = score.isPlayerServing() ? "Blue serves" : "Red serves";
        canvas.drawText(serverLabel, w / 2f, court.getBottom() + 40f, hudPaint);

        // Side labels
        canvas.drawText("YOU", court.getLeft() + court.getWidth() * 0.08f, court.getBottom() + 70f, hudPaint);
        canvas.drawText("AI", court.getRight() - court.getWidth() * 0.08f, court.getBottom() + 70f, hudPaint);

        // Serve hint
        if (serving) {
            canvas.drawRect(0f, h * 0.42f, w, h * 0.55f, overlayPaint);
            canvas.drawText("TAP TO SERVE", w / 2f, h * 0.50f, serveHintPaint);
        }
    }

    private void drawPaddle(Canvas canvas, Paddle p, Paint paint) {
        canvas.drawRoundRect(p.rect(), p.getCornerRadius(), p.getCornerRadius(), paint);
    }

    // =========================== SCORED ===========================

    public void drawScored(Canvas canvas, int w, int h, String scorer, ScoreBoard score) {
        canvas.drawRect(0f, 0f, w, h, overlayPaint);
        canvas.drawText("POINT!", w / 2f, h * 0.42f, scorePaint);
        canvas.drawText(scorer + " scores", w / 2f, h * 0.50f, subtitlePaint);
        canvas.drawText(score.scoreText(), w / 2f, h * 0.58f, scorePaint);
    }

    // =========================== GAME OVER ===========================

    public void drawGameOver(Canvas canvas, int w, int h, String winner, ScoreBoard score) {
        canvas.drawRect(0f, 0f, w, h, overlayPaint);
        canvas.drawText("GAME OVER", w / 2f, h * 0.30f, titlePaint);
        canvas.drawText(winner + " wins!", w / 2f, h * 0.42f, scorePaint);
        canvas.drawText(score.scoreText(), w / 2f, h * 0.52f, subtitlePaint);

        float bw = w * 0.50f;
        float bh = 68f;
        playAgainRect = new RectF(w / 2f - bw / 2, h * 0.64f, w / 2f + bw / 2, h * 0.64f + bh);
        canvas.drawRoundRect(playAgainRect, 16f, 16f, btnSelPaint);
        canvas.drawText("PLAY AGAIN", w / 2f, h * 0.64f + bh / 2 + 12f, btnTextPaint);
    }

    // ---- accessors for button rects ----
    public RectF getEasyBtnRect()   { return easyBtnRect; }
    public RectF getMediumBtnRect() { return mediumBtnRect; }
    public RectF getHardBtnRect()   { return hardBtnRect; }
    public RectF getStartBtnRect()  { return startBtnRect; }
    public RectF getPlayAgainRect() { return playAgainRect; }

    // ---- paint helpers ----
    private Paint makePaint(int color, boolean antiAlias) {
        Paint p = new Paint(antiAlias ? Paint.ANTI_ALIAS_FLAG : 0);
        p.setColor(color);
        return p;
    }

    private Paint makeTextPaint(float size, int color, Paint.Align align, Typeface tf) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(size);
        p.setColor(color);
        p.setTextAlign(align);
        if (tf != null) p.setTypeface(tf);
        return p;
    }
}
