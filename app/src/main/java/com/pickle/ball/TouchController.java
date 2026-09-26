package com.pickle.ball;

import android.graphics.RectF;
import android.view.MotionEvent;

public class TouchController {
    private int activePointerId = -1;
    private float lastY;

    public boolean handleTouch(MotionEvent event, Paddle paddle, float courtTop, float courtBottom) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = event.getPointerId(0);
                lastY = event.getY();
                paddle.setY(Math.max(courtTop + paddle.getHeight() / 2f,
                             Math.min(courtBottom - paddle.getHeight() / 2f, event.getY())));
                return true;

            case MotionEvent.ACTION_MOVE:
                int idx = event.findPointerIndex(activePointerId);
                if (idx >= 0) {
                    float curY = event.getY(idx);
                    paddle.setY(Math.max(courtTop + paddle.getHeight() / 2f,
                                 Math.min(courtBottom - paddle.getHeight() / 2f,
                                          paddle.getY() + (curY - lastY))));
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

    public boolean isTapInRect(MotionEvent event, RectF rect) {
        return event.getActionMasked() == MotionEvent.ACTION_DOWN
                && rect != null
                && rect.contains(event.getX(), event.getY());
    }
}
