package com.play.app.zSupportFunc.funcs;

import org.joml.Vector3f;

import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.Mesh;
import com.play.app.mesh.UnitMesh;
import com.play.app.zSupportFunc.SupportFunc;

/**
 * Cyclinder with base circle centered at origin, 
 * diameter 1, height 1
 */
public class CyclinderSupp extends SupportFunc {

    @Override
    protected Vector3f getMax(Vector3f direction) {
        final Vector3f max = new Vector3f(direction);
        // find direction in x,z
        max.y = 0;
        // special case, x,z = 0
        if (max.lengthSquared() == 0) {
            if (direction.y > 0) {
                max.y = 1;
            } else {
                max.y = 0;
            }
        } else {
            max.normalize().div(2);
            if (direction.y > 0) {
                max.y = 1;
            } else if (direction.y < 0) {
                max.y = 0;
            } else {
                // there is a line of solution, pick 0
                max.y = 0.5f;
            }
        }
        return max;
    }

    @Override
    public Mesh getDebugMesh() {
        return new UnitMesh(Type.Cyclinder);
    }

}
