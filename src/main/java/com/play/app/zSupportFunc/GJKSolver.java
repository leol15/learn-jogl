package com.play.app.zSupportFunc;

import java.util.ArrayList;
import java.util.List;

import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.NormalMesh;
import com.play.app.utils.CONST;
import com.play.app.utils.ShaderUtils;
import com.play.app.zSupportFunc.funcs.CompositeSupp;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import lombok.extern.log4j.Log4j2;

/**
 * Collision detection algo for geometry that is defined by
 * a support function
 */
@Log4j2
public class GJKSolver {

    private static NormalMesh debugMesh;
    private static ShaderProgram debugShader;
    private static final List<Vector3f> debugPositions = new ArrayList<>();
    private static final List<Vector3f> debugDirs = new ArrayList<>();
    private static int prevSize = 0;
    private static Vector4f DEBUG_COLOR = new Vector4f(1, 0, 0, 1);

    // draw the simplex for debugging
    public static void drawDebugSimpelx() {
        if (debugMesh == null) {
            debugShader = ShaderUtils.getShader("Basic");
            debugMesh = new NormalMesh();
            debugMesh.setGlPrimitiveType(GL11.GL_LINE_STRIP);
        }

        if (debugPositions.size() == 0) {
            return;
        }

        if (prevSize != debugPositions.size()) {
            prevSize = debugPositions.size();
            log.debug("simplex size {}", prevSize);
            for (int i = 0; i < debugPositions.size(); i++) {
                log.debug("p {}", debugPositions.get(i));
                log.debug("dir {}", debugDirs.get(i));
            }
        }
        debugMesh.reset();
        for (int i = 0; i < debugPositions.size(); i++) {
            final Vector3f p = debugPositions.get(i);
            debugMesh.addPos(p);
            debugMesh.addNormal(0, 0, 1);
            debugMesh.addElement(i);
        }
        debugMesh.uploadData();

        debugShader.uniform4f(CONST.MATERIAL_COLOR, DEBUG_COLOR);
        debugShader.useProgram();

        // debugMesh.getVao().bind();
        // // glDrawElements(glPrimitiveType, triangles.size(), GL_UNSIGNED_INT, 0);
        // GL11.glDrawElements(GL11.GL_LINE_STRIP, 5, GL11.GL_UNSIGNED_INT, 0);
        // debugMesh.getVao().unbind();

        debugMesh.draw();
        debugShader.unuseProgram();
    }

    /**
     * A, B are shapes
     */
    public static boolean intersect(SupportFunc A, SupportFunc B) {

        final CompositeSupp composite = new CompositeSupp();
        composite.add(A);
        composite.subtract(B);
        debugPositions.clear();
        final Simplex simplex = new Simplex();
        final Vector3f dir = new Vector3f(0, 1, 0);
        final Vector3f p = composite.getMaxWorld(dir);
        debugPositions.add(new Vector3f(p));
        debugDirs.add(new Vector3f(dir));
        simplex.points.add(new Vector3f(p));
        if (p.length() < 0.001f) {
            return true;
        }
        dir.set(p).mul(-1).normalize();

        int itr = 0;
        while (true) {
            // log.debug("itr = {}", ++itr);
            // log.debug("[");
            // for (final Vector3f v : simplex.points) {
            //     log.debug(">>> {}", v);
            // }
            // log.debug("]");
            // go in direction of origin
            final Vector3f p2 = composite.getMaxWorld(dir);
            debugPositions.add(new Vector3f(p2));
            debugDirs.add(new Vector3f(dir));
            // log.debug("p2 {} dir {}", p2, dir);
            // check if 0 is inside
            if (p2.dot(dir) < 0) {
                // log.debug("not colliding");
                // debugging
                return false;
            }

            // expand simplex
            simplex.points.add(p2);
            if (simplex.containsOrigin(dir)) {
                // log.debug("collided");
                return true;
            }

            // continue search, dir is updated
            dir.normalize();
        }

    }

    private static class Simplex {
        public final List<Vector3f> points = new ArrayList<>();

        public boolean containsOrigin(final Vector3f nextDir) {
            if (points.size() == 0) {
                return false;
            } else if (points.size() == 1) {
                nextDir.set(points.get(0)).mul(-1);
                return points.get(0).distance(0, 0, 0) < 0.001f;
            } else if (points.size() == 2) {
                // line case
                final Vector3f B = points.get(0);
                final Vector3f A = points.get(1);
                final Vector3f AB = new Vector3f(B).sub(A);

                // which side of the line is the origin on?
                AB.cross(A, nextDir);
                if (nextDir.length() == 0) {
                    // origin on the line AB
                    return true;
                }
                AB.cross(nextDir, nextDir);
                return false;
            } else if (points.size() == 3) {
                // triangle case
                final Vector3f C = points.get(0);
                final Vector3f B = points.get(1);
                final Vector3f A = points.get(2);

                final Vector3f BC = new Vector3f(C).sub(B);
                final Vector3f BA = new Vector3f(A).sub(B);
                final Vector3f AC = new Vector3f(C).sub(A);
                final Vector3f up = new Vector3f(BC).cross(BA);
                final Vector3f outBA = new Vector3f(up).cross(BA);
                final Vector3f outAC = new Vector3f(up).cross(AC);

                // check regions
                if (outBA.dot(A) < 0) {
                    // throw away C
                    A.cross(BA, nextDir).cross(BA);
                    points.remove(C);
                    debugPositions.remove(0);
                    debugDirs.remove(0);
                    return false;
                } else if (outAC.dot(A) < 0) {
                    // throw away B
                    A.cross(AC, nextDir).cross(AC);
                    points.remove(B);
                    debugPositions.remove(1);
                    debugDirs.remove(1);
                    return false;
                } else {
                    // above or below triangle
                    final float d = up.dot(A);
                    if (Math.abs(d) < 0.001f) {
                        return true;
                    } else if (d < 0) {
                        nextDir.set(up);
                    } else {
                        nextDir.set(up).mul(-1);
                    }
                }
            } else {
                return true;
                // log.error("TODO implement check for {}-simplex", points.size());
            }
            return false;
        }
    }
}
