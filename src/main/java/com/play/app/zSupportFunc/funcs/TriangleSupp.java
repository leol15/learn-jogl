package com.play.app.zSupportFunc.funcs;

import org.joml.Math;
import org.joml.Vector3f;

/**
 * Triangle with base 2, height sqrt(3), with base centered at origin
 */
public class TriangleSupp extends AbstractVertexSupp {

    public TriangleSupp() {
        vertices.add(new Vector3f(1, 0, 0));
        vertices.add(new Vector3f(-1, 0, 0));
        vertices.add(new Vector3f(0, Math.sqrt(3), 0));
    }

}
