package com.pickle.ball;

public class Ball {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private final float radius;
    private boolean smashed;
    private final float[] trailX = new float[6];
    private final float[] trailY = new float[6];
    private int trailCount = 0;

    public Ball() {
        this(0f, 0f, 0f, 0f, 18f);
    }

    public Ball(float x, float y, float vx, float vy, float radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
    }

    public void update() {
        for (int i = trailX.length - 1; i > 0; i--) {
            trailX[i] = trailX[i - 1];
            trailY[i] = trailY[i - 1];
        }
        trailX[0] = x;
        trailY[0] = y;
        if (trailCount < trailX.length) {
            trailCount++;
        }

        x += vx;
        y += vy;
    }

    public void reset(float px, float py) {
        x = px;
        y = py;
        vx = 0f;
        vy = 0f;
        smashed = false;
        trailCount = 0;
    }

    public void serve(float direction, float baseSpeed) {
        vx = direction * baseSpeed;
        vy = baseSpeed * 0.25f * (Math.random() > 0.5 ? 1f : -1f);
        smashed = false;
        trailCount = 0;
    }

    public float speed() {
        return (float) Math.sqrt(vx * vx + vy * vy);
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getVx() { return vx; }
    public void setVx(float vx) { this.vx = vx; }
    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
    public float getRadius() { return radius; }
    public boolean isSmashed() { return smashed; }
    public void setSmashed(boolean smashed) { this.smashed = smashed; }
    public float[] getTrailX() { return trailX; }
    public float[] getTrailY() { return trailY; }
    public int getTrailCount() { return trailCount; }
}
