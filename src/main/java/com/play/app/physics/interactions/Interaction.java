package com.play.app.physics.interactions;

/**
 * Bad name,,,
 * This is interface for things that prepare a time step
 * Like a spring acting on two particle
 */
public interface Interaction {
    public void prepareStep();
}
