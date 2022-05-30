package com.play.app.physics;

import org.joml.Vector3f;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * This is a real particle in Physics, aka point mass
 * should provide some particle interface?
 */
@RequiredArgsConstructor
public class Particle implements TimedUpdate {
    public final Vector3f p = new Vector3f();
    public final Vector3f v = new Vector3f();
    public final float mass;

    public final Accumulator accumulator = new Accumulator();

    @Override
    public void update(float dt) {
        // accerlation = f / m
        v.add(
                accumulator.force.x / mass * dt,
                accumulator.force.y / mass * dt,
                accumulator.force.z / mass * dt);
        p.add(v.x * dt, v.y * dt, v.z * dt);
        accumulator.clear();
    }

}
