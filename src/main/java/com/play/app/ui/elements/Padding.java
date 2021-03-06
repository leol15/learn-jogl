package com.play.app.ui.elements;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Matrix4f;

import lombok.extern.log4j.Log4j2;

/**
 * blank space, very important
 */
@Log4j2
public class Padding extends AbstractUIElement {

    public float width, height;

    public Padding(UIManager uiManager) {
        this(uiManager, 100, 100);
        bgColor.set(0.5);
    }

    public Padding(UIManager uiManager, float w, float h) {
        super(uiManager);
        width = w;
        height = h;
    }

    @Override
    public float getW() {
        return width;
    }

    @Override
    public float getH() {
        return height;
    }

    @Override
    public void drawInternal(Matrix4f transform) {}

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        if (action == ButtonAction.PRESS) {
            log.trace("You clicked a padding!");
        }
        return null;
    }

}
