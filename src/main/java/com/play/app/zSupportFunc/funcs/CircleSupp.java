package com.play.app.zSupportFunc.funcs;

import com.play.app.zSupportFunc.SupportFunc;

import org.joml.Vector3f;

/**
 * Circle with radius 1 centered at origin
 */
public class CircleSupp extends SupportFunc {

    @Override
    protected Vector3f getMax(Vector3f direction) {
        final Vector3f dir = new Vector3f(direction);
        dir.z = 0;
        if (dir.length() < 0.0001f) {
            dir.set(1, 0, 0);
        }
        dir.normalize();
        return dir;
    }

}
