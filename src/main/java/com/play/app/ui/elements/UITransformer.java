package com.play.app.ui.elements;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.AbstractUIWrapper;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * give a transformation to an arbitrary UIElement
 */
@Log4j2
public class UITransformer extends AbstractUIWrapper {

    @Getter
    @Setter
    private UIElement target;
    private final Matrix4f targetTransform = new Matrix4f();
    public final Vector2f translation = new Vector2f();
    public final Vector2f scale = new Vector2f(1);

    public UITransformer(UIManager uiManager) {
        this(uiManager, null);
    }

    public UITransformer(UIManager uiManager, UIElement element) {
        super(uiManager);
        setTarget(element);
        setDrawBackground(false);
    }

    @Override
    protected float getW() {
        if (target == null) {
            return 0;
        }
        return translation.x + scale.x * target.getWidth();
    }

    @Override
    protected float getH() {
        if (target == null) {
            return 0;
        }
        return translation.y + scale.y * target.getHeight();
    }

    @Override
    protected void drawInternal(Matrix4f transform) {
        if (target != null) {
            targetTransform.identity()
                    .translate(translation.x, translation.y, 0)
                    .scale(scale.x, scale.y, 1);
            targetTransform.mulLocal(transform);
            target.draw(targetTransform);
        }
    }

    @Override
    protected UIElement getWrappedElement() {
        return target;
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        if (target == null) {
            return null;
        }
        // to target's local space
        mouseX = (mouseX - translation.x) / scale.x;
        mouseY = (mouseY - translation.y) / scale.y;
        if (target.contains(mouseX, mouseY)) {
            return target.onMouseButton(button, action, mods, mouseX, mouseY);
        }
        return null;
    }

    @Override
    public UIElement onScroll(float mouseX, float mouseY, double xoffset, double yoffset) {
        if (target == null) {
            return null;
        }
        // to target's local space
        mouseX = (mouseX - translation.x) / scale.x;
        mouseY = (mouseY - translation.y) / scale.y;
        if (target.contains(mouseX, mouseY)) {
            return target.onScroll(mouseX, mouseY, xoffset, yoffset);
        }
        return null;
    }

}
