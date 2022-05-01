package com.play.app.scene;

import java.util.HashSet;
import java.util.Set;

import com.play.app.basics.Drawable;
import com.play.app.basics.Thing;

import org.joml.Matrix4f;

public class SceneNode extends Thing implements Drawable {

    private Set<SceneNode> children = new HashSet<>();
    private Drawable drawable;

    private final Matrix4f tmpMatrix = new Matrix4f();

    private void drawSelf(final Matrix4f transform) {
        if (drawable != null)
            drawable.draw(transform);
    }

    public SceneNode setDrawable(Drawable drawable) {
        this.drawable = drawable;
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
        getModelMatrix(tmpMatrix);
        tmpMatrix.mulLocal(transform);

        for (final SceneNode child : children)
            child.draw(tmpMatrix);
        drawSelf(tmpMatrix);
    }

}
