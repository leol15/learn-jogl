package com.play.app.scene;

import java.util.HashSet;
import java.util.Set;

import com.play.app.basics.Drawable;
import com.play.app.basics.SpacialThing;

import org.joml.Matrix4f;

public class SceneNode implements Drawable {

    public final SpacialThing modelInfo = new SpacialThing();

    private Set<SceneNode> children = new HashSet<>();
    private final Matrix4f tmpMatrix = new Matrix4f();
    private SceneObject sceneObject;

    public SceneNode setSceneObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
        return this;
    }

    public SceneNode addChild(final SceneNode child) {
        children.add(child);
        return this;
    }

    public SceneNode removeChild(final SceneNode child) {
        children.remove(child);
        return this;
    }

    @Override
    public void draw(final Matrix4f transform) {
        // apply current transform
        modelInfo.getModelMatrix(tmpMatrix);
        tmpMatrix.mulLocal(transform);

        if (sceneObject != null) {
            sceneObject.draw(tmpMatrix);
        }

        // in case argument is changed
        modelInfo.getModelMatrix(tmpMatrix);
        tmpMatrix.mulLocal(transform);

        for (final SceneNode child : children) {
            child.draw(tmpMatrix);
        }

    }

}
