package com.pickle.ball.models;

public class Ball {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private final float radius;

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
        x += vx;
        y += vy;
    }

    public void reset(float px, float py) {
        x = px;
        y = py;
        vx = 0f;
        vy = 0f;
    }

    public void serve(float direction, float baseSpeed) {
        vx = direction * baseSpeed;
        vy = baseSpeed * 0.25f * (Math.random() > 0.5 ? 1f : -1f);
    }

    public float speed() {
        return (float) Math.sqrt(vx * vx + vy * vy);
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getVx() { return vx; }
    public void setVx(float vx) { this.vx = vx; }
    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
    public float getRadius() { return radius; }
}
