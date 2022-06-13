package com.play.app.physics;

import java.util.ArrayList;
import java.util.List;

import com.play.app.physics.globalactors.GlobalActor;
import com.play.app.physics.interactions.Interaction;
import com.play.app.physics.physicsentities.PhysicsEntity;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Manages all physical interactions between Physics Entities
 */
@Log4j2
public class PhysicsEngine {

    @Setter
    private boolean stop = false;
    @Setter
    private boolean pause = false;

    private final List<GlobalActor> globalActors = new ArrayList<>();
    private final List<Interaction> interactions = new ArrayList<>();
    private final List<PhysicsEntity> entities = new ArrayList<>();

    private final Thread engineThread;

    public PhysicsEngine() {
        engineThread = new Thread(this::run);
        engineThread.start();
    }

    public void addObject(PhysicsEntity o) {
        entities.add(o);
    }

    public void addInteraction(Interaction i) {
        interactions.add(i);
    }

    public void addGlobalActor(GlobalActor g) {
        globalActors.add(g);
    }

    private void run() {
        log.info("PhysicsEngine thread runnning");
        while (!stop) {
            long lastTime = System.currentTimeMillis();
            while (!pause && !stop) {
                if (System.currentTimeMillis() - lastTime < 16) {
                    nap(16);
                }
                long dt = System.currentTimeMillis() - lastTime;
                lastTime += dt;
                update(dt);
            }
            nap(100);
        }
    }

    /**
     * Main loop
     * Handle collision?
     * 1. detect colliding Entities
     */
    private void update(long ms) {
        final float seconds = ms / 1000f;
        // apply global things
        globalActors.forEach(g -> {
            entities.forEach(e -> g.apply(e));
        });

        // apply interactions
        interactions.forEach(i -> i.prepareStep());

        // collision
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                final PhysicsEntity e1 = entities.get(i);
                final PhysicsEntity e2 = entities.get(j);
                final List<CollisionInstance> collisions = e1.getCollisions(e2);
                resolveCollisions(collisions);
            }
        }

        // TODO apply constraints here or in interactions?

        // update
        entities.forEach(e -> e.update(seconds));
    }

    private void resolveCollisions(List<CollisionInstance> collisions) {
        // TODO
    }

    private void nap(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
