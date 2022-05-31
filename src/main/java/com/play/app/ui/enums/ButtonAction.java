package com.play.app.ui.enums;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ButtonAction {
    PRESS(GLFW_PRESS), RELEASE(GLFW_RELEASE), REPEAT(GLFW_REPEAT);

    final int GLFW_ID;

    public static ButtonAction fromInt(int id) {
        switch (id) {
            case GLFW_PRESS:
                return PRESS;
            case GLFW_RELEASE:
                return RELEASE;
            case GLFW_REPEAT:
                return REPEAT;
        }
        return null;
    }
}
