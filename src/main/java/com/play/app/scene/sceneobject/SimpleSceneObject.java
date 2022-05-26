package com.play.app.scene.sceneobject;

import com.play.app.basics.*;
import com.play.app.geometry.Ray;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.CONST;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Accessors(chain = true)
@Log4j2
public class SimpleSceneObject extends SOBase implements SceneObject {

    // simple, only 2 fields, no transformation info (provided by SceneNode)
    @Setter
    protected Mesh mesh;
    @Setter
    private Collidable collidable;

    @Override
    public void draw(Matrix4f transform) {
        if (mesh == null) {
            return;
        }

        bindAll();

        if (shader != null) {
            shader.uniformMatrix4fv(CONST.MODEL_MATRIX, transform);
            shader.useProgram();
        }

        mesh.drawMesh();

        unbindAll();
    }

    @Override
    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix) {
        if (collidable == null) {
            return null;
        }
        final Matrix4f rayMatIdentity = new Matrix4f();

        return collidable.collide(ray, worldMatrix, rayMatIdentity);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        material.select(editor);
    }

    @Override
    public void accept(SceneVisitor visitor, Matrix4f worldTransform) {
        visitor.visitSimpleSceneObject(this, worldTransform);
    }

}
