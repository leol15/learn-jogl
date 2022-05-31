package com.play.app.ui.elements;

import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Math;
import org.joml.Matrix4f;

import lombok.extern.log4j.Log4j2;

/**
 * A vertical container that stacks children
 */
@Log4j2
public class ContainerV extends AbstractContainer {

    public float padding = 10;
    private final Matrix4f childTransform = new Matrix4f();

    public ContainerV(UIManager uiManager) {
        super(uiManager);
    }

    public void addChild(UIElement e) {
        children.add(e);
    }

    @Override
    public float getW() {
        float w = 0;
        for (final UIElement child : children) {
            w = Math.max(w, child.getWidth());
        }
        return w;
    }

    @Override
    public float getH() {
        float h = 0;
        for (final UIElement child : children) {
            if (h != 0 && child.getHeight() != 0) {
                h += padding;
            }
            h += child.getHeight();
        }
        return h;
    }

    @Override
    public void drawInternal(Matrix4f transform) {
        drawBackground(transform);
        float y = 0;
        for (final UIElement child : children) {
            transform.translate(0, y, 0, childTransform);
            child.draw(childTransform);
            y += child.getHeight() + padding;
        }
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        // find a child to handle this
        float y = 0;
        for (final UIElement child : children) {
            if (y != 0 && child.getHeight() != 0) {
                y += padding;
            }
            if (child.contains(mouseX, mouseY - y)) {
                return child.onMouseButton(button, action, mods, mouseX, mouseY - y);
            }
            y += child.getHeight();
        }
        return null;
    }

    @Override
    public UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        float y = 0;
        for (final UIElement child : children) {
            if (y != 0 && child.getHeight() != 0) {
                y += padding;
            }
            if (child.contains(mouseX, mouseY - y)) {
                return child.onScroll(mouseX, mouseY - y, xoffset, yoffset);
            }
            y += child.getHeight();
        }
        return null;
    }

}
