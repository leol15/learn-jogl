package com.play.app.scene.lights;

import com.play.app.mesh.Mesh;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

/**
 * light direction is (0, 1, 0)
 */
public class DirectionalLight implements Light {
    public Vector4f color = new Vector4f(1, 1, 0, 1);
    private static final Vector4f DIRECTION = new Vector4f(0, 1, 0, 0);

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.getInstance().accepDirectionalLight(this, worldTransform);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("DL Color", color);
    }

    @Override
    public Mesh getDebugMesh() {
        return Mesh.createConeMesh(10);
    }

    public Vector4f getDirection() {
        return DIRECTION;
    }

}
