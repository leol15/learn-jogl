package com.play.app.ui;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_NOT_ALLOWED_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_POINTING_HAND_CURSOR;

import com.play.app.ui.enums.CursorType;
import com.play.app.utils.WindowManager;

import org.lwjgl.glfw.GLFW;

public class Cursor {
    public static final long CURSOR_ARROW = GLFW.glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    public static final long CURSOR_IBEAM = GLFW.glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
    public static final long CURSOR_CROSSHAIR = GLFW.glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
    public static final long CURSOR_POINTING_HAND = GLFW.glfwCreateStandardCursor(GLFW_POINTING_HAND_CURSOR);
    public static final long CURSOR_NOT_ALLOWED = GLFW.glfwCreateStandardCursor(GLFW_NOT_ALLOWED_CURSOR);

    public static void setCusor(WindowManager windowManager, CursorType cursorType) {
        long id = -1;
        switch (cursorType) {
            case CURSOR_CROSSHAIR:
                id = CURSOR_CROSSHAIR;
                break;
            case CURSOR_IBEAM:
                id = CURSOR_IBEAM;
                break;
            case CURSOR_NOT_ALLOWED:
                id = CURSOR_NOT_ALLOWED;
                break;
            case CURSOR_POINTING_HAND:
                id = CURSOR_POINTING_HAND;
                break;
            default:
            case CURSOR_ARROW:
                id = CURSOR_ARROW;
                break;
        }
        GLFW.glfwSetCursor(windowManager.window, id);
    }

}
