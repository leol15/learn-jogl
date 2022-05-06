package com.play.app.geometry;

import java.util.HashMap;
import java.util.Map;

import com.play.app.basics.Collidable;
import com.play.app.utils.Func;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import lombok.val;
import lombok.extern.log4j.Log4j2;

// bad design :( kinda
// visitor pattern still violates OCP for Geometry class
@Log4j2
public class CollisionDetector {

    public static final Map<String, Resolver<? super Collidable, ? super Collidable>> COLLISION_RESOLVERS = new HashMap<>();

    /////////////////////////
    // one for each method
    /////////////////////////

    static {
        // reversal is taken care of
        ADD_ENTRY(Cube.class, Cube.class, CollisionDetector::cubeCubeCollision);
        ADD_ENTRY(Cube.class, Ray.class, CollisionDetector::cubeRayCollision);
        ADD_ENTRY(Ray.class, Ray.class, CollisionDetector::rayRayCollision);
    }

    public static Vector3f collide(Collidable a, Collidable b,
            final Matrix4f aTransform, final Matrix4f bTransform) {
        val resolver = COLLISION_RESOLVERS.get(TO_KEY(a, b));
        if (resolver == null) {
            log.warn("Unsupported collision for {}", TO_KEY(a, b));
            return null;
        }
        return resolver.resolve(a, b, aTransform, bTransform);
    }

    /////////////////////////
    // actual collision detection,does not need revese arguments
    /////////////////////////

    private static Vector3f cubeCubeCollision(final Cube c1, final Cube c2,
            final Matrix4f aTransform, final Matrix4f bTransform) {
        return null;
    }

