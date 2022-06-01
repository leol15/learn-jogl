package com.play.app.ui;

import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

/**
 * All events you ever wanted for UI
 * Uses relative coordinates 
 */
public interface UIEventHandler {

    // only this one need special handling, it seems
    default UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        return null;
    }

    default UIElement onKey(int key, int scancode, ButtonAction action, int mods) {
        return null;
    }

    // this is absolute coordinates
    default UIElement onMouseMove(double xpos, double ypos) {
        return null;
    }

    // TODO implement
    // default UIElement onMouseEnter(boolean entered) {
    //     return null;
    // }

    default UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        return null;
    }

    default UIElement onChar(int c) {
        return null;
    }

    default void onWindowResize(int width, int height) {}

}
