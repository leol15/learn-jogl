package com.play.app.drawable;

import java.nio.FloatBuffer;

import com.play.app.geometry.Cube;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.utils.CONST;
import com.play.app.utils.Func;

import org.joml.Matrix4f;

public class CubeDrawable implements Drawable {

    private static final Matrix4f TMP_MATRIX = new Matrix4f();

    private final Cube cube = new Cube();
    private ShaderProgram shader;

    public CubeDrawable() {
    }

    public CubeDrawable setShader(final ShaderProgram shader) {
        this.shader = shader;
        return this;
    }

    @Override
    public void draw(final Matrix4f transform) {
        cube.getModelMatrix(TMP_MATRIX);
        TMP_MATRIX.mulLocal(transform);

        shader.uniformMatrix4fv(CONST.MODEL_MATRIX, TMP_MATRIX);
        shader.useProgram();
        UnitGeometries.drawCube();
        shader.unuseProgram();
    }

}
