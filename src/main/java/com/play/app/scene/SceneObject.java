package com.play.app.scene;

import com.play.app.basics.*;

import org.joml.Matrix4f;

/**
 * An object in the scene, that can be
 * drawn, selected
 */
public interface SceneObject extends Drawable, Selectable {
    // perform some special action
    public void accept(SceneVisitor visitor, Matrix4f worldTransform);
}
