package com.play.app.physics.interactions;

import com.play.app.physics.*;

import org.joml.Vector3f;

public class Spring implements Interaction {

    private final Vector3f p1, p2;
    private final Accumulator p1Accu, p2Accu;

    // inherent property
    private final float restLengh, springConstant;

    public Spring(Particle a, Particle b, float restLengh, float springConstant) {
        p1 = a.p;
        p2 = b.p;
        p1Accu = a.accumulator;
        p2Accu = b.accumulator;
        this.restLengh = restLengh;
        this.springConstant = springConstant;
    }

    // it does not need a time step
    @Override
    public void prepareStep() {
        final Vector3f force = new Vector3f();
        final float dist = p1.distance(p2);
        p1.sub(p2, force).div(dist);
        // F = -k x
        force.mul(springConstant * (dist - restLengh));
        p1Accu.force.sub(force);
        p2Accu.force.add(force);
    }

}
