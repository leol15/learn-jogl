package com.play.app.geometry;

import java.util.HashMap;
import java.util.Map;

import com.play.app.basics.Collidable;

import org.joml.Vector3f;

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

    public static Vector3f collide(Collidable a, Collidable b) {
        val resolver = COLLISION_RESOLVERS.get(TO_KEY(a, b));
        if (resolver == null) {
            log.warn("Unsupported collision for {}", TO_KEY(a, b));
            return null;
        }
        return resolver.resolve(a, b);
    }

    /////////////////////////
    // actual collision detection,does not need revese arguments
    /////////////////////////

    private static Vector3f cubeCubeCollision(final Cube c1, final Cube c2) {
        return null;
    }

    private static Vector3f cubeRayCollision(final Cube cube, final Ray ray) {
        return null;
    }

    private static Vector3f rayRayCollision(final Ray r1, final Ray r2) {
        return null;
    }

    /////////////////////////
    // magical hack
    /////////////////////////

    private interface Resolver<T1, T2> {
        public Vector3f resolve(T1 a, T2 b);
    }

    private static <T1 extends Collidable, T2 extends Collidable> void ADD_ENTRY(Class<?> a, Class<?> b,
            Resolver<? super T1, ? super T2> r) {
        // ugly cast is hidden here
        Resolver<? super Collidable, ? super Collidable> realResolver = (Resolver) r;
        COLLISION_RESOLVERS.put(TO_KEY(a, b), realResolver);
        COLLISION_RESOLVERS.put(TO_KEY(a, b), (o1, o2) -> realResolver.resolve(o1, o2));
    }

    private static String TO_KEY(Collidable a, Collidable b) {
        return TO_KEY(a.getClass(), b.getClass());
    }

    private static String TO_KEY(Class<?> a, Class<?> b) {
        return String.format("%s$$%s", a.getName(), b.getName());
    }

}
