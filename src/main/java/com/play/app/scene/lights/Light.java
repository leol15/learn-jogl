package com.play.app.scene.lights;

import com.play.app.basics.*;

import org.joml.Matrix4f;

public interface Light extends Editable {
    public void addToUBO(Matrix4f worldTransform);
}
