package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.play.app.scene.SceneObjectVisitor;
import com.play.app.scene.lights.Light;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.Matrix4f;

public class LightSceneObject extends SimpleSceneObject {

    private Light light;

    public LightSceneObject(Light light) {
        super();
        // debug shader
        property.shader = ShaderUtils.getShader("Simple3D");
        setLight(light);
    }

    public Light getLight() {
        return light;
    }

    public LightSceneObject setLight(Light l) {
        light = l;
        if (light != null) {
            shape.mesh = light.getDebugMesh();
            shape.collider = light.getDebugCollider();
        }
        return this;
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
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("property", property);
        writer.writeObjectField("shape", shape);
        writer.writeInterfaceField("light", light);
        writer.writeEndObject();
    }

    public static LightSceneObject create(WorldSerializer reader) throws IOException {
        final LightSceneObject lso = new LightSceneObject(null);
        reader.consumeStartObject();
        reader.consumeObjectField("property", lso.property);
        reader.consumeObjectField("shape", lso.shape);
        lso.setLight((Light) reader.consumeInterfaceField("light"));
        reader.consumeEndObject();
        return lso;
    }
}
