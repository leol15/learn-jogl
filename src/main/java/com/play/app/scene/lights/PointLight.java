package com.play.app.scene.lights;

import com.play.app.geometry.Ray;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

public class PointLight implements Light {

    public Vector4f color = new Vector4f(1, 1, 0, 1);
    public Vector3f attenuation = new Vector3f(0f, 0.02f, 1f);

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.instance().acceptPointLight(this, worldTransform);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("PL color", color);
        editor.addProperty("attenuation", attenuation, 0.01f);
    }

}
