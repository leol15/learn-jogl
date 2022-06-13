package com.play.app.physics;

import com.play.app.physics.physicsentities.PhysicsEntity;

import org.joml.Vector3f;

import lombok.RequiredArgsConstructor;

/**
 * stores collision info
 */
@RequiredArgsConstructor
public class CollisionInstance {
    public final Vector3f location = new Vector3f();
    public final PhysicsEntity A, B;
    public final Vector3f ANormal = new Vector3f(), BNormal = new Vector3f();
}
