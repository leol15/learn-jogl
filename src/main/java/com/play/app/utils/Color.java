package com.play.app.utils;

import org.joml.*;

/**
 * Everything about color?
 */
public class Color {
    private final Vector4f v = new Vector4f(1);

    public Color(float c) {
        set(c);
    }

    public Color(double c) {
        set(c);
    }

    public Color(java.awt.Color c) {
        set(c);
    }

    public Color(Vector4f c) {
        set(c);
    }

    public Color(Vector3f c) {
        set(c);
    }

    public Color(float r, float g, float b) {
        set(r, g, b);
    }

    public Color(float r, float g, float b, float a) {
        set(r, g, b, a);
    }

    public Color(double r, double g, double b, double a) {
        set(r, g, b, a);
    }

    public Vector4f get() {
        return v;
    }

    public float getR() {
        return v.x;
    }

    public float getG() {
        return v.y;
    }

    public float getB() {
        return v.z;
    }

    public float getA() {
        return v.w;
    }

    public void set(float c) {
        v.set(c, c, c);
    }

    public void set(double c) {
        v.set((float) c);
    }

    public void set(float r, float g, float b) {
        v.set(r, g, b, 1);
    }

    public void set(double r, double g, double b) {
        v.set(r, g, b, 1);
    }

    public void set(float r, float g, float b, float a) {
        v.set(r, g, b, a);
    }

    public void set(double r, double g, double b, double a) {
        v.set((float) r, (float) g, (float) b, (float) a);
    }

    public void set(final Vector4f c) {
        v.set(c);
    }

    public void set(final Vector3f c) {
        v.set(c.x, c.y, c.z, 1);
    }

    public void set(final java.awt.Color color) {
        set(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }
}
