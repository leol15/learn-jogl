package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.play.app.geometry.Ray;
import com.play.app.scene.SceneObject;
import com.play.app.scene.SceneObjectVisitor;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public class SimpleSceneObject implements SceneObject {

    public final SORenderProperty property = new SORenderProperty();
    public final SOShape shape = new SOShape();

    @Override
    public void draw(Matrix4f transform) {
        property.bind(transform);
        shape.draw();
        property.unbind();
    }

    @Override
    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix) {
        return shape.intersectRay(ray, worldMatrix);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        property.addToEditor(editor);
        editor.addDivider();
        shape.addToEditor(editor);
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitSimpleSceneObject(this, worldTransform);
    }

    @Override
    public boolean hasTransparency() {
        return property.material.hasTransparency();
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("property", property);
        writer.writeObjectField("shape", shape);
        writer.writeEndObject();
    }

    public static SimpleSceneObject create(WorldSerializer reader) throws IOException {
        final SimpleSceneObject sso = new SimpleSceneObject();
        reader.consumeStartObject();
        reader.consumeObjectField("property", sso.property);
        reader.consumeObjectField("shape", sso.shape);
        reader.consumeEndObject();
        return sso;
    }
}
