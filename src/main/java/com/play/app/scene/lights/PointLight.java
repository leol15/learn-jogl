package com.play.app.scene.lights;

import com.play.app.mesh.Mesh;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

public class PointLight implements Light {

    public Vector4f color = new Vector4f(1, 1, 0, 1);
    public Vector3f attenuation = new Vector3f(0f, 0.02f, 1f);

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.getInstance().acceptPointLight(this, worldTransform);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("PL color", color);
        editor.addProperty("Falloff", attenuation, 0.01f);
    }

    @Override
    public Mesh getDebugMesh() {
        return Mesh.createSphereMesh(10);
    }

}
