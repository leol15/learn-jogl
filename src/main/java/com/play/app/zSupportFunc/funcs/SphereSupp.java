package com.play.app.zSupportFunc.funcs;

import com.play.app.graphics.UnitGeometries;
import com.play.app.mesh.Mesh;
import com.play.app.mesh.UnitMesh;
import com.play.app.zSupportFunc.SupportFunc;

import org.joml.Vector3f;

/**
 * Sphere with radius 1 centered at origin
 */
public class SphereSupp extends SupportFunc {

    @Override
    protected Vector3f getMax(Vector3f direction) {
        return new Vector3f(direction).normalize().mul(0.5f);
    }

    @Override
    public Mesh getDebugMesh() {
        return new UnitMesh(UnitGeometries.Type.Sphere);
    }
}
