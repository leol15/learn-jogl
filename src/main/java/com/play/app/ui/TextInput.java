package com.play.app.ui;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

import com.play.app.graphics.Text;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.Vector4f;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class TextInput extends UIBase {

    public static final float TEXT_HEIGHT = 28;
    private static final String DEFAULT_INPUT = "input";
    private static final Vector4f DEFAULT_BACKGROUND_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 0.75f);
    private static final Vector4f DEFAULT_ACTIVE_BACKGROUND_COLOR = new Vector4f(0.35f, 0.35f, 0.35f, 1f);

    private final Text textDisplay;
    private final List<Character> textContent;
    @Setter
    private boolean scrollable = false;
    @Setter
    private float scrollDelta = 0.1f;
    private boolean focused;

    @Setter
    private Consumer<TextInput> onChange;

    public TextInput(WindowManager windowManager, float x, float y) {
        super(windowManager);
        textDisplay = new Text(windowManager, "", x, y);
        textDisplay.setColor(Color.WHITE);

        textContent = new ArrayList<>();
        for (int i = 0; i < DEFAULT_INPUT.length(); i++) {
            textContent.add(DEFAULT_INPUT.charAt(i));
        }
        textContentUpdated();

        // configure
        setBounds(x, y, 100, TEXT_HEIGHT);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);

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
        textContent.clear();
        for (int i = 0; i < text.length(); i++) {
            textContent.add(text.charAt(i));
        }
        // potential of infinite loop
        textContentUpdated();
    }

    public String getAsString() {
        StringBuilder sb = new StringBuilder();
        for (char c : textContent) {
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

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        textDisplay.setText(getAsString(), x, y);
        return this;
    }

    private void textContentUpdated() {
        textDisplay.setText(getAsString());
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    @Override
    public void showInternal() {
        showBackground();
        textDisplay.draw();
    }

    private void addChar(int c) {
        textContent.add((char) c);
        textContentUpdated();
    }

    private void deleteChar(int mods) {
        if (mods == GLFW_MOD_CONTROL) {
            // delete word
            while (textContent.size() > 0 && textContent.get(textContent.size() - 1) != ' ') {
                textContent.remove(textContent.size() - 1);
            }
            // remove space
            if (textContent.size() > 0) {
                textContent.remove(textContent.size() - 1);
            }
        } else if (mods == GLFW_MOD_ALT) {
            // delete all
            textContent.clear();
        } else {
            if (textContent.size() > 0) {
                textContent.remove(textContent.size() - 1);
            }
        }
        textContentUpdated();
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

    private void setFocused(boolean inFocus) {
        if (focused == inFocus) {
            return;
        }
        focused = inFocus;
        if (focused) {
            textDisplay.setColor(Color.YELLOW);
            setBackgroundColor(DEFAULT_ACTIVE_BACKGROUND_COLOR);
        } else {
            textDisplay.setColor(Color.WHITE);
            setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
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
