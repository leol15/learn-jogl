package com.play.app.ui;

import java.awt.Color;

import com.play.app.geometry.Rect;
import com.play.app.graphics.*;
import com.play.app.utils.*;

import org.joml.*;

// just draws a rectangle on screen
public class BackgroundRect {
    public final Vector4f color = new Vector4f(1);

    // in screen space
    private final ShaderProgram uiShader;
    private final Rect rectangle = new Rect(0, 0, 0, 0);
    private final Matrix4f modelMatrix = new Matrix4f();
    private final WindowManager windowManager;

    public BackgroundRect(WindowManager windowManager) {
        this.windowManager = windowManager;
        uiShader = ShaderUtils.getShader("UI");
        windowManager.windowSizeEvent.addListener(e -> computeMatrix());
    }

    public float getX() {
        return rectangle.getX();
    }

    public float getY() {
        return rectangle.getY();
    }

    public float getW() {
        return rectangle.getW();
    }

    public float getH() {
        return rectangle.getH();
    }

    public void setPosition(float x, float y) {
        setBounds(x, y, getW(), getH());
    }

    public void setSize(float w, float h) {
        setBounds(getX(), getY(), w, h);
    }

    public void setBounds(float x, float y, float w, float h) {
        rectangle.setX(x).setY(y).setW(w).setH(h);
        computeMatrix();
    }

    private void computeMatrix() {
        modelMatrix.identity();
        // to gl space
        modelMatrix.scale(2f / windowManager.windowSize[0], -2f / windowManager.windowSize[1], 1);
        modelMatrix.translate(-windowManager.windowSize[0] / 2, -windowManager.windowSize[1] / 2, 0);
        // scale and move
        modelMatrix.scale(getW(), getH(), 1);
        modelMatrix.translate(getX() / getW(), getY() / getH(), 0);
    }

    public void show() {
        uiShader.uniform4f(CONST.MATERIAL_COLOR, color);
        uiShader.uniformMatrix4fv("UItoGL", modelMatrix);
        uiShader.useProgram();
        UnitGeometries.drawPlane();
        uiShader.unuseProgram();
    }

    public boolean inside(float screenX, float screenY) {
        return rectangle.inside(screenX, screenY);
    }

    public void setColor(final Vector4f color) {
        this.color.set(color);
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public void setColor(final Color color) {
        setColor(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }
}
