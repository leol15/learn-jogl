package com.play.app.ui;

import static org.lwjgl.opengl.GL30.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.play.app.graphics.Text;
import com.play.app.ui.WindowManager.Layer;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;

@Log4j2
@Accessors(chain = true)
public class TextInput extends UIBase {

    private static final String DEFAULT_INPUT = "input";
    private static final Vector4f DEFAULT_BACKGROUND_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 0.75f);
    private static final Vector4f DEFAULT_ACTIVE_BACKGROUND_COLOR = new Vector4f(0.35f, 0.35f, 0.35f, 1f);

    private final Text input;
    private final List<Character> value;
    @Setter
    private boolean scrollable = false;
    @Setter
    private float scrollDelta = 0.1f;
    private boolean focused;

    @Setter
    private Consumer<TextInput> onChange;

    public TextInput(WindowManager windowManager, float x, float y) {
        super(windowManager, x, y, 80, 25);
        input = new Text(windowManager, "", x, y);
        input.setColor(Color.WHITE);

        value = new ArrayList<>();
        for (int i = 0; i < DEFAULT_INPUT.length(); i++) {
            value.add(DEFAULT_INPUT.charAt(i));
        }

        updateText();
        // bg
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setSize(100, input.getHeight());

        // for simlpicity, just add call back
        windowManager.addKeyCallback(Layer.UI, (window, key, code, action, mods) -> {
            handleKey(key, action, mods);
        });

        windowManager.addCharCallback(Layer.UI, (window, c) -> {
            if (!focused) {
                return;
            }
            addChar(c);
        });

        windowManager.addMouseButtonCallback(Layer.UI, (window, button, action, mods) -> {
            final float mouseX = windowManager.lastMousePos[0];
            final float mouseY = windowManager.lastMousePos[1];
            onClick(mouseX, mouseY, action);
        });

        windowManager.addScrollCallback(Layer.UI, (window, dx, dy) -> {
            handleScroll(dx, dy);
        });
    }

    public void setText(CharSequence text) {
        value.clear();
        for (int i = 0; i < text.length(); i++) {
            value.add(text.charAt(i));
        }
        updateText();
    }

    public String getAsString() {
        StringBuilder sb = new StringBuilder();
        for (char c : value) {
            sb.append(c);
        }
        return sb.toString();
    }

    public float getAsFloat() {
        try {
            return Float.parseFloat(getAsString());
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }

    private void updateText() {
        input.setText(getAsString());
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    private void setFocused(boolean inFocus) {
        if (focused == inFocus) {
            return;
        }
        focused = inFocus;
        if (focused) {
            input.setColor(Color.YELLOW);
            setBackgroundColor(DEFAULT_ACTIVE_BACKGROUND_COLOR);
        } else {
            input.setColor(Color.WHITE);
            setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        }
    }

    @Override
    public void showInternal() {
        showBackground();
        input.draw();
    }

    private void addChar(int c) {
        value.add((char) c);
        updateText();
    }

    private void deleteChar(int mods) {
        if (mods == GLFW_MOD_CONTROL) {
            // delete word
            while (value.size() > 0 && value.get(value.size() - 1) != ' ') {
                value.remove(value.size() - 1);
            }
            // remove space
            if (value.size() > 0) {
                value.remove(value.size() - 1);
            }
        } else if (mods == GLFW_MOD_ALT) {
            // delete all
            value.clear();
        } else {
            if (value.size() > 0) {
                value.remove(value.size() - 1);
            }
        }
        updateText();
    }

    // for none char inputs
    private void handleKey(int key, int action, int mods) {
        if (!focused) {
            return;
        }
        windowManager.stopPropagation();
        if (key == GLFW_KEY_BACKSPACE) {
            if (action == GLFW_PRESS) {
                deleteChar(mods);
            }
        }
    }

    private void handleScroll(double dx, double dy) {
        // will scroll even if not focued
        if (!visible || !scrollable) {
            return;
        }
        if (!inside(windowManager.lastMousePos[0], windowManager.lastMousePos[1])) {
            return;
        }
        windowManager.stopPropagation();
        // is numeric?
        try {
            float v = Float.parseFloat(getAsString());
            v += dy * scrollDelta;
            setText(String.format("%.1f", v));
        } catch (Exception e) {

        }
    }

    private void onClick(float mouseX, float mouseY, int action) {
        boolean inside = inside(mouseX, mouseY);
        boolean shouldConsumeEvent = inside;
        if (shouldConsumeEvent) {
            // event is handled
            windowManager.stopPropagation();
        }
        if (action == GLFW_PRESS) {
            // ignore
            return;
        }
        if (!inside) {
            setFocused(false);
        } else {
            // toggle
            setFocused(!focused);
        }
    }

}
