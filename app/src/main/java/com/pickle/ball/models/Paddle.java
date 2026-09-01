package com.pickle.ball.models;

import android.graphics.RectF;

public class Paddle {
    private float x;
    private float y;
    private final float width;
    private final float height;
    private final float speed;
    private final float cornerRadius;

    public Paddle() {
        this(0f, 0f, 18f, 140f, 14f, 10f);
    }

    public Paddle(float x, float y, float width, float height, float speed, float cornerRadius) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.cornerRadius = cornerRadius;
    }

    public float getTop()    { return y - height / 2f; }
    public float getBottom() { return y + height / 2f; }
    public float getLeft()   { return x; }
    public float getRight()  { return x + width; }

    public RectF rect() {
        return new RectF(getLeft(), getTop(), getRight(), getBottom());
    }

    public void clampY(float courtTop, float courtBottom) {
        y = Math.max(courtTop + height / 2f, Math.min(courtBottom - height / 2f, y));
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getSpeed() { return speed; }
    public float getCornerRadius() { return cornerRadius; }
}
