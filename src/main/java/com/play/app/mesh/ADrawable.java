package com.play.app.mesh;

import java.util.HashSet;
import java.util.Set;

import com.play.app.basics.Drawable;
import com.play.app.basics.SpacialThing;
import com.play.app.graphics.ShaderProgram;
import com.play.app.utils.CONST;
import com.play.app.utils.Func;

import org.joml.Matrix4f;

/**
 * This package knows how to draw
 * it does not directly manages spatial info. (done by geometry / Thing)
 * it manages things like shader, texture, set of geometries
 * 
 * Goal, draw them!
 */
public abstract class ADrawable implements Drawable {

    private final Matrix4f TMP_MATRIX = new Matrix4f();
    private final Matrix4f WORLD_TRANSFORM = new Matrix4f();

    protected final Set<SpacialThing> instances = new HashSet<>();
    private ShaderProgram shader;

    public ADrawable addInstance(final SpacialThing instance) {
        instances.add(instance);
        return this;
    }

    public ADrawable removeInstance(final SpacialThing instance) {
        instances.remove(instance);
        return this;
    }

    public void draw(final Matrix4f transform) {
        WORLD_TRANSFORM.set(transform);

        for (final SpacialThing instance : instances) {
            instance.getModelMatrix(TMP_MATRIX);
            TMP_MATRIX.mulLocal(WORLD_TRANSFORM);
            shader.uniformMatrix4fv(CONST.MODEL_MATRIX, TMP_MATRIX);
            shader.useProgram();
            drawInstance();
        }

        shader.unuseProgram();
    }

    public ADrawable setShader(final ShaderProgram shader) {
        this.shader = shader;
        return this;
    }

    // all the work for drawing 1 instance
    protected abstract void drawInstance();

}
