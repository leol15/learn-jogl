package com.play.app.physics.physicsentities;

import java.util.List;

import com.play.app.physics.Accumulator;
import com.play.app.physics.CollisionInstance;

import lombok.Getter;

/**
 * A thing that can be acted on by other PhysicsEntity and GlobalActor
 */
public abstract class PhysicsEntity {
    @Getter
    protected final Accumulator accumulator = new Accumulator();

    public abstract void update(float dt);

    public abstract List<CollisionInstance> getCollisions(PhysicsEntity other);

}
