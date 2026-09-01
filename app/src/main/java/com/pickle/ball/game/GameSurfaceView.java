package com.pickle.ball.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** SurfaceView that runs the game loop and forwards touch events to PickleBallGame. */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread gameThread;
    private volatile boolean running;
    private final PickleBallGame game = new PickleBallGame();

    public GameSurfaceView(Context context) {
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public PickleBallGame getGame() { return game; }

    // ---- surface lifecycle ----

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        game.init(getWidth(), getHeight());
        startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        game.init(w, h);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopLoop();
    }

    // ---- game loop ----

    private void startLoop() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void stopLoop() {
        running = false;
        if (gameThread != null) {
            try { gameThread.join(500); } catch (InterruptedException ignored) {}
        }
        gameThread = null;
    }

    @Override
    public void run() {
        long targetDelta = 1_000_000L / 60; // ~16.67 ms in nanos
        while (running) {
            long start = System.nanoTime();
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    synchronized (getHolder()) {
                        game.update();
                        game.render(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    try { getHolder().unlockCanvasAndPost(canvas); } catch (Exception ignored) {}
                }
            }
            long elapsed = System.nanoTime() - start;
            long sleep = (targetDelta - elapsed) / 1_000_000;
            if (sleep > 0) {
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }

    // ---- touch ----

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        game.handleTouch(event);
        return true;
    }

    public void pause() { stopLoop(); }

    public void resume() {
        if (getHolder().getSurface().isValid()) {
            startLoop();
        }
    }
}
