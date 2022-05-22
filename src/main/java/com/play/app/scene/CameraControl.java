package com.play.app.scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;
import java.nio.*;

import com.play.app.geometry.Ray;
import com.play.app.graphics.*;
import com.play.app.graphics.UnitGeometries.VAOHelper;
import com.play.app.scene.lights.LightUBO;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Accessors(chain = true)
@Log4j2
public class CameraControl {

    private static final Vector3f DEFAULT_CAM_POSITION = new Vector3f(3, 4, 5);
    private static final Vector3f DEFAULT_CAM_TARGET = new Vector3f();

    private final Matrix4f view;
    private final Matrix4f projection;

    private final Vector3f ambientColor = new Vector3f(0.2f, 0.2f, 0.2f);
    private final Vector3f cameraPosition = new Vector3f(DEFAULT_CAM_POSITION);
    private final Vector3f cameraTarget = new Vector3f(DEFAULT_CAM_TARGET);
    private final Vector3f cameraUp = new Vector3f(0, 1, 0);
    private float fov = 45;

    private MouseButton activeMouseButton = MouseButton.NONE;
    private double mouseX, mouseY;

    // grid relate
    @Setter
    private boolean drawGrid = true;
    private Matrix4f gridModelMat = new Matrix4f();
    private final VAO gridVAO;

    // marker related
    private float markerScale = 15;
    private float drawMarkerFrame = 0;
    private final ShaderProgram lineShader;
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

        view = new Matrix4f();
        projection = new Matrix4f();

        gridVAO = createBaseGrid();

        lineShader = createLineShader();

        updateProjection();
        updateView();

        windowManager.addMouseButtonCallback(Layer.SCENE, this::mouseButtonCallback);
        windowManager.addScrollCallback(Layer.SCENE, (GLFWScrollCallbackI) this::scrollCallback);
        windowManager.addCursorEnterCallback(Layer.SCENE, this::cursorEnterCallback);
        windowManager.addCursorPosCallback(Layer.SCENE, (GLFWCursorPosCallbackI) this::cursorPosCallback);
        windowManager.addWindowSizeCallback(Layer.SCENE, this::windowSizeCallback);
        windowManager.addKeyCallback(Layer.SCENE, this::keyCallback);

    }

    public void draw() {
        // grid
        if (drawGrid) {
            lineShader.uniformMatrix4fv(CONST.MODEL_MATRIX, gridModelMat);
            lineShader.uniform4f(CONST.SHADER_COLOR, new Vector4f(0, 0.6f, 0.6f, 1));
            lineShader.useProgram();
            gridVAO.draw();
            lineShader.unuseProgram();
        }

        // only draw marker when moving or rotating
        if (activeMouseButton == MouseButton.NONE && drawMarkerFrame <= 0) {
            return;
        }
        drawMarkerFrame--;
        for (int i = 0; i < ringModelMatrix.length; i++) {
            ringModelMatrix[i].get(modelBuffer);
            lineShader.uniformMatrix4fv(CONST.MODEL_MATRIX, modelBuffer);
            lineShader.uniform4f(CONST.SHADER_COLOR, ringColor[i]);
            lineShader.useProgram();
            UnitGeometries.drawCircle(40);
            lineShader.unuseProgram();
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
                .withShader(CONST.SHADER_DEFAULT_FOLDER + "Line.vert", GL_VERTEX_SHADER)
                .withShader(CONST.SHADER_DEFAULT_FOLDER + "Default.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        final Matrix4f identityMatrix = new Matrix4f();
        final FloatBuffer identityMatrixBuffer = BufferUtils.createFloatBuffer(16);
        identityMatrix.get(identityMatrixBuffer);
        shaderProgram.uniformMatrix4fv(CONST.MODEL_MATRIX, identityMatrixBuffer);
        return shaderProgram;
    }

    // create am 9x9 grid
    private static VAO createBaseGrid() {
        final int HALF_SIZE = 6;
        final VAOHelper vHelper = new VAOHelper((HALF_SIZE * 2 + 1) * 2 * 2, 0);
        for (int d = -HALF_SIZE; d < HALF_SIZE + 1; d++) {
            vHelper.positions.put(-HALF_SIZE).put(0).put(d);
            vHelper.positions.put(HALF_SIZE).put(0).put(d);

            vHelper.positions.put(d).put(0).put(-HALF_SIZE);
            vHelper.positions.put(d).put(0).put(HALF_SIZE);
        }

        vHelper.done();
        vHelper.modifyingVAO.disableVertexAttribArray(CONST.VERT_IN_NORMAL);
        vHelper.modifyingVAO.disableVertexAttribArray(CONST.VERT_IN_UV);
        vHelper.modifyingVAO.setDrawFunction(() -> glDrawArrays(GL_LINES, 0, (HALF_SIZE * 2 + 1) * 2 * 2));

        return vHelper.modifyingVAO;
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
        updateCameraUboData();
    }

    private void updateProjection() {
        projection.setPerspective(Math.toRadians(fov),
                windowManager.windowSize[0] / (float) windowManager.windowSize[1],
                0.1f, 100f);
        updateCameraUboData();
    }

    private void updateCameraUboData() {
        CameraUBO.getInstance().setData(view, projection, cameraPosition, ambientColor);
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
            cameraPosition.set(DEFAULT_CAM_POSITION);
            cameraTarget.set(DEFAULT_CAM_TARGET);
            updateView();
        }
    }
}
