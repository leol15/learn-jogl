package com.play.app.utils;

public class Func {

    // conver from screen space to openGL coordinates
    // TODO: use a matrix
    public static float toGLPosition(float value, float max, boolean isY) {
        if (isY) {
            return (max - value) * 2 / max - 1.0f;
        } else {
            return value * 2 / max - 1.0f;
        }
    }
}
