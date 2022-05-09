package com.play.app.scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;
import java.nio.FloatBuffer;

import com.play.app.geometry.Ray;
import com.play.app.graphics.*;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

public class CameraControl {

    private final FloatBuffer viewBuffer;
    private final FloatBuffer projectionBuffer;
    private final Matrix4f view;
    private final Matrix4f projection;

    private final Vector3f cameraPosition = new Vector3f(0, 0, 5);
    private final Vector3f cameraTarget = new Vector3f(0, 0, 0);
    private final Vector3f cameraUp = new Vector3f(0, 1, 0);
    private float fov = 45;

    private MouseButton activeMouseButton = MouseButton.NONE;
    private double mouseX, mouseY;

    // marker related
    private float markerScale = 15;
    private float drawMarkerFrame = 0;
    private static final ShaderProgram debugShader = createLineShader();
    private static final FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
    private static final Vector4f[] ringColor = {
            Func.toVec4(Color.RED),
            Func.toVec4(Color.GREEN),
            Func.toVec4(Color.CYAN)
    };
    private final Matrix4f[] ringModelMatrix = { new Matrix4f(), new Matrix4f(), new Matrix4f() };
    private static final Matrix4f[] RING_MODEL_MATRIX_BASE = {
            new Matrix4f(),
            new Matrix4f().setRotationXYZ(0, (float) Math.PI / 2, 0),
            new Matrix4f().setRotationXYZ((float) Math.PI / 2, 0, 0)
    };

    private final WindowManager windowManager;

    private enum MouseButton {
        LEFT, RIGHT, MIDDLE, NONE;
    };

    public CameraControl(WindowManager windowManager) {
        this.windowManager = windowManager;
        viewBuffer = BufferUtils.createFloatBuffer(16);
        projectionBuffer = BufferUtils.createFloatBuffer(16);

        view = new Matrix4f();
        projection = new Matrix4f();

        windowManager.addMouseButtonCallback(Layer.SCENE, this::mouseButtonCallback);
        windowManager.addScrollCallback(Layer.SCENE, (GLFWScrollCallbackI) this::scrollCallback);
        windowManager.addCursorEnterCallback(Layer.SCENE, this::cursorEnterCallback);
        windowManager.addCursorPosCallback(Layer.SCENE, (GLFWCursorPosCallbackI) this::cursorPosCallback);
        windowManager.addWindowSizeCallback(Layer.SCENE, this::windowSizeCallback);
        windowManager.addKeyCallback(Layer.SCENE, this::keyCallback);

        updateProjection();
        updateView();
    }

    public void setViewAndProjection(ShaderProgram program) {
        view.get(viewBuffer);
        projection.get(projectionBuffer);
        program.uniformMatrix4fv(CONST.VIEW_MATRIX, viewBuffer);
        program.uniformMatrix4fv(CONST.PROJECTION_MATRIX, projectionBuffer);
    }

    public void draw() {
        // only draw marker when moving or rotating
        if (activeMouseButton == MouseButton.NONE && drawMarkerFrame <= 0) {
            return;
        }
        drawMarkerFrame--;
        setViewAndProjection(debugShader);
        for (int i = 0; i < ringModelMatrix.length; i++) {
            ringModelMatrix[i].get(modelBuffer);
            debugShader.uniformMatrix4fv(CONST.MODEL_MATRIX, modelBuffer);
            debugShader.uniform4f("color", ringColor[i]);
            debugShader.useProgram();
            UnitGeometries.drawCircle(40);
            debugShader.unuseProgram();
        }

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

        return new Ray(new Vector3f(cameraPosition),
                new Vector3f(dir.x, dir.y, dir.z).normalize());
    }

    public Vector3f getCameraPosition() {
        return new Vector3f().set(cameraPosition);
    }

    public Vector3f getCameraTarget() {
        return new Vector3f().set(cameraTarget);
    }

    public void getViewMatrix(final Matrix4f out) {
        out.set(view);
    }

    public void getProjectionMatrix(final Matrix4f out) {
        out.set(projection);
    }

    ///////////////////
    // camera actions
    ///////////////////
    private void moveCamera(double dx, double dy) {
        final float dxFraction = (float) dx / windowManager.windowSize[0];
        final float dyFraction = (float) dy / windowManager.windowSize[1];

        final Vector3f cameraDir = new Vector3f();
        cameraTarget.sub(cameraPosition, cameraDir);
        final float cameraDistance = cameraDir.length();

        final Vector3f xDir = new Vector3f();
        final Vector3f yDir = new Vector3f();

        cameraDir.cross(cameraUp, xDir);
        xDir.cross(cameraDir, yDir);

        xDir.normalize().mul(dxFraction * cameraDistance);
        yDir.normalize().mul(dyFraction * cameraDistance);

        cameraPosition.sub(xDir).add(yDir);
        cameraTarget.sub(xDir).add(yDir);

        updateView();
    }