    private static Vector3f cubeRayCollision(final Cube cube, final Ray ray,
            final Matrix4f cubeTransform, final Matrix4f rayTransform) {
        // transform ray
        final Matrix4f cubeInverse = new Matrix4f();
        cubeTransform.invert(cubeInverse);

        final Vector4f rayStart = Func.toVec4(ray.start);
        rayStart.mul(rayTransform).mul(cubeInverse);

        final Vector4f rayDir = new Vector4f(ray.direction, 0);
        rayDir.mul(rayTransform).mul(cubeInverse);

        final Vector3f rayStart3 = Func.toVec3(rayStart);
        final Vector3f rayDir3 = Func.toVec3(rayDir);
        // intersect with unit cube
        final Vector3f origin = new Vector3f(0, 0, 0);
        final Vector3f otherCorner = new Vector3f(1, 1, 1);
        Vector3f intersect = rayRectIntersection(rayStart3, rayDir3,
                origin,
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0));
        if (intersect != null) {
            return intersect;
        }
        intersect = rayRectIntersection(rayStart3, rayDir3,
                origin,
                new Vector3f(1, 0, 0),
                new Vector3f(0, 0, 1));
        if (intersect != null) {
            return intersect;
        }
        intersect = rayRectIntersection(rayStart3, rayDir3,
                origin,
                new Vector3f(0, 1, 0),
                new Vector3f(0, 0, 1));
        if (intersect != null) {
            return intersect;
        }
        intersect = rayRectIntersection(rayStart3, rayDir3,
                otherCorner,
                new Vector3f(-1, 0, 0),
                new Vector3f(0, -1, 0));
        if (intersect != null) {
            return intersect;
        }
        intersect = rayRectIntersection(rayStart3, rayDir3,
                otherCorner,
                new Vector3f(-1, 0, 0),
                new Vector3f(0, 0, -1));
        if (intersect != null) {
            return intersect;
        }
        intersect = rayRectIntersection(rayStart3, rayDir3,
                otherCorner,
                new Vector3f(0, -1, 0),
                new Vector3f(0, 0, -1));
        if (intersect != null) {
            return intersect;
        }
        return intersect;
    }

    private static Vector3f rayRayCollision(final Ray r1, final Ray r2,
            final Matrix4f aTransform, final Matrix4f bTransform) {
        // no, rays don't collide
        return null;
    }

    /////////////////////////
    // helper
    /////////////////////////

    /**
     * 
     * @param rayStart
     * @param rayDir
     * @param rectBase
     * @param sideA side starting from rectbase
     * @param sideB the other side starting from rectbase
     * @return
     */
    private static Vector3f rayRectIntersection(final Vector3f rayStart, final Vector3f rayDir,
            final Vector3f rectBase, final Vector3f sideA, final Vector3f sideB) {

        Vector3f planeIntersect = rayPlaneIntersection(rayStart, rayDir, rectBase, sideA, sideB);
        // inside outside check
        if (insideRect(planeIntersect, rectBase, sideA, sideB)) {
            return planeIntersect;
        }
        return null;
    }

    private static Vector3f rayTriangleIntersection(final Vector3f rayStart, final Vector3f rayDir,
            final Vector3f triangleBase, final Vector3f sideA, final Vector3f sideB) {

        Vector3f planeIntersect = rayPlaneIntersection(rayStart, rayDir, triangleBase, sideA, sideB);
        // inside outside check
        if (insideTriangle(planeIntersect, triangleBase, sideA, sideB)) {
            return planeIntersect;
        }
        return null;
    }

    private static boolean insideTriangle(Vector3f planeIntersect, Vector3f triangleBase, Vector3f sideA,
            Vector3f sideB) {
        // barycentric coordiate check
        final Vector3f toP = new Vector3f();
        planeIntersect.sub(triangleBase, toP);

        final float l00 = sideA.dot(sideA);
        final float l01 = sideA.dot(sideB);
        final float l02 = sideA.dot(toP);
        final float l11 = sideB.dot(sideB);
        final float l12 = sideB.dot(toP);

        final float mul = 1 / (l00 * l11 - l01 * l01);
        final float u = (l00 * l12 - l01 * l02) * mul;
        final float v = (l11 * l02 - l01 * l12) * mul;

        return u >= 0 && v >= 0 && u + v <= 2;
    }

    // rect is actually a parallelogram
    private static boolean insideRect(Vector3f planeIntersect, Vector3f triangleBase, Vector3f sideA,
            Vector3f sideB) {
        // barycentric coordiate check
        final Vector3f toP = new Vector3f();
        planeIntersect.sub(triangleBase, toP);

        final float l00 = sideA.dot(sideA);
        final float l01 = sideA.dot(sideB);
        final float l02 = sideA.dot(toP);
        final float l11 = sideB.dot(sideB);
        final float l12 = sideB.dot(toP);

        final float mul = 1 / (l00 * l11 - l01 * l01);
        final float u = (l00 * l12 - l01 * l02) * mul;
        final float v = (l11 * l02 - l01 * l12) * mul;

        return u >= 0 && u <= 1 && v >= 0 && v <= 1;
    }

    private static Vector3f rayPlaneIntersection(final Vector3f rayStart, final Vector3f rayDir,
            final Vector3f triangleBase, final Vector3f sideA, final Vector3f sideB) {
        rayDir.normalize();

        final Vector3f normal = new Vector3f();
        sideA.cross(sideB, normal);
        normal.normalize();

        final Vector3f toTriangleBase = new Vector3f();
        triangleBase.sub(rayStart, toTriangleBase);

        final float distanceToPlane = toTriangleBase.dot(normal);
        final float ratio = rayDir.dot(normal);
        final float t = distanceToPlane / ratio;

        final Vector3f intersect = new Vector3f();
        rayStart.add(rayDir.mul(t, normal), intersect);

        return intersect;
    }

    /////////////////////////
    // magical hack
    /////////////////////////

    private interface Resolver<T1, T2> {
        public Vector3f resolve(T1 a, T2 b, Matrix4f aTransform, Matrix4f bTransform);
    }

    private static <T1 extends Collidable, T2 extends Collidable> void ADD_ENTRY(Class<?> a, Class<?> b,
            Resolver<? super T1, ? super T2> r) {
        // ugly cast is hidden here
        Resolver<? super Collidable, ? super Collidable> realResolver = (Resolver) r;
        COLLISION_RESOLVERS.put(TO_KEY(a, b), realResolver);
        COLLISION_RESOLVERS.put(TO_KEY(b, a),
                (o1, o2, t1, t2) -> realResolver.resolve(o2, o1, t2, t1));
    }

    private static String TO_KEY(Collidable a, Collidable b) {
        return TO_KEY(a.getClass(), b.getClass());
    }

    private static String TO_KEY(Class<?> a, Class<?> b) {
        return String.format("%s$$%s", a.getName(), b.getName());
    }

}
