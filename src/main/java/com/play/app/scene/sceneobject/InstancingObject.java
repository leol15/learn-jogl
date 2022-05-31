package com.play.app.scene.sceneobject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonToken;
import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;
import com.play.app.scene.SceneObject;
import com.play.app.scene.SceneObjectVisitor;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public class InstancingObject implements SceneObject {

    public final SORenderProperty property = new SORenderProperty();
    public final SOShape shape = new SOShape();

    private final Set<SpacialThing> instances = new HashSet<>();

    // helper
    private final Matrix4f tmpMatrix = new Matrix4f();

    public InstancingObject addInstance(final SpacialThing t) {
        instances.add(t);
        return this;
    }

    public InstancingObject removeInstance(final SpacialThing t) {
        instances.remove(t);
        return this;
    }

    @Override
    public void draw(Matrix4f transform) {

        property.bind(transform);

        for (final SpacialThing modelInfo : instances) {
            // override the instance model matrix
            if (property.shader != null) {
                modelInfo.getModelMatrix(tmpMatrix);
                tmpMatrix.mulLocal(transform);
                property.bind(tmpMatrix);
            }
            shape.draw();
        }

        property.unbind();
    }

    // return the first point of intersection
    @Override
    public Vector3f intersectRay(final Ray ray, final Matrix4f worldMatrix) {
        if (shape.collider == null) {
            return null;
        }
        final Matrix4f tmpMat = new Matrix4f();
        for (final SpacialThing model : instances) {
            model.getModelMatrix(tmpMat);
            tmpMat.mulLocal(worldMatrix);

            final Vector3f intersect = shape.intersectRay(ray, tmpMat);
            if (intersect != null) {
                return intersect;
            }
        }

        return null;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        property.addToEditor(editor);
        shape.addToEditor(editor);
        // TODO not really good to edit all instance?
        if (instances.size() > 0) {
            for (SpacialThing s : instances) {
                editor.addProperty("instance 0", s);
                break;
            }
        }
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitInstancingObject(this, worldTransform);
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("property", property);
        writer.writeObjectField("shape", shape);
        writer.writeArrayFieldStart("instances");
        for (final SpacialThing i : instances) {
            writer.writeObject(i);
        }
        writer.writeEndArray();
        writer.writeEndObject();
    }

    public static InstancingObject create(WorldSerializer reader) throws IOException {
        final InstancingObject io = new InstancingObject();
        reader.consumeStartObject();

        reader.consumeObjectField("property", io.property);
        reader.consumeObjectField("shape", io.shape);

        reader.consumeArrayFieldStart("instances");
        while (reader.currentToken() != JsonToken.END_ARRAY) {
            final SpacialThing i = new SpacialThing();
            i.load(reader);
            io.addInstance(i);
        }
        reader.consumeEndArray();

        reader.consumeEndObject();
        return io;
    }

    @Override
    public boolean hasTransparency() {
        return property.material.hasTransparency();
    }

}
