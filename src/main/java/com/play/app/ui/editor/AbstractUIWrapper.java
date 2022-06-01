package com.play.app.ui.editor;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.AbstractUIElement;
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
    protected float getW() {
        final UIElement e = getWrappedElement();
        return e != null ? e.getWidth() : 0;
    }

    @Override
    protected float getH() {
        final UIElement e = getWrappedElement();
        return e != null ? e.getHeight() : 0;
    }

    @Override
    protected void drawInternal(Matrix4f transform) {
        drawBackground(transform);
        final UIElement e = getWrappedElement();
        if (e != null) {
            e.draw(transform);
        }
    }

    @Override
    public void destroy() {
        final UIElement e = getWrappedElement();
        if (e != null) {
            e.destroy();
        }
    }

    @Override
    public UIElement onChar(int c) {
        final UIElement e = getWrappedElement();
        return e != null ? e.onChar(c) : null;
    }

    @Override
    public UIElement onKey(int key, int scancode, ButtonAction action, int mods) {
        final UIElement e = getWrappedElement();
        return e != null ? e.onKey(key, scancode, action, mods) : null;
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        final UIElement e = getWrappedElement();
        return e != null ? e.onMouseButton(button, action, mods, mouseX, mouseY) : null;
    }

    @Override
    public UIElement onMouseMove(double xpos, double ypos) {
        final UIElement e = getWrappedElement();
        return e != null ? e.onMouseMove(xpos, ypos) : null;
    }

    @Override
    public UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        final UIElement e = getWrappedElement();
        return e != null ? e.onScroll(mouseX, mouseY, xoffset, yoffset) : null;
    }

    @Override
    public void onWindowResize(int width, int height) {
        final UIElement e = getWrappedElement();
        if (e != null) {
            e.onWindowResize(width, height);
        }
    }

}
