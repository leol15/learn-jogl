package com.play.app.scene;

import com.play.app.basics.Editable;
import com.play.app.basics.Savable;
import com.play.app.basics.Selectable;

import org.joml.Matrix4f;

/**
 * An object in the scene, that can be
 * drawn, selected
 */
public interface SceneObject extends Selectable, Editable, Savable {
    // perform some special action
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform);

    // to try handle transparency
    public boolean hasTransparency();

    // draw the object given the transform
    public void draw(Matrix4f worldTransform);
}
