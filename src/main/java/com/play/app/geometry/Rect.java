package com.play.app.geometry;

import org.joml.*;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class Rect {

    private float x, y, w, h;

    public Rect(final Vector2f topLeft, final Vector2f widthHeight) {
        this(topLeft.x, topLeft.y, widthHeight.x, widthHeight.y);
    }

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.w = x;
        this.h = y;
    }

    public boolean inside(final Vector2f p) {
        return inside(p.x, p.y);
    }

    public boolean inside(float x1, float y1) {
        return x <= x1 && x1 <= x + w
                && y <= y1 && y1 <= y + h;
    }
}
