package com.play.app.basics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpacialThing {

    public final Vector3f scale = new Vector3f(1);
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f translation = new Vector3f();

    public Matrix4f getModelMatrix(final Matrix4f out) {
        return out.translation(translation).rotate(rotation).scale(scale);
    }

}
