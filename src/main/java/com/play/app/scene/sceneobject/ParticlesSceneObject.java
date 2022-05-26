package com.play.app.scene.sceneobject;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Cube;
import com.play.app.mesh.Mesh;
import com.play.app.scene.CameraControl;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.property.*;
import com.play.app.utils.*;

import org.joml.*;

public class ParticlesSceneObject extends SimpleSceneObject {

    private final CameraControl cameraControl;
    private final List<Particle> particles = new ArrayList<>();
    private final Matrix4f tmpMatrix = new Matrix4f();
    private final Matrix4f lastTransform = new Matrix4f();

    public BooleanProperty isBillboard = new BooleanProperty(true);
    public BooleanProperty isTransparent = new BooleanProperty(true);
    // emitter attr
    public final Vector3f force = new Vector3f(0, -.1f, 0);
    public FloatProperty emitTime = new FloatProperty(0.2f); // in seconds
    private float TTE = 0;
    // particle attr
    public FloatProperty TTL = new FloatProperty(5f); // in seconds
    public final Vector3f initalVelocity = new Vector3f(0.02f, 0.06f, 0);
    public final Vector3f intialPositionDelta = new Vector3f(0);

    public ParticlesSceneObject(CameraControl cameraControl) {
        super();

        // debug view
        setMesh(Mesh.CUBE);
        setCollidable(new Cube());
        setShader(ShaderUtils.getShader("Simple3D"));

        this.cameraControl = cameraControl;
    }

    public int getParticleCount() {
        return particles.size();
    }

    @Override
    public void draw(Matrix4f transform) {
        // transparency sort
        if (isTransparent.getValue()) {
            sortByDistToCamera(transform);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        }

        lastTransform.set(transform);

        // draw debug thing
        super.draw(transform);
        // draw the particles

        if (mesh == null) {
            return;
        }

        bindAll();

        final AxisAngle4f invertedCameraRotation = new AxisAngle4f();
        if (isBillboard.getValue()) {
            final Matrix4f viewMatrix = new Matrix4f();
            cameraControl.getViewMatrix(viewMatrix);
            viewMatrix.getRotation(invertedCameraRotation);
            invertedCameraRotation.angle = -invertedCameraRotation.angle;
        }

        for (final Particle p : particles) {
            if (shader != null) {
                // rotate to face camera 
                if (isBillboard.getValue()) {
                    p.model.rotation.set(invertedCameraRotation);
                }
                p.model.getModelMatrix(tmpMatrix);
                shader.uniformMatrix4fv(CONST.MODEL_MATRIX, tmpMatrix);
                shader.useProgram();
            }
            mesh.drawMesh();
        }

        unbindAll();

        if (isTransparent.getValue()) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        // debug
        update(1f / 60f);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        super.addToEditor(editor);

        editor.addProperty("billboard", isBillboard);
        editor.addProperty("transparent", isTransparent);

        editor.addProperty("Force", force);
        editor.addProperty("emit time", emitTime, 0.01f);

        editor.addProperty("TTL", TTL);
        editor.addProperty("Intial dx", intialPositionDelta);
        editor.addProperty("Intial V", initalVelocity);
    }

    // dt is in seconds
    private void update(float dt) {
        final Vector3f dv = new Vector3f().set(force).mul(dt);
        for (final Particle p : particles) {
            p.velocity.add(dv);
            p.model.translation.add(p.velocity);
            p.ttl -= dt;
        }

        if (TTE < 0) {
            // generate new particle
            emitNewParticle();
            TTE = emitTime.getValue();
        }
        TTE -= dt;

        // prune particles
        particles.removeIf(p -> p.ttl <= 0);
    }

    private void emitNewParticle() {
        final Particle p = new Particle(TTL.getValue());
        // get initial position and scale from parent transformation
        p.model.set(lastTransform);

        p.model.translation.add(intialPositionDelta);
        p.velocity.set(initalVelocity);

        particles.add(p);
    }

    private void sortByDistToCamera(Matrix4f worldTransform) {
        final Matrix4f toLocalSpace = new Matrix4f();
        worldTransform.invertAffine(toLocalSpace);

        final Vector3f cameraPos = cameraControl.getCameraPosition();
        Func.multMat(cameraPos, toLocalSpace);

        final Vector3f cameraTarget = cameraControl.getCameraTarget();
        Func.multMat(cameraTarget, toLocalSpace);

        final Vector3f cameraDirection = new Vector3f();
        cameraTarget.sub(cameraPos, cameraDirection);

        // distance function should be a projection onto the view direction
        final Vector3f d1 = new Vector3f();
        final Vector3f d2 = new Vector3f();

        particles.sort((p1, p2) -> {
            p1.model.translation.sub(cameraPos, d1);
            p2.model.translation.sub(cameraPos, d2);
            final float dist1 = d1.dot(cameraDirection);
            final float dist2 = d2.dot(cameraDirection);
            return dist1 - dist2 > 0 ? -1 : (dist1 == dist2 ? 0 : 1);
        });

    }

    private class Particle {
        final SpacialThing model = new SpacialThing();
        final Vector3f velocity = new Vector3f();
        float ttl;

        public Particle(float ttl) {
            this.ttl = ttl;
        }
    }
}