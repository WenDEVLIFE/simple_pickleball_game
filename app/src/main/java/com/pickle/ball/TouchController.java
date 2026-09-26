package com.pickle.ball;

import android.graphics.RectF;
import android.view.MotionEvent;

public class TouchController {
    private int activePointerId = -1;
    private float startX;
    private float lastX;
    private float lastY;
    private boolean smashRequested;
    private long smashTimestamp;

    public boolean handleTouch(MotionEvent event, Paddle paddle, float courtTop, float courtBottom) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = event.getPointerId(0);
                startX = event.getX();
                lastX = startX;
                lastY = event.getY();
                paddle.setY(Math.max(courtTop + paddle.getHeight() / 2f,
                             Math.min(courtBottom - paddle.getHeight() / 2f, event.getY())));
                return true;

            case MotionEvent.ACTION_MOVE:
                int idx = event.findPointerIndex(activePointerId);
                if (idx >= 0) {
                    float curX = event.getX(idx);
                    float curY = event.getY(idx);
                    if (curX - startX > 50f || curX - lastX > 25f) {
                        smashRequested = true;
                        smashTimestamp = System.currentTimeMillis();
                    }
                    paddle.setY(Math.max(courtTop + paddle.getHeight() / 2f,
                                 Math.min(courtBottom - paddle.getHeight() / 2f,
                                          paddle.getY() + (curY - lastY))));
                    lastX = curX;
                    lastY = curY;
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activePointerId = -1;
                return true;
        }
        return false;
    }

    public boolean isSmashActive() {
        return smashRequested && (System.currentTimeMillis() - smashTimestamp < 400L);
    }

    public void consumeSmash() {
        smashRequested = false;
    }

    public boolean isTapInRect(MotionEvent event, RectF rect) {
        return event.getActionMasked() == MotionEvent.ACTION_DOWN
                && rect != null
                && rect.contains(event.getX(), event.getY());
    }
}
