package com.play.app.ui.elements;

import com.play.app.graphics.*;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.utils.*;

import org.joml.Matrix4f;

import lombok.Getter;

public abstract class AbstractUIElement implements UIElement {

    public final Color bgColor = new Color(0.2f, 0.2f, 0.2f, 0.3f);

    protected final UIManager uiManager;
    @Getter
    protected boolean visible = true;

    private final Matrix4f model = new Matrix4f();

    public AbstractUIElement(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    /**
     * Visibility, affect drawing, size calclation, and events
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    final public float getWidth() {
        if (isVisible()) {
            return getW();
        }
        return 0;
    }

    @Override
    final public float getHeight() {
        if (isVisible()) {
            return getH();
        }
        return 0;
    }

    abstract protected float getW();

    abstract protected float getH();

    //////////////////
    // drawing
    //////////////////

    abstract protected void drawInternal(Matrix4f transform);

    @Override
    public final void draw(Matrix4f transform) {
        if (this.isVisible()) {
            drawInternal(transform);
        }
    }

    protected final void drawBackground(Matrix4f transform) {
        model.identity().scale(getWidth(), getHeight(), 1);
        model.mulLocal(transform);
        uiManager.uiShader.uniform4f(CONST.MATERIAL_COLOR, bgColor.get());
        uiManager.uiShader.uniformMatrix4fv(CONST.MODEL_MATRIX, model);
        uiManager.uiShader.useProgram();
        UnitGeometries.drawPlane();
        uiManager.uiShader.unuseProgram();
    }
}
