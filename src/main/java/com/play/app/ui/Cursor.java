package com.play.app.ui;

import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class Cursor {
    public static final long CURSOR_ARROW = GLFW.glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    public static final long CURSOR_IBEAM = GLFW.glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
    public static final long CURSOR_CROSSHAIR = GLFW.glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
    public static final long CURSOR_POINTING_HAND = GLFW.glfwCreateStandardCursor(GLFW_POINTING_HAND_CURSOR);
    public static final long CURSOR_NOT_ALLOWED = GLFW.glfwCreateStandardCursor(GLFW_NOT_ALLOWED_CURSOR);

    public static void setCusor(long window, long id) {
        GLFW.glfwSetCursor(window, id);
    }
}
