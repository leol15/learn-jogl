package com.play.app.physics.physicsentities;

import java.util.List;

import com.play.app.physics.CollisionInstance;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import lombok.RequiredArgsConstructor;

/**
 * A RigidBody
 */
@RequiredArgsConstructor
public class RigidBody extends PhysicsEntity {

    // fully characterized by these
    private final float mass;
    private final Vector3f centerOfMass = new Vector3f();
    private final Vector3f velocity = new Vector3f();
    // private final InertiaTensor momentOfInertia
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f angularVelocity = new Vector3f();

    @Override
    public void update(float dt) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<CollisionInstance> getCollisions(PhysicsEntity other) {
        // TODO Auto-generated method stub
        return null;
    }

}
