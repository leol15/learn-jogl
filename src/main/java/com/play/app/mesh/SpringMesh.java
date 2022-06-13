package com.play.app.mesh;

import java.io.IOException;

import com.play.app.physics.*;
import com.play.app.physics.interactions.Spring;
import com.play.app.physics.physicsentities.Particle;
import com.play.app.utils.WorldSerializer;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringMesh implements Mesh {

    final NormalMesh mesh;

    public SpringMesh(PhysicsEngine physicsEngine) {
        mesh = new NormalMesh();

        Particle pA = new Particle(0.2f);
        Particle pB = new Particle(0.2f);
        Particle pC = new Particle(0.2f);
        Particle pD = new Particle(0.2f);
        pA.p.set(0, 0, 0);
        pB.p.set(0, 1, 0);
        pC.p.set(1, 1, 0);
        pD.p.set(1, 0, 0);
        mesh.addPos(pA.p);
        mesh.addPos(pB.p);
        mesh.addPos(pC.p);
        mesh.addPos(pD.p);

        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);

        mesh.addTriangle(0, 1, 2);
        mesh.addTriangle(0, 2, 3);
        mesh.uploadData();

        physicsEngine.addInteraction(new Spring(pA, pB, 0.8f, 0.4f));
        // physicsEngine.addInteraction(new Spring(pA, pC, 0.8f, 0.4f));
        physicsEngine.addInteraction(new Spring(pA, pD, 0.8f, 0.4f));
        physicsEngine.addInteraction(new Spring(pB, pC, 0.8f, 0.4f));
        physicsEngine.addInteraction(new Spring(pB, pD, 0.8f, 0.4f));
        physicsEngine.addInteraction(new Spring(pC, pD, 0.8f, 0.4f));

        physicsEngine.addObject(pA);
        physicsEngine.addObject(pB);
        physicsEngine.addObject(pC);
        physicsEngine.addObject(pD);
    }

    @Override
    public void draw() {
        mesh.uploadData();
        mesh.draw();
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        // TOD save extra info?
        writer.writeStartObject();
        writer.writeInterfaceField("mesh", mesh);
        writer.writeStartObject();
    }

}
