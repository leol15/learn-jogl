package com.play.app.physics.globalactors;

import com.play.app.physics.physicsentities.PhysicsEntity;

/**
 * Things that affect physics entity in one direction
 */
public interface GlobalActor {
    public void apply(PhysicsEntity entity);
}
