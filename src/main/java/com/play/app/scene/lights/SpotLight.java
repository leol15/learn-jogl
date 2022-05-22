package com.play.app.scene.lights;

import com.play.app.basics.Collidable;
import com.play.app.geometry.Cube;
import com.play.app.mesh.Mesh;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

public class SpotLight implements Light {
    private static final Vector4f DIRECTION = new Vector4f(0, -1, 0, 0);

    public final Vector4f color = new Vector4f(1, 1, 0, 1);
    public final Vector3f attenuation = new Vector3f(0.01f, 0.02f, 1f);
    public final Vector3f angle = new Vector3f(30, 0, 0); // spot light area in degrees

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("Color", color);
        editor.addProperty("Falloff", attenuation, 0.01f);
        editor.addProperty("Angle", angle, 5);
    }

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.getInstance().acceptSpotLight(this, worldTransform);
    }

    @Override
    public Mesh getDebugMesh() {
        return Mesh.createConeMesh(10);
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    public Vector4f getDirection() {
        return DIRECTION;
    }

    @Override
    public Collidable getDebugCollidable() {
        // TODO use cone
        return new Cube();
    }
}
