package com.play.app.ui.elements;

import java.util.ArrayList;
import java.util.List;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;

/**
 * For element that has arbitrary children
 */
public abstract class AbstractContainer extends AbstractUIElement {

    public AbstractContainer(UIManager uiManager) {
        super(uiManager);
    }

    protected final List<UIElement> children = new ArrayList<>();

    public int numChildren() {
        return children.size();
    }

    public void addChild(UIElement e) {
        children.add(e);
    }

    public void preprendChild(UIElement e) {
        children.add(0, e);
    }

    public UIElement removeChild(UIElement e) {
        if (children.remove(e)) {
            return e;
        } else {
            return null;
        }
    }

    public void clear() {
        children.removeIf(c -> {
            c.destroy();
            return true;
        });
    }

    @Override
    public void destroy() {
        clear();
    }

    @Override
    public UIElement onChar(int c) {
        for (final UIElement e : children) {
            if (e.onChar(c) != null) {
                return e;
            }
        }
        return null;
    }

    @Override
    public UIElement onKey(int key, int scancode, ButtonAction action, int mods) {
        for (final UIElement e : children) {
            if (e.onKey(key, scancode, action, mods) != null) {
                return e;
            }
        }
        return null;
    }

    @Override
    public UIElement onMouseMove(double xpos, double ypos) {
        for (final UIElement e : children) {
            if (e.onMouseMove(xpos, ypos) != null) {
                return e;
            }
        }
        return null;
    }

    @Override
    public void onWindowResize(int width, int height) {
        for (final UIElement e : children) {
            e.onWindowResize(width, height);
        }
    }

}
