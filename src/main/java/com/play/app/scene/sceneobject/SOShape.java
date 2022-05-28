package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.play.app.basics.*;
import com.play.app.geometry.*;
import com.play.app.mesh.Mesh;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Manages both the rendered shape and the collidable in scene editor
 */
@Accessors(chain = true)
public class SOShape implements Editable, Savable, Loadable {
    @Setter
    public Mesh mesh;
    @Setter
    public Collidable collidable;

    private final Matrix4f rayMatIdentity = new Matrix4f();

    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix) {
        if (collidable == null) {
            return null;
        } else {
            return collidable.collide(ray, worldMatrix, rayMatIdentity);
        }
    }

    public void draw() {
        if (mesh != null) {
            mesh.drawMesh();
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
        writer.writeObjectField("mesh", "cube");
        writer.writeObjectField("collidable", "cube");
        writer.writeEndObject();
    }

    @Override
    public void load(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        reader.consumeStringField("mesh");
        reader.consumeStringField("collidable");
        mesh = Mesh.CUBE;
        collidable = new Cube();
        reader.consumeEndObject();
    }
}
