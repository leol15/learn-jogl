package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.scene.SceneObjectVisitor;
import com.play.app.scene.lights.Light;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.Matrix4f;

public class LightSceneObject extends SimpleSceneObject {

    private Light light;

    public LightSceneObject(Light light) {
        super();

        this.light = light;
        shape.mesh = light.getDebugMesh();
        shape.collidable = light.getDebugCollidable();
        property.shader = ShaderUtils.getShader("Simple3D");
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        // does not add shape or property, they are fixed
        light.addToEditor(editor);
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitLightSceneObject(this, worldTransform);
    }

    // override the color to match the light color
    @Override
    public void draw(Matrix4f transform) {
        // override color
        property.material.color.set(light.getColor());

        property.bind(transform);
        shape.draw();
        property.unbind();
    }

    @Override
    public void save(YAMLGenerator generator) throws IOException {
        generator.writeStartObject();
        WorldSerializer.writeObjectType(this.getClass(), generator);
        WorldSerializer.writeObjectField("property", property, generator);
        WorldSerializer.writeObjectField("shape", shape, generator);
        WorldSerializer.writeObjectField("light", light, generator);
        generator.writeEndObject();
    }
}
