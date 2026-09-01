package com.pickle.ball.models;

public class Court {
    private final float left;
    private final float top;
    private final float right;
    private final float bottom;

    private static final float KITCHEN_FRAC = 0.14f;

    public Court(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float getWidth()    { return right - left; }
    public float getHeight()   { return bottom - top; }
    public float getCenterX()  { return (left + right) / 2f; }
    public float getCenterY()  { return (top + bottom) / 2f; }

    public float getNetX() { return getCenterX(); }

    // Kitchen zones
    public float getPlayerKitchenLeft()  { return getCenterX() - getWidth() * KITCHEN_FRAC; }
    public float getPlayerKitchenRight() { return getCenterX(); }
    public float getAiKitchenLeft()      { return getCenterX(); }
    public float getAiKitchenRight()     { return getCenterX() + getWidth() * KITCHEN_FRAC; }

    // Service lines
    public float getServiceLineTop()    { return top + getHeight() * 0.15f; }
    public float getServiceLineBottom() { return bottom - getHeight() * 0.15f; }

    // Getters
    public float getLeft()  { return left; }
    public float getTop()   { return top; }
    public float getRight() { return right; }
    public float getBottom() { return bottom; }
}
