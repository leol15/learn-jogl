package com.play.app.ui;

import java.util.*;

import com.play.app.basics.Drawable;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

/**
 * Handle alllllll the UIs
 */
public class UIManager implements Drawable, WindowEventHandler {

    private final List<UIBase> elements = new ArrayList<>();

    public UIManager(WindowManager windowManager) {

        // add events
        windowManager.addMouseButtonCallback(Layer.UI,
                (window, button, action, mods) -> onMouseButton(button, action, mods));
        windowManager.addScrollCallback(Layer.UI, (window, xoffset, yoffset) -> onScroll(xoffset, yoffset));
        windowManager.addCursorEnterCallback(Layer.UI, (window, entered) -> onMouseEnter(entered));
        windowManager.addCursorPosCallback(Layer.UI, (window, xpos, ypos) -> onMouseMove(xpos, ypos));
        windowManager.addWindowSizeCallback(Layer.UI, (window, width, height) -> onWindowResize(width, height));
        windowManager.addKeyCallback(Layer.UI,
                (window, key, scancode, action, mods) -> onKey(key, scancode, action, mods));
        windowManager.addCharCallback(Layer.UI, (window, c) -> onChar(c));
    }

    @Override
    public void draw() {
        for (final UIBase e : elements) {
            e.show();
        }
    }

    @Override
    public boolean onChar(int c) {
        return WindowEventHandler.super.onChar(c);
    }

    @Override
    public boolean onKey(int key, int scancode, int action, int mods) {
        return WindowEventHandler.super.onKey(key, scancode, action, mods);
    }

    @Override
    public boolean onMouseButton(int button, int action, int mods) {
        return WindowEventHandler.super.onMouseButton(button, action, mods);
    }

    @Override
    public boolean onMouseEnter(boolean entered) {
        return WindowEventHandler.super.onMouseEnter(entered);
    }

    @Override
    public boolean onMouseMove(double xpos, double ypos) {
        return WindowEventHandler.super.onMouseMove(xpos, ypos);
    }

    @Override
    public boolean onScroll(double xoffset, double yoffset) {
        return WindowEventHandler.super.onScroll(xoffset, yoffset);
    }

    @Override
    public boolean onWindowResize(int width, int height) {
        return WindowEventHandler.super.onWindowResize(width, height);
    }

}
