package com.play.app.zSupportFunc.funcs;

import java.util.ArrayList;
import java.util.List;

import com.play.app.zSupportFunc.SupportFunc;

import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

/**
 * Support function that is simply a collection of points
 */
@Log4j2
public abstract class AbstractVertexSupp extends SupportFunc {

    protected final List<Vector3f> vertices = new ArrayList<>();

    @Override
    final protected Vector3f getMax(Vector3f direction) {
        Vector3f maxV = null;
        float maxProj = -1;
        for (final Vector3f v : vertices) {
            final float proj = v.dot(direction);
            if (proj > maxProj || maxV == null) {
                maxProj = proj;
                maxV = v;
            }
        }
        if (maxV == null) {
            log.error("SupportFunc has no vertices, size [{}]", vertices.size());
        }
        return new Vector3f(maxV);
    }
}
