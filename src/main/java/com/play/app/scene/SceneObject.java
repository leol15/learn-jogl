package com.play.app.scene;

import java.util.HashSet;
import java.util.Set;

import com.play.app.basics.Drawable;
import com.play.app.basics.SpacialThing;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Texture;
import com.play.app.mesh.Mesh;
import com.play.app.utils.CONST;

import org.joml.Matrix4f;

/**
 * An object in the scene, that can be
 * drawn, selected
 */
public class SceneObject implements Drawable {

    private ShaderProgram shader;
    private Texture texture;
    private Mesh mesh;

    private final Set<SpacialThing> instances = new HashSet<>();
    private final Matrix4f localTransform = new Matrix4f();

    public SceneObject setMesh(final Mesh m) {
        mesh = m;
        return this;
    }

    public SceneObject setTexture(final Texture t) {
        texture = t;
        return this;
    }

    public SceneObject setShader(final ShaderProgram s) {
        shader = s;
        return this;
    }

    public SceneObject addInstance(final SpacialThing t) {
        instances.add(t);
        return this;
    }

    public SceneObject removeInstance(final SpacialThing t) {
        instances.remove(t);
        return this;
    }

    @Override
    public void draw(Matrix4f transform) {
        if (mesh == null) {
            return;
        }

        if (texture != null) {
            texture.bindTexture();
        }

        for (final SpacialThing modelInfo : instances) {
            if (shader != null) {
                modelInfo.getModelMatrix(localTransform);
                localTransform.mulLocal(transform);
                shader.uniformMatrix4fv(CONST.MODEL_MATRIX, localTransform);
                shader.useProgram();
            }
            mesh.drawMesh();
        }

        if (shader != null) {
            shader.unuseProgram();
        }
        if (texture != null) {
            texture.unbindTexture();
        }

    }

}
