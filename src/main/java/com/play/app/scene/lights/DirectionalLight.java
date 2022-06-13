package com.play.app.scene.lights;

import java.io.IOException;

import com.play.app.collider.Collider;
import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.Mesh;
import com.play.app.mesh.UnitMesh;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * light direction is (0, 1, 0)
 */
public class DirectionalLight implements Light {
    public Vector4f color = new Vector4f(1, 1, 0, 1);
    private static final Vector4f DIRECTION = new Vector4f(0, 1, 0, 0);

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.getInstance().acceptDirectionalLight(this, worldTransform);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("DL Color", color);
    }

    @Override
    public Mesh getDebugMesh() {
        return new UnitMesh(UnitGeometries.Type.Cone);
    }

    @Override
    public Collider getDebugCollider() {
        return new UnitCollider(Type.Cone);
    }

    public Vector4f getDirection() {
        return DIRECTION;
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("color", color);
        writer.writeEndObject();
    }

    public static DirectionalLight create(WorldSerializer reader) throws IOException {
        final DirectionalLight dl = new DirectionalLight();
        reader.consumeStartObject();
        reader.consumeObjectField("color", dl.color);
        reader.consumeEndObject();
        return dl;
    }

}