    private void rotateCamera(double dx, double dy) {
        // snapping
        final double speed = dx * dx + dy * dy; // or 3
        final double absDx = Math.abs(dx);
        final double absDy = Math.abs(dy);
        final double magicValue = Math.abs((absDx - absDy) / (absDx + absDy));
        final double diff = absDx - absDy;
        if (!(dx == 0 || dy == 0 || diff == 0) && (magicValue > 0.45 || speed < 10)) {
            if (absDx > absDy) {
                dy = 0;
            } else {
                dx = 0;
            }
        }

        final Quaternionf spin = new Quaternionf().rotateAxis((float) -dx / 300f, cameraUp);
        final Vector3f yawAxis = new Vector3f();
        final Vector3f translatedCam = new Vector3f();
        cameraPosition.sub(cameraTarget, translatedCam);
        cameraUp.cross(translatedCam, yawAxis);
        final Quaternionf yaw = new Quaternionf().rotateAxis((float) -dy / 600f, yawAxis);

        translatedCam.rotate(spin).rotate(yaw);
        translatedCam.add(cameraTarget, cameraPosition);

        updateView();
    }

    private void zoomCamera(double xoffset, double yoffset) {
        // zoom with fov
        // fov -= (float) yoffset * 2;
        // if (fov < 1f) {
        // fov = 1.0f;
        // } else if (fov > 45f) {
        // fov = 45.0f;
        // }
        // updateProjection();

        final Vector3f cameraDir = new Vector3f();
        cameraTarget.sub(cameraPosition, cameraDir);

        final float cameraDistance = cameraDir.length();
        final float zoomScaler = cameraDistance * 0.1f;

        cameraDir.normalize().mul((float) yoffset * zoomScaler);

        if (yoffset > 0 && cameraDistance - cameraDir.length() < 0.3f) {
            // trying to zoom too close
        } else {
            cameraPosition.add(cameraDir);
            updateView();
        }
    }

    ///////////////////
    // helpers
    ///////////////////
    private static ShaderProgram createLineShader() {
        final ShaderProgram shaderProgram = new ShaderProgram()
                .withShader("resources/shaders/Line.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Line.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        final Matrix4f identityMatrix = new Matrix4f();
        final FloatBuffer identityMatrixBuffer = BufferUtils.createFloatBuffer(16);
        identityMatrix.get(identityMatrixBuffer);
        shaderProgram.uniformMatrix4fv(CONST.MODEL_MATRIX, identityMatrixBuffer);
        return shaderProgram;
    }

    private void updateMarker() {
        final float cameraDistance = cameraPosition.distance(cameraTarget) / markerScale;
        for (int i = 0; i < RING_MODEL_MATRIX_BASE.length; i++) {
            RING_MODEL_MATRIX_BASE[i].scaleLocal(cameraDistance, ringModelMatrix[i]);
            ringModelMatrix[i].translateLocal(cameraTarget);
        }
    }

    private void updateView() {
        view.setLookAt(cameraPosition, cameraTarget, cameraUp);
        updateMarker();
    }

    private void updateProjection() {
        projection.setPerspective(Math.toRadians(fov),
                windowManager.windowSize[0] / (float) windowManager.windowSize[1],
                0.1f, 100f);
    }

    ///////////////////
    // callbacks
    ///////////////////
    private void mouseButtonCallback(long window, int button, int action, int mode) {
        if (action == 1) {
            activeMouseButton = MouseButton.values()[button];
            cursorPosCallback(window, mouseX, mouseY);
        } else {
            activeMouseButton = MouseButton.NONE;
        }
    }

    private void cursorPosCallback(long window, double xpos, double ypos) {
        final double dx = xpos - mouseX;
        final double dy = ypos - mouseY;
        mouseX = xpos;
        mouseY = ypos;
        switch (activeMouseButton) {
            case LEFT:
                markerScale = 30;
                moveCamera(dx, dy);
                break;
            case RIGHT:
                markerScale = 15;
                rotateCamera(dx, dy);
                break;
            case MIDDLE:
                markerScale = 15;
                rotateCamera(dx, dy);
                break;
            default:
        }
    }

    private void cursorEnterCallback(long window, boolean entered) {

    }

    private void scrollCallback(long window, double xoffset, double yoffset) {
        if (activeMouseButton == MouseButton.MIDDLE) {
            // rotating, skip zoom
            return;
        }
        drawMarkerFrame = 45;
        markerScale = 30;
        zoomCamera(xoffset, yoffset);
    }

    private void windowSizeCallback(long window, int w, int h) {
        glViewport(0, 0, w, h);
        updateProjection();
    }

    private void keyCallback(long window, int key, int code, int action, int mods) {
        if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
            windowManager.stopPropagation();
            // reset camera
            cameraPosition.set(0, 0, 5);
            cameraTarget.set(0, 0, 0);
            updateView();
        }
    }
}