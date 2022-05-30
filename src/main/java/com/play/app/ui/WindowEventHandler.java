package com.play.app.ui;

/**
 * All events you ever wanted
 */
public interface WindowEventHandler {
    default boolean onKey(int key, int scancode, int action, int mods) {
        return false;
    }

    default boolean onMouseButton(int button, int action, int mods) {
        return false;
    }

    default boolean onMouseMove(double xpos, double ypos) {
        return false;
    }

    default boolean onMouseEnter(boolean entered) {
        return false;
    }

    default boolean onScroll(double xoffset, double yoffset) {
        return false;
    }

    default boolean onWindowResize(int width, int height) {
        return false;
    }

    default boolean onChar(int c) {
        return false;
    }

}
