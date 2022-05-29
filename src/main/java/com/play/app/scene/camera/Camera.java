package com.play.app.scene.camera;

import static org.lwjgl.opengl.GL11.glViewport;

import com.play.app.geometry.Ray;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.joml.Math;

import lombok.experimental.Accessors;

/**
 * a camera, with info such as position and view direction
 */
@Accessors(chain = true)
public class Camera {

    public final Vector3f position = new Vector3f();
    public final Vector3f target = new Vector3f();
    public final Vector3f upVector = new Vector3f(0, 1, 0);
    public float fov = 45;

    private final Matrix4f view = new Matrix4f();
    private final Matrix4f projection = new Matrix4f();
    private final WindowManager windowManager;

    public Camera(WindowManager windowManager) {
        this.windowManager = windowManager;
        windowManager.addWindowSizeCallback(Layer.SCENE, this::windowSizeCallback);
        updated();
    }

    ////////////////////////
    // cast ray into space
    ////////////////////////
    public Ray getRay(final float screenX, final float screenY) {
        final int windowW = windowManager.windowSize[0];
        final int windowH = windowManager.windowSize[1];
        final float cx = screenX - windowW / 2 + 0.5f;
        final float cy = screenY - windowH / 2 + 0.5f;
        // find inverse
        final Matrix4f inverseView = new Matrix4f();
        view.invert(inverseView);

        final double focal_length = windowH / (2 * Math.tan(fov * Math.PI / 360.f));
        final Vector3f v0 = new Vector3f(cx, -cy, (float) -focal_length).normalize();
        final Vector4f dir = new Vector4f(v0, 0).mul(inverseView);

        return new Ray(new Vector3f(position),
                new Vector3f(dir.x, dir.y, dir.z).normalize());
    }

    public void getViewMatrix(final Matrix4f out) {
        out.set(view);
    }

    public void getProjectionMatrix(final Matrix4f out) {
        out.set(projection);
    }

    ///////////////////
    // helpers
    ///////////////////
    public void updated() {
        computeMatrices();
        loadToUBO();
    }

    public void computeMatrices() {
        view.setLookAt(position, target, upVector);
        projection.setPerspective(Math.toRadians(fov),
                windowManager.windowSize[0] / (float) windowManager.windowSize[1],
                0.1f, 300f);
    }

    public void loadToUBO() {
        CameraUBO.getInstance().setData(view, projection, position, new Vector3f());
    }

    ///////////////////
    // callbacks
    ///////////////////

    private void windowSizeCallback(long window, int w, int h) {
        glViewport(0, 0, w, h);
        updated();
    }

    //helper to look at p
    public void focusOn(Vector3f p) {
        position.sub(target);
        target.set(p);
        position.add(target);
        updated();
    }

}
