package com.play.app.zSupportFunc.funcs;

import org.joml.Vector3f;

/**
 * A unit square
 */
public class SquareSupp extends AbstractVertexSupp {

    public SquareSupp() {
        vertices.add(new Vector3f(0, 0, 0));
        vertices.add(new Vector3f(1, 0, 0));
        vertices.add(new Vector3f(1, 1, 0));
        vertices.add(new Vector3f(0, 1, 0));
    }
}
