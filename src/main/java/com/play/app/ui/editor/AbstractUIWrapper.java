package com.play.app.ui.editor;

import com.play.app.ui.UIManager;
import com.play.app.ui.elements.AbstractUIElement;
import com.play.app.ui.elements.UIElement;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Matrix4f;

/**
 * Simple, just pass all events to the child
 */
public abstract class AbstractUIWrapper extends AbstractUIElement {

    public AbstractUIWrapper(UIManager uiManager) {
        super(uiManager);
    }

    abstract protected UIElement getWrappedElement();

    @Override
    public float getW() {
        return getWrappedElement().getWidth();
    }

    @Override
    public float getH() {
        return getWrappedElement().getHeight();
    }

    @Override
    protected void drawInternal(Matrix4f transform) {
        drawBackground(transform);
        getWrappedElement().draw(transform);
    }

    @Override
    public UIElement onChar(int c) {
        return getWrappedElement().onChar(c);
    }

    @Override
    public UIElement onKey(int key, int scancode, ButtonAction action, int mods) {
        return getWrappedElement().onKey(key, scancode, action, mods);
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        return getWrappedElement().onMouseButton(button, action, mods, mouseX, mouseY);
    }

    @Override
    public UIElement onMouseMove(double xpos, double ypos) {
        return getWrappedElement().onMouseMove(xpos, ypos);
    }

    @Override
    public UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        return getWrappedElement().onScroll(mouseX, mouseY, xoffset, yoffset);
    }

    @Override
    public void onWindowResize(int width, int height) {
        getWrappedElement().onWindowResize(width, height);
    }

}
