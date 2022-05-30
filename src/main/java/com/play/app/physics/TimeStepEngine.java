package com.play.app.physics;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.play.app.physics.interactions.Interaction;

import lombok.extern.log4j.Log4j2;

/**
 * Automatically updates the TimedUpdate objects that is given
 * Also invokes all interaction before every time step
 */
@Log4j2
public class TimeStepEngine {

    private ConcurrentHashMap<String, Interaction> interactions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimedUpdate> objects = new ConcurrentHashMap<>();

    public boolean paused = false;
    public boolean terminated = false;

    public TimeStepEngine() {
        // start a thread
        new Thread(this::run).start();
    }

    public void addObject(TimedUpdate o) {
        objects.put(o.toString(), o);
    }

    public void addInteraction(Interaction i) {
        interactions.put(i.toString(), i);
    }

    private void run() {
        log.info("Physics Engine thread runnnning");
        while (!terminated) {
            long previousTime = System.currentTimeMillis();
            while (!paused && !terminated) {
                final long dt = System.currentTimeMillis() - previousTime;
                calculateInteractions();
                updateObjects(dt / 1000f); // in seconds
                if (dt < 10) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                previousTime += dt;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private void updateObjects(float dt) {
        final Enumeration<TimedUpdate> os = objects.elements();
        while (os.hasMoreElements()) {
            os.nextElement().update(dt);
        }
    }

    private void calculateInteractions() {
        final Enumeration<Interaction> is = interactions.elements();
        while (is.hasMoreElements()) {
            is.nextElement().prepareStep();
        }
    }

}
