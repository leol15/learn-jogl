package com.play.app.ui;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.util.*;

import com.play.app.graphics.Text;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.Vector4f;

public class Button extends UIBase {

    // static things
    private static List<Button> buttons;

    // state
    private Runnable action = null;
    private Text text;
    private CharSequence label;

    // internal things
    private Vector4f hoverColor = new Vector4f(0.8f, 0.8f, 0.8f, 1f);

    // coordinates are in screen space
    public Button(WindowManager windowManager, float x, float y, float width, float height) {
        super(windowManager);
        label = "Button";
        this.text = new Text(windowManager, label, x, y);
        init(x, y, width, height);
    }

    public Button(WindowManager windowManager, float x, float y, CharSequence label) {
        super(windowManager);
        this.label = label;
        this.text = new Text(windowManager, label, x, y);
        init(x, y, text.getWidth(), text.getHeight());
    }

    private void init(float x, float y, float width, float height) {
        setColor(0.8f, 0.8f, 0.8f, 1f);
        setPosition(x, y);
        setSize(width, height);

        if (buttons == null) {
            initStatic();
        }
        buttons.add(this);
    }

    public void setLabel(String label) {
        this.label = label;
        text.setText(label);
    }

    public void setAction(Runnable r) {
        action = r;
    }

    public Button setColor(float r, float g, float b, float a) {
        setBackgroundColor(r, g, b, a);
        backgroundColorCopy.set(backgroundColor);
        computeHoverAndTextColor();
        return this;
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        text.setText(label, x, y);
        return this;
    }

    private Vector4f backgroundColorCopy = new Vector4f();

    private void setHovered(boolean hovered) {
        if (hovered) {
            // copy over background color
            backgroundColorCopy.set(backgroundColor);
            setBackgroundColor(hoverColor);
        } else {
            setBackgroundColor(backgroundColorCopy);
        }
    }

    private void computeHoverAndTextColor() {
        if (backgroundColor.length() < 1.01f) {
            hoverColor.set(0.2, 0.2, 0.2, 1);
        } else {
            backgroundColor.mul(1.2f, hoverColor);
        }
        hoverColor.mul(1, 1, 1, 0.7f);
        text.setColor(1 - backgroundColor.x,
                1 - backgroundColor.y,
                1 - backgroundColor.z,
                1);
    }

    @Override
    public void showInternal() {
        showBackground();
        text.draw();
    }

    private void handleClick(double x, double y, int buttonAction) {
        if (buttonAction == GLFW_PRESS) {
            if (action != null) {
                action.run();
            }
        }
    }

    private void initStatic() {
        buttons = new ArrayList<>();
        // setup one callback on mouse click
        windowManager.addMouseButtonCallback(Layer.UI, (window, button, action, mods) -> {
            final float mouseX = windowManager.lastMousePos[0];
            final float mouseY = windowManager.lastMousePos[1];
            for (final Button b : buttons) {
                if (!b.inside(mouseX, mouseY)) {
                    continue;
                }
                // event is handled
                windowManager.stopPropagation();
                b.handleClick(mouseX, mouseY, action);
            }
        });

        windowManager.addCursorPosCallback(Layer.UI, Button::cursorHover);
    }

    private static void cursorHover(long window, double x, double y) {
        boolean anyHover = false;
        for (Button b : buttons) {
            final boolean isHovered = b.inside((float) x, (float) y);
            b.setHovered(isHovered);
            anyHover |= isHovered;
        }
        if (anyHover) {
            Cursor.setCusor(window, Cursor.CURSOR_POINTING_HAND);
        } else {
            Cursor.setCusor(window, Cursor.CURSOR_ARROW);
        }
    }

}
