package com.play.app.scene.lights;

import java.io.IOException;

import com.play.app.collider.*;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

public class PointLight implements Light {

    public Vector4f color = new Vector4f(1, 1, 0, 1);
    public Vector3f attenuation = new Vector3f(0.01f, 0.02f, 1f);

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
        return new UnitMesh(UnitGeometries.Type.Sphere);
    }

    @Override
    public Collider getDebugCollider() {
        return new UnitCollider(Type.Sphere);
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("color", color);
        writer.writeObjectField("attenuation", attenuation);
        writer.writeEndObject();
    }

    public static PointLight create(WorldSerializer reader) throws IOException {
        final PointLight pl = new PointLight();

        reader.consumeStartObject();
        reader.consumeObjectField("color", pl.color);
        reader.consumeObjectField("attenuation", pl.attenuation);
        reader.consumeEndObject();

        return pl;
    }

}
