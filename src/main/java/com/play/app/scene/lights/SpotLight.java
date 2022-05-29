package com.play.app.scene.lights;

import java.io.IOException;

import com.play.app.collider.*;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.property.FloatProperty;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

public class SpotLight implements Light {
    private static final Vector4f DIRECTION = new Vector4f(0, -1, 0, 0);

    public final Vector4f color = new Vector4f(1, 1, 0, 1);
    public final Vector3f attenuation = new Vector3f(0.01f, 0.02f, 1f);
    public final FloatProperty angle = new FloatProperty(45); // spot light area in degrees

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
        return new UnitMesh(UnitGeometries.Type.Cone);
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    public Vector4f getDirection() {
        return DIRECTION;
    }

    @Override
    public Collider getDebugCollider() {
        // TODO use cone
        return new UnitCollider(Type.Cube);
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("color", color);
        writer.writeObjectField("attenuation", attenuation);
        writer.writeObjectField("angle", angle.getValue());
        writer.writeEndObject();
    }

    public static SpotLight create(WorldSerializer reader) throws IOException {
        final SpotLight sl = new SpotLight();

        reader.consumeStartObject();
        reader.consumeObjectField("color", sl.color);
        reader.consumeObjectField("attenuation", sl.attenuation);
        sl.angle.setValue(reader.consumeFloatField("angle"));
        reader.consumeEndObject();

        return sl;
    }
}
