package com.play.app.scene.lights;

import org.joml.Matrix4f;

public interface Light {
    public void addToUBO(Matrix4f worldTransform);
}
