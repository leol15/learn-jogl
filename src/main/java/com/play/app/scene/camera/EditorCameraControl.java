package com.play.app.scene.camera;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.nio.FloatBuffer;

import com.play.app.geometry.Ray;
import com.play.app.graphics.*;
import com.play.app.graphics.UnitGeometries.VAOHelper;
import com.play.app.utils.*;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.BufferUtils;

import lombok.*;

public class EditorCameraControl implements CameraControl {

    private static final Vector3f DEFAULT_CAM_POSITION = new Vector3f(3, 4, 5);
    private static final Vector3f DEFAULT_CAM_TARGET = new Vector3f();

    private Camera camera;
    private int activeMouseButton = -1;
    private double mouseX, mouseY;

    // grid related
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
    private final SceneManager sceneManager;

    // private enum MouseButton {
    //     LEFT, RIGHT, MIDDLE, NONE;
    // };

    public EditorCameraControl(WindowManager windowManager, SceneManager sceneManager) {
        this.windowManager = windowManager;
        this.sceneManager = sceneManager;

        gridVAO = createBaseGrid();
        lineShader = createLineShader();
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
        if (camera != null && camera.position.distance(camera.target) < 1) {
            resetCameraPosition();
        }
        updated();
    }

    @Override
    public boolean onMouseButton(int button, int action, int mode) {
        if (camera == null) {
            return false;
        }
        if (action == 1) {
            activeMouseButton = button;
            return onCursorPos(mouseX, mouseY);
        } else {
            activeMouseButton = -1;
        }
        return false;
    }

    @Override
    public boolean onCursorPos(double xpos, double ypos) {
        if (camera == null) {
            return false;
        }
        final double dx = xpos - mouseX;
        final double dy = ypos - mouseY;
        mouseX = xpos;
        mouseY = ypos;
        switch (activeMouseButton) {
            case 0:
                markerScale = 30;
                moveCamera(dx, dy);
                return true;
            case 1:
                markerScale = 15;
                rotateCamera(dx, dy);
                return true;
            case 2:
                markerScale = 15;
                rotateCamera(dx, dy);
                return true;
            default:
        }
        return false;
    }

    @Override
    public boolean onCursorEnter(boolean entered) {
        if (camera == null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean onScroll(double xoffset, double yoffset) {
        if (camera == null) {
            return false;
        }
        if (activeMouseButton == 2) {
            // rotating, skip zoom
            return false;
        }
        drawMarkerFrame = 45;
        markerScale = 30;
        zoomCamera(xoffset, yoffset);
        return true;
    }

    @Override
    public boolean onKey(int key, int code, int action, int mods) {
        if (camera == null) {
            return false;
        }
        if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
            windowManager.stopPropagation();
            // reset camera
            resetCameraPosition();
            return true;
        }
        return false;
    }

    @Override
    public boolean onChar(int character) {
        if (camera == null) {
            return false;
        }
        if (character == 'a') {
            windowManager.stopPropagation();
            final Ray ray = camera.getRay(windowManager.lastMousePos[0],
                    windowManager.lastMousePos[1]);
            sceneManager.selectSceneNode(ray);
            return true;
        } else if (character == 'f') {
            // focus
            return sceneManager.focusSelect();
        }
        return false;
    }

    @Override
    public void show() {
        // grid
        if (drawGrid) {
            lineShader.uniformMatrix4fv(CONST.MODEL_MATRIX, gridModelMat);
            lineShader.uniform4f(CONST.MATERIAL_COLOR, new Vector4f(0, 0.6f, 0.6f, 1));
            lineShader.useProgram();
            gridVAO.draw();
            lineShader.unuseProgram();
        }

        // only draw marker when moving or rotating
        if (activeMouseButton == -1 && drawMarkerFrame <= 0) {
            return;
        }
        drawMarkerFrame--;
        drawMarker();

        // also draw a tiny marker
        final float oldMarkerScale = markerScale;
        markerScale = 200;
        updateMarker();

        drawMarker();

        markerScale = oldMarkerScale;
        updateMarker();

    }

    public void focusOn(Vector3f target) {
        if (camera == null) {
            return;
        }
        camera.position.sub(camera.target);
        camera.target.set(target);
        camera.position.add(camera.target);
        camera.updated();
    }

    ///////////////////
    // camera actions
    ///////////////////
    private void moveCamera(double dx, double dy) {
        final float dxFraction = (float) dx / windowManager.windowSize[0];
        final float dyFraction = (float) dy / windowManager.windowSize[1];

        final Vector3f cameraDir = new Vector3f();
        camera.target.sub(camera.position, cameraDir);
        final float cameraDistance = cameraDir.length();

        final Vector3f xDir = new Vector3f();
        final Vector3f yDir = new Vector3f();

        cameraDir.cross(camera.upVector, xDir);
        xDir.cross(cameraDir, yDir);

        xDir.normalize().mul(dxFraction * cameraDistance);
        yDir.normalize().mul(dyFraction * cameraDistance);

        camera.position.sub(xDir).add(yDir);
        camera.target.sub(xDir).add(yDir);

        updated();
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

        final Quaternionf spin = new Quaternionf().rotateAxis((float) -dx / 300f, camera.upVector);
        final Vector3f yawAxis = new Vector3f();
        final Vector3f translatedCam = new Vector3f();
        camera.position.sub(camera.target, translatedCam);
        camera.upVector.cross(translatedCam, yawAxis);
        final Quaternionf yaw = new Quaternionf().rotateAxis((float) -dy / 600f, yawAxis);

        translatedCam.rotate(spin).rotate(yaw);
        translatedCam.add(camera.target, camera.position);

        updated();
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
        camera.target.sub(camera.position, cameraDir);

        final float cameraDistance = cameraDir.length();
        final float zoomScaler = cameraDistance * 0.1f;

        cameraDir.normalize().mul((float) yoffset * zoomScaler);

        if (yoffset > 0 && cameraDistance - cameraDir.length() < 0.3f) {
            // trying to zoom too close
        } else {
            camera.position.add(cameraDir);
            updated();
        }
    }

    private void updated() {
        if (camera != null) {
            camera.updated();
            updateMarker();
        }
    }

    ///////////////////
    // helpers
    ///////////////////
    private static ShaderProgram createLineShader() {
        final ShaderProgram shaderProgram = ShaderUtils.getShader("Line");

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
        final float cameraDistance = camera.position.distance(camera.target) / markerScale;
        for (int i = 0; i < RING_MODEL_MATRIX_BASE.length; i++) {
            RING_MODEL_MATRIX_BASE[i].scaleLocal(cameraDistance, ringModelMatrix[i]);
            ringModelMatrix[i].translateLocal(camera.target);
        }
    }

    private void drawMarker() {
        for (int i = 0; i < ringModelMatrix.length; i++) {
            ringModelMatrix[i].get(modelBuffer);
            lineShader.uniformMatrix4fv(CONST.MODEL_MATRIX, modelBuffer);
            lineShader.uniform4f(CONST.MATERIAL_COLOR, ringColor[i]);
            lineShader.useProgram();
            UnitGeometries.drawCircle(40);
            lineShader.unuseProgram();
        }
    }

    ///////////////////
    // callbacks
    ///////////////////
    private void resetCameraPosition() {
        if (camera != null) {
            camera.position.set(DEFAULT_CAM_POSITION);
            camera.target.set(DEFAULT_CAM_TARGET);
            updated();
        }
    }

}
