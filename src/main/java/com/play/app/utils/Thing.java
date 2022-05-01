package com.play.app.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Thing {

    public Vector3f translation = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1);

    public Matrix4f getModelMatrix(final Matrix4f out) {
        return out.translation(translation).rotate(rotation).scale(scale);
    }

}
