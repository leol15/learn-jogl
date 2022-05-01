package com.play.app.geometry;

import com.play.app.basics.Thing;

import org.joml.Vector3f;

/**
 * This package does not know how to draw
 * but they have all the spatial information
 * they can also handle intersections / collision detection
 */
public class APhysicsObject extends Thing {

    protected Vector3f velocity = new Vector3f();
    protected Vector3f acceleration = new Vector3f();

}
