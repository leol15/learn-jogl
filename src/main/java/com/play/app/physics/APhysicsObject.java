package com.play.app.physics;

import com.play.app.basics.SpacialThing;

import org.joml.Vector3f;

/**
 * This package does not know how to draw
 * but they have all the spatial information
 * they can also handle intersections / collision detection
 */
public class APhysicsObject {

    protected Vector3f velocity = new Vector3f();
    protected Vector3f acceleration = new Vector3f();
    protected SpacialThing spacialInfo = new SpacialThing();

}
