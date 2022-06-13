package com.play.app.physics.physicsentities;

import java.util.List;

import com.play.app.physics.Accumulator;
import com.play.app.physics.CollisionInstance;

import org.joml.Vector3f;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * This is a real particle in Physics, aka point mass
 */
@RequiredArgsConstructor
@Log4j2
public class Particle extends PhysicsEntity {
    public final Vector3f p = new Vector3f();
    public final Vector3f v = new Vector3f();
    public final float mass;

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

    @Override
    public List<CollisionInstance> getCollisions(PhysicsEntity other) {
        // TODO Auto-generated method stub
        return null;
    }

}
