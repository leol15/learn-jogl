package com.play.app.ui.elements;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;

import java.util.ArrayList;
import java.util.List;

import com.play.app.basics.Event;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Matrix4f;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextInput extends AbstractUIElement {

    @Setter
    private float padding = 5;
    public final Event<TextInput> changeEvent;
    @Setter
    private float width = -1;
    @Setter
    private boolean scrollable = false;
    private float scrollDelta = 0.1f;
    private String NUMBER_FORMAT;

    private final UIText text;
    private final Matrix4f textTransform = new Matrix4f();
    private final List<Character> textContent = new ArrayList<>();

    // TODO: loose focus when click outside
    private boolean focused = false;

    public TextInput(UIManager uiManager) {
        this(uiManager, "input");
    }

    public TextInput(UIManager uiManager, String placeholder) {
        super(uiManager);
        text = new UIText(uiManager);
        changeEvent = new Event<TextInput>(this);
        setDefaultColor();
        computeNumberFormat();
        setContent(placeholder);
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
    public float getW() {
        return padding * 2 + (width > 0 ? width : text.getWidth());
    }

    @Override
    public float getH() {
        return padding * 2 + text.getHeight();
    }

    @Override
    protected void drawInternal(Matrix4f transform) {
        // try to center text
        textTransform.identity();
        textTransform.translate(padding, padding, 0);
        if (width > 0) {
            textTransform.translate(width / 2 - text.getWidth() / 2, 0, 0);
        }
        textTransform.mulLocal(transform);
        text.draw(textTransform);
    }

    @Override
    public void destroy() {
        text.destroy();
    }

    public void setContent(String text) {
        textContent.clear();
        for (char c : text.toCharArray()) {
            textContent.add(c);
        }
        updated();
    }

    public void setContent(float v) {
        setContent(String.format(NUMBER_FORMAT, v));
    }

    private void setDefaultColor() {
        bgColor.set(0.4, 0.4, 0);
        text.bgColor.set(0.1, 0.1, 0, 0.2);
        text.textColor.set(1);
    }

    private void setfocusedColor() {
        bgColor.set(0, 0.3, 0.3);
        text.bgColor.set(0.3, 0.3, 0, 0.2);
        text.textColor.set(1);
    }

    private void updated() {
        final StringBuilder sb = new StringBuilder();
        textContent.forEach(c -> sb.append(c));
        text.setText(" " + sb.toString() + " ");
        if (focused) {
            setfocusedColor();
        } else {
            setDefaultColor();
        }
        changeEvent.fire();
    }

    // scroll input
    public void setScrollDelta(float delta) {
        scrollable = true;
        scrollDelta = delta;
        computeNumberFormat();
        updated();
    }

    private void computeNumberFormat() {
        final String deltaStr = String.valueOf(scrollDelta);
        final int dotIdx = deltaStr.indexOf(".");
        if (dotIdx == -1) {
            NUMBER_FORMAT = "%.0f";
        } else {
            int i = deltaStr.length() - 1;
            while (deltaStr.charAt(i) == '0') {
                i--;
            }
            NUMBER_FORMAT = "%." + (i - dotIdx) + "f";
        }
    }

    ////////////////
    // events
    ////////////////

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        if (action != ButtonAction.RELEASE) {
            focused = !focused;
            updated();
            return this;
        } else {
            return null;
        }
    }

    @Override
    public UIElement onScroll(float xPos, float yPos, double xoffset, double yoffset) {
        // will scroll even if not focued
        if (!scrollable) {
            return null;
        }
        // is numeric?
        try {
            float v = Float.parseFloat(getAsString());
            v += yoffset * scrollDelta;
            setContent(v);
        } catch (Exception e) {

        }
        return this;
    }

    @Override
    public UIElement onChar(int c) {
        if (focused) {
            textContent.add((char) c);
            updated();
            return this;
        }
        return null;
    }

    @Override
    public UIElement onKey(int key, int scancode, ButtonAction action, int mods) {
        if (!focused) {
            return null;
        }
        if (key == GLFW_KEY_BACKSPACE) {
            if (action == ButtonAction.PRESS) {
                deleteChar(mods);
            }
        }
        return this;
    }

    // advanced delete
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
        updated();
    }
}
