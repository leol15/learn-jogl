package com.play.app.geometry;

import org.joml.*;

public class Rect {

    private Vector2f topLeft;
    private Vector2f widthHeight;

    public Rect(final Vector2f topLeft, final Vector2f widthHeight) {
        this.topLeft = new Vector2f(topLeft);
        this.widthHeight = new Vector2f(widthHeight);
    }

    public Rect(float x, float y, float width, float height) {
        this.topLeft = new Vector2f(x, y);
        this.widthHeight = new Vector2f(width, height);
    }

    public boolean inside(final Vector2f p) {
        return inside(p.x, p.y);
    }

    public boolean inside(float x, float y) {
        return x >= topLeft.x && x <= topLeft.x + widthHeight.x
                && y >= topLeft.y && y <= topLeft.y + widthHeight.y;
    }
}
