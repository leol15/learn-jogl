package com.play.app.ui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import com.play.app.utils.WindowManager;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

/**
 * Every UI element should derive from UIBase
 * For simplicity, every UI element is a rectangle
 * Coordinates are in screen space
 */
@Log4j2
@Accessors(chain = true)
public class UIBase implements WindowEventHandler {

    protected final WindowManager windowManager;
    protected final BackgroundRect background;

    // state
    @Setter
    protected boolean visible = true;

    public UIBase(WindowManager windowManager) {
        this.windowManager = windowManager;
        background = new BackgroundRect(windowManager);
    }

    public UIBase(WindowManager windowManager, float x, float y, float w, float h) {
        this(windowManager);
        setBounds(x, y, w, h);
    }

    final public void show() {
        if (!visible) {
            return;
        }

        showInternal();
    }

    final public UIBase setPosition(float x, float y) {
        return setBounds(x, y, getWidth(), getHeight());
    }

    final public UIBase setSize(float width, float height) {
        return setBounds(getX(), getY(), width, height);
    }

    // subclass shoud override this for resizing
    public UIBase setBounds(float x, float y, float w, float h) {
        background.setBounds(x, y, w, h);
        return this;
    }

    public float getWidth() {
        return background.getW();
    }

    public float getHeight() {
        return background.getH();
    }

    public float getX() {
        return background.getX();
    }

    public float getY() {
        return background.getY();
    }

    //////////////////
    // for subclasses
    //////////////////

    // implement this to draw 
    protected void showInternal() {
        showBackground();
    }

    final public void setColor(Color c) {
        setColor(c.getRed() / 255.0f,
                c.getGreen() / 255.0f,
                c.getBlue() / 255.0f,
                c.getAlpha() / 255.0f);
    }

    // implement this to update color
    public UIBase setColor(float r, float g, float b, float a) {
        return this;
    }

    protected void showBackground() {
        int oldPolygonMode = glGetInteger(GL_POLYGON_MODE);
        glPolygonMode(GL_FRONT, GL_FILL);
        background.show();
        glPolygonMode(GL_FRONT, oldPolygonMode);
        // so subsequent draw can appear, TODO maybe just disable it
    }

    protected boolean inside(float screenX, float screenY) {
        return visible && background.inside(screenX, screenY);
    }

}
