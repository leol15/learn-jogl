package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.play.app.basics.*;
import com.play.app.collider.*;
import com.play.app.geometry.*;
import com.play.app.graphics.UnitGeometries;
import com.play.app.mesh.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Manages both the rendered shape and the collider in scene editor
 */
@Accessors(chain = true)
public class SOShape implements Editable, Savable, Loadable {
    @Setter
    public Mesh mesh;
    @Setter
    public Collider collider;

    private final Matrix4f rayMatIdentity = new Matrix4f();

    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix) {
        if (collider == null) {
            return null;
        } else {
            return collider.collide(ray, worldMatrix, rayMatIdentity);
        }
    }

    public void setUnitGeometry(UnitGeometries.Type type) {
        mesh = new UnitMesh(type);
        collider = new UnitCollider(type);
    }

    public void draw() {
        if (mesh != null) {
            mesh.draw();
        }
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        // TOOD, for now, cannot edit shape
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        // TODO hmmm
        writer.writeStartObject();
        writer.writeInterfaceField("mesh", mesh);
        writer.writeInterfaceField("collider", collider);
        writer.writeEndObject();
    }

    @Override
    public void load(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        mesh = (Mesh) reader.consumeInterfaceField("mesh");
        collider = (Collider) reader.consumeInterfaceField("collider");
        reader.consumeEndObject();
    }
}
