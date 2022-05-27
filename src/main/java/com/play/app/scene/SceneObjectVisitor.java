package com.play.app.scene;

import com.play.app.scene.sceneobject.*;

import org.joml.Matrix4f;

public interface SceneObjectVisitor {

    public void visitSimpleSceneObject(SimpleSceneObject object, Matrix4f worldTransform);

    public void visitInstancingObject(InstancingObject object, Matrix4f worldTransform);

    public void visitLightSceneObject(LightSceneObject object, Matrix4f worldTransform);

}
