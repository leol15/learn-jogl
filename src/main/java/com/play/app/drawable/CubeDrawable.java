package com.play.app.drawable;

import com.play.app.geometry.Cube;
import com.play.app.graphics.UnitGeometries;

import org.joml.Matrix4f;

public class CubeDrawable extends DrawableBase {

    private final Cube cube = new Cube();

    @Override
    protected void drawSelf() {
        UnitGeometries.drawCube();

    }

    @Override
    protected void getModelMatrix(Matrix4f out) {
        cube.getModelMatrix(out);
    }

}
