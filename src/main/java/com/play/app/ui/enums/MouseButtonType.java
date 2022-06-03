package com.play.app.ui.enums;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MouseButtonType {
    NONE(-1), LEFT(GLFW_MOUSE_BUTTON_1), RIGHT(GLFW_MOUSE_BUTTON_2), MIDDLE(GLFW_MOUSE_BUTTON_3);

    int GLFW_ID;

    public static MouseButtonType fromInt(int id) {
        switch (id) {
            case -1:
                return NONE;
            case GLFW_MOUSE_BUTTON_1:
                return LEFT;
            case GLFW_MOUSE_BUTTON_2:
                return RIGHT;
            case GLFW_MOUSE_BUTTON_3:
                return MIDDLE;
        }
        return null;
    }

}
