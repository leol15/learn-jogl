package com.play.app.physics;

import org.joml.Vector3f;

/**
 * Thing that physics objects react to each time step
 */
public class Accumulator {
    public final Vector3f force = new Vector3f();

    public void clear() {
        force.set(0);
    }
}
