package com.play.app.zSupportFunc;

import com.play.app.basics.SpacialThing;
import com.play.app.mesh.Mesh;
import com.play.app.utils.Func;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * A support function fully describe a shape, in local space
 * Only 1 public function
 */
@Log4j2
public abstract class SupportFunc {
    @Setter
    private SpacialThing model;
    private final Matrix4f modelMat = new Matrix4f();

    final public Vector3f getMaxWorld(Vector3f worldDirection) {
        if (model == null) {
            return getMax(worldDirection);
        }

        // transform into local space
        model.getModelMatrix(modelMat);
        modelMat.invertAffine();
        final Vector3f dirLocal = Func.multMat(worldDirection, 0, modelMat);

        final Vector3f posLocal = getMax(dirLocal);

        // to world space
        model.getModelMatrix(modelMat);
        return Func.multMat(posLocal, 1, modelMat);
    }

    // debugging
    public Mesh getDebugMesh() {
        return SupportDrawer.toMesh(this);
    }

    // gets the points on shape with maximum projection on direction
    // direction is normalized
    protected abstract Vector3f getMax(Vector3f direction);

}
