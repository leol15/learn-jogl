package com.play.app.drawable;

import com.play.app.graphics.ShaderProgram;
import com.play.app.interfaces.Drawable;
import com.play.app.utils.CONST;

import org.joml.Matrix4f;

public abstract class DrawableBase implements Drawable {

    private static final Matrix4f TMP_MATRIX = new Matrix4f();

    protected ShaderProgram shader;

    public DrawableBase setShader(final ShaderProgram shader) {
        this.shader = shader;
        return this;
    }

    public void draw(final Matrix4f transform) {
        getModelMatrix(TMP_MATRIX);
        TMP_MATRIX.mulLocal(transform);

        if (shader != null) {
            shader.uniformMatrix4fv(CONST.MODEL_MATRIX, TMP_MATRIX);
            shader.useProgram();
        }

        drawSelf();

        if (shader != null) {
            shader.unuseProgram();
        }
    }

    protected abstract void drawSelf();

    protected abstract void getModelMatrix(final Matrix4f out);

}
