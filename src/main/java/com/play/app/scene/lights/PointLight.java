package com.play.app.scene.lights;

import org.joml.*;

public class PointLight implements Light {

    public Vector4f color = new Vector4f(1);
    public Vector3f position = new Vector3f();

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.acceptPointLight(this, worldTransform);
    }

}
