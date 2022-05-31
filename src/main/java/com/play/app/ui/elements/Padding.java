package com.play.app.ui.elements;

import com.play.app.ui.UIManager;

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
    public void drawInternal(Matrix4f transform) {
        drawBackground(transform);
    }

}
