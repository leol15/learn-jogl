package com.play.app.zSupportFunc.funcs;

import com.play.app.zSupportFunc.SupportFunc;

import org.joml.Vector3f;

/**
 * Sphere with radius 1 centered at origin
 */
public class SphereSupp extends SupportFunc {

    @Override
    protected Vector3f getMax(Vector3f direction) {
        return new Vector3f(direction).normalize();
    }
}
