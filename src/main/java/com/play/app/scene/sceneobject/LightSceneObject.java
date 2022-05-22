package com.play.app.scene.sceneobject;

import com.play.app.geometry.Sphere;
import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneVisitor;
import com.play.app.scene.lights.*;
import com.play.app.ui.PropertyEditor;

import org.joml.Matrix4f;

public class LightSceneObject extends SimpleSceneObject {

    private Light light;

    public LightSceneObject(Light light) {
        super();
        setCollidable(new Sphere());

        this.light = light;
        setMesh(light.getDebugMesh());
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        super.addToEditor(editor);
        // add light specific things
        light.addToEditor(editor);
    }

    @Override
    public void accept(SceneVisitor visitor, Matrix4f worldTransform) {
        visitor.visitLightSceneObject(this, worldTransform);
    }
}
