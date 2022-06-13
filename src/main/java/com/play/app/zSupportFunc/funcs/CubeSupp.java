package com.play.app.zSupportFunc.funcs;

import org.joml.Vector3f;

/**
 * Cube with side length 1
 */
public class CubeSupp extends AbstractVertexSupp {

    public CubeSupp() {
        vertices.add(new Vector3f(0, 0, 0));
        vertices.add(new Vector3f(1, 0, 0));
        vertices.add(new Vector3f(1, 1, 0));
        vertices.add(new Vector3f(0, 1, 0));

        vertices.add(new Vector3f(0, 0, 1));
        vertices.add(new Vector3f(1, 0, 1));
        vertices.add(new Vector3f(1, 1, 1));
        vertices.add(new Vector3f(0, 1, 1));
    }

}
