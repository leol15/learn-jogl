package com.play.app.scene;

import java.util.*;

import com.play.app.basics.*;
import com.play.app.geometry.Ray;
import com.play.app.mesh.Mesh;
import com.play.app.utils.CONST;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Accessors(chain = true)
public class InstancingObject extends SOBase implements SceneObject {

    @Setter
    private Mesh mesh;
    @Setter
    private Collidable collidable;
    private final Set<SpacialThing> instances = new HashSet<>();

    // helper
    private final Matrix4f tmpMatrix = new Matrix4f();

    public InstancingObject addInstance(final SpacialThing t) {
        instances.add(t);
        return this;
    }

    public InstancingObject removeInstance(final SpacialThing t) {
        instances.remove(t);
        return this;
    }

    @Override
    public void draw(Matrix4f transform) {
        if (mesh == null) {
            return;
        }

        bindAll();

        for (final SpacialThing modelInfo : instances) {
            if (shader != null) {
                modelInfo.getModelMatrix(tmpMatrix);
                tmpMatrix.mulLocal(transform);
                shader.uniformMatrix4fv(CONST.MODEL_MATRIX, tmpMatrix);
                shader.useProgram();
            }
            mesh.drawMesh();
        }

        unbindAll();
    }

    // return the first point of intersection
    @Override
    public Vector3f intersectRay(final Ray ray, final Matrix4f worldMat) {
        if (collidable == null) {
            return null;
        }
        final Matrix4f rayMatIdentity = new Matrix4f();
        final Matrix4f tmpMat = new Matrix4f();
        for (final SpacialThing model : instances) {
            model.getModelMatrix(tmpMat);
            tmpMat.mulLocal(worldMat);

            final Vector3f intersect = collidable.collide(ray, tmpMat, rayMatIdentity);
            if (intersect != null) {
                return intersect;
            }
        }

        return null;
    }

}
