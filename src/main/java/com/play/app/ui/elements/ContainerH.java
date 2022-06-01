package com.play.app.ui.elements;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Matrix4f;

public class ContainerH extends AbstractContainer {

    public float padding = 10;

    private final Matrix4f childTransform = new Matrix4f();

    public ContainerH(UIManager uiManager) {
        super(uiManager);
    }

    @Override
    public float getW() {
        float w = 0;
        for (final UIElement child : children) {
            if (w != 0 && child.getWidth() != 0) {
                w += padding;
            }
            w += child.getWidth();
        }
        return w;
    }

    @Override
    public float getH() {
        float h = 0;
        for (final UIElement child : children) {
            h = Math.max(h, child.getHeight());
        }
        return h;
    }

    @Override
    public void drawInternal(Matrix4f transform) {
        drawBackground(transform);
        float x = 0;
        for (final UIElement child : children) {
            transform.translate(x, 0, 0, childTransform);
            child.draw(childTransform);
            x += child.getWidth() + padding;
        }
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        // find a child to handle this
        float x = 0;
        for (final UIElement child : children) {
            if (x != 0 && child.getWidth() != 0) {
                x += padding;
            }
            if (child.contains(mouseX - x, mouseY)) {
                return child.onMouseButton(button, action, mods, mouseX - x, mouseY);
            }
            x += child.getWidth();
        }
        return null;
    }

    @Override
    public UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        float x = 0;
        for (final UIElement child : children) {
            if (child.contains(mouseX - x, mouseY)) {
                return child.onScroll(mouseX - x, mouseY, xoffset, yoffset);
            }
            x += child.getWidth() + padding;
        }
        return null;
    }
}
