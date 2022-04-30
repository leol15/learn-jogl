package com.play.app.ui;

import java.nio.FloatBuffer;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.utils.CONST;
import com.play.app.utils.Func;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

public class CameraControl {

    private final FloatBuffer viewBuffer;
    private final FloatBuffer projectionBuffer;
    private final Matrix4f view;
    private final Matrix4f projection;

    private final Vector3f cameraPosition;
    private final Vector3f cameraTarget;
    private final Vector3f cameraUp = new Vector3f(0, 1, 0);
    private float fov;

    private MouseButton activeMouseButton = MouseButton.NONE;
    private int mouseButtonMode;
    private double mouseX, mouseY;
    private int[] windowSize;

    private static final ShaderProgram debugShader = createLineShader();

    private enum MouseButton {
        LEFT, RIGHT, MIDDLE, NONE;
    };

    public CameraControl(long window) {
        viewBuffer = BufferUtils.createFloatBuffer(16);
        projectionBuffer = BufferUtils.createFloatBuffer(16);

        view = new Matrix4f();
        projection = new Matrix4f();

        windowSize = Func.getWindowSize(window);
        cameraPosition = new Vector3f(0, 0, 5);
        cameraTarget = new Vector3f(0, 0, 0);
        fov = 45;

        updateProjection();
        updateView();

        glfwSetMouseButtonCallback(window, this::mouseButtonCallback);
        glfwSetScrollCallback(window, this::scrollCallback);

        glfwSetCursorEnterCallback(window, this::cursorEnterCallback);
        glfwSetCursorPosCallback(window, this::cursorPosCallback);

        glfwSetWindowSizeCallback(window, this::windowSizeCallback);

        glfwSetKeyCallback(window, (w, k, c, a, m) -> {
            if (k == GLFW_KEY_SPACE && a == GLFW_RELEASE) {
                // reset camera
                cameraPosition.set(0, 0, 5);
                cameraTarget.set(0, 0, 0);
                updateView();
            }
        });
    }

    public void setViewAndProjection(ShaderProgram program) {
        view.get(viewBuffer);
        projection.get(projectionBuffer);
        program.uniformMatrix4fv(CONST.VIEW_MATRIX, viewBuffer);
        program.uniformMatrix4fv(CONST.PROJECTION_MATRIX, projectionBuffer);
    }

    public void draw() {
        setViewAndProjection(debugShader);
        debugShader.useProgram();
        UnitGeometries.drawAxisSphere();
        debugShader.unuseProgram();
    }

    private void updateView() {
        view.setLookAt(cameraPosition, cameraTarget, cameraUp);
    }

    private void updateProjection() {
        projection.setPerspective(Math.toRadians(fov),
                windowSize[0] / (float) windowSize[1], 0.1f, 100f);
    }

    private void mouseButtonCallback(long window, int button, int action, int mode) {
        if (action == 1) {
            activeMouseButton = MouseButton.values()[button];
        } else {
            activeMouseButton = MouseButton.NONE;
        }
        mouseButtonMode = mode;
    }

    private void scrollCallback(long window, double xoffset, double yoffset) {
        if (activeMouseButton == MouseButton.MIDDLE) {
            // rotating, skip zoom
            return;
        }
        zoomCamera(xoffset, yoffset);
    }

    private void cursorEnterCallback(long window, boolean entered) {

    }

    private void cursorPosCallback(long window, double xpos, double ypos) {
        final double dx = xpos - mouseX;
        final double dy = ypos - mouseY;
        mouseX = xpos;
        mouseY = ypos;
        switch (activeMouseButton) {
            case LEFT:
                moveCamera(dx, dy);
                break;
            case RIGHT:
                rotateCamera(dx, dy);
                break;
            case MIDDLE:
                rotateCamera(dx, dy);
                break;
            default:
        }
    }

    private void windowSizeCallback(long window, int w, int h) {
        glViewport(0, 0, w, h);

        windowSize[0] = w;
        windowSize[1] = h;
        updateProjection();
    }

    private void moveCamera(double dx, double dy) {
        final Vector3f cameraDir = new Vector3f();
        cameraTarget.sub(cameraPosition, cameraDir);
        final float cameraDistance = cameraDir.length();

        final Vector3f xDir = new Vector3f();
        cameraDir.cross(cameraUp, xDir);
        xDir.normalize();
        final float dxFraction = (float) dx / windowSize[0];
        final float dyFraction = (float) dy / windowSize[1];
        xDir.mul(dxFraction * cameraDistance);

        final Vector3f yDir = new Vector3f(cameraUp);
        yDir.mul(dyFraction * cameraDistance);

        cameraPosition.sub(xDir).add(yDir);
        cameraTarget.sub(xDir).add(yDir);
        // Func.p("Move yDir " + yDir);
        // Func.p("Move x " + dx + " y " + dy);
        updateView();
    }

    private void rotateCamera(double dx, double dy) {
        // TODO use better scale factor
        // snapping
        final double speed = dx * dx + dy * dy; // or 3
        final double absDx = Math.abs(dx);
        final double absDy = Math.abs(dy);
        final double magicValue = Math.abs((absDx - absDy) / (absDx + absDy));
        final double diff = absDx - absDy;
        Func.p("dx " + dx + " dy " + dy + " bound " + speed + " magic " + magicValue);
        if (dx == 0 || dy == 0) {

        } else if (diff != 0 && (magicValue > 0.45 || speed < 10)) {
            Func.p("snap");
            if (absDx > absDy) {
                dy = 0;
            } else {
                dx = 0;
            }
        }

        final Quaternionf spin = new Quaternionf().rotateAxis((float) -dx / 200, cameraUp);
        final Vector3f yawAxis = new Vector3f();
        final Vector3f translatedCam = new Vector3f();
        cameraPosition.sub(cameraTarget, translatedCam);
        cameraUp.cross(translatedCam, yawAxis);
        final Quaternionf yaw = new Quaternionf().rotateAxis((float) -dy / 500, yawAxis);

        translatedCam.rotate(spin).rotate(yaw);
        translatedCam.add(cameraTarget, cameraPosition);

        // final Vector3f translatedCam = new Vector3f();
        // cameraPosition.sub(cameraTarget, translatedCam);
        // final float cameraDistance = translatedCam.length();
        // translatedCam.normalize();

        // translatedCam.rotateAxis((float) (-dx / windowSize[0] * 6),
        // cameraUp.x, cameraUp.y, cameraUp.z);
        // translatedCam.normalize();

        // final Vector3f yawAxis = new Vector3f();
        // cameraUp.cross(translatedCam, yawAxis);

        // translatedCam.rotateAxis((float) (-dy / windowSize[1] * 3),
        // yawAxis.x, yawAxis.y, yawAxis.z, cameraPosition);

        // cameraPosition.normalize().mul(cameraDistance);
        // cameraPosition.add(cameraTarget);

        updateView();
    }

    private void zoomCamera(double xoffset, double yoffset) {
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

        if (yoffset > 0 && cameraDistance - cameraDir.length() < 0.1f) {
            // trying to zoom too close
        } else {
            cameraPosition.add(cameraDir);
            updateView();
        }
    }

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
}
