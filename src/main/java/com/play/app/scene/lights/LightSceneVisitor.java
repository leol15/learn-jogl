package com.play.app.scene.lights;

import com.play.app.scene.SceneObjectVisitor;
import com.play.app.scene.sceneobject.*;

import org.joml.Matrix4f;

public class LightSceneVisitor implements SceneObjectVisitor {

    @Override
    public void visitLightSceneObject(LightSceneObject object, Matrix4f worldTransform) {
        // collect the light to shader
        final Light light = object.getLight();
        if (light != null) {
            light.addToUBO(worldTransform);
        }
    }

    @Override
    public void visitSimpleSceneObject(SimpleSceneObject object, Matrix4f worldTransform) {
        // ignore
    }

    @Override
    public void visitInstancingObject(InstancingObject object, Matrix4f worldTransform) {
        // ignore
    }

}
