package com.play.app.scene.lights;

import com.play.app.basics.Selectable;

import org.joml.Matrix4f;

public interface Light extends Selectable {
    public void addToUBO(Matrix4f worldTransform);
}
