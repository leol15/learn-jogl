package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.geometry.Ray;
import com.play.app.scene.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.*;

import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Accessors(chain = true)
@Log4j2
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
        shape.addToEditor(editor);
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitSimpleSceneObject(this, worldTransform);
    }

    @Override
    public void save(YAMLGenerator generator) throws IOException {
        generator.writeStartObject();
        WorldSerializer.writeObjectType(this.getClass(), generator);
        WorldSerializer.writeObjectField("property", property, generator);
        WorldSerializer.writeObjectField("shape", shape, generator);
        generator.writeEndObject();
    }

    @Override
    public boolean hasTransparency() {
        return property.material.hasTransparency();
    }

}
