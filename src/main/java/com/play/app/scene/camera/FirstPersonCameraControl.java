
package com.play.app.scene.camera;

import static org.lwjgl.glfw.GLFW.*;

import java.util.*;

import com.play.app.geometry.Ray;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.UnitMesh;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.*;

import org.joml.*;

import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Accessors(chain = true)
@Log4j2
public class FirstPersonCameraControl implements CameraControl {

    private final WindowManager windowManager;
    private Camera camera;

    private double mouseX, mouseY;
    private boolean mouseCaptured = false;

    // marker 
    private final Matrix4f markerModel = new Matrix4f();
    private final Vector4f markerColor = new Vector4f(1, 1, 1, 1);
    private final Ray markerRay = new Ray(new Vector3f(), new Vector3f(1));
    private final SimpleSceneObject marker = new SimpleSceneObject();

    public FirstPersonCameraControl(WindowManager windowManager) {
        this.windowManager = windowManager;

        marker.shape.setMesh(new UnitMesh(Type.Cyclinder, 3));
        marker.property.setShader(ShaderUtils.getShader("Simple3D"));
        marker.property.material.color.set(markerColor);
    }

    ///////////////////
    // callbacks
    ///////////////////

    @Override
    public void show() {
        // marker
        drawMarker();

        // hack, since show is call at 60 fps
        if (activeKeys.contains(GLFW_KEY_A)) {
            move(0, -1);
        }
        if (activeKeys.contains(GLFW_KEY_W)) {
            move(1, 0);
        }
        if (activeKeys.contains(GLFW_KEY_S)) {
            move(-1, 0);
        }
        if (activeKeys.contains(GLFW_KEY_D)) {
            move(0, 1);
        }
    }

    private void drawMarker() {

        // final float cameraDistance = camera.position.distance(camera.target) / 20;
        // markerModel.identity();
        // markerModel.scaleLocal(cameraDistance);
        // markerModel.translateLocal(camera.target);

        // markerShader.uniform4f(CONST.MATERIAL_COLOR, markerColor);
        // markerShader.uniformMatrix4fv(CONST.MODEL_MATRIX, markerModel);
        // markerShader.useProgram();
        // UnitGeometries.drawCyclinder(3);
        // markerShader.unuseProgram();

        marker.draw(markerModel);
    }

    private void move(float forward, float sideways) {
        if (camera == null) {
            return;
        }
        final Vector3f forwardDir = new Vector3f();
        camera.target.sub(camera.position, forwardDir).normalize();
        final Vector3f sidewaysDir = new Vector3f();
        forwardDir.cross(camera.upVector, sidewaysDir).normalize();
        // sacle it
        forwardDir.mul(forward / 20);
        sidewaysDir.mul(sideways / 20);

        // update
        forwardDir.add(sidewaysDir);
        camera.position.add(forwardDir);
        camera.target.add(forwardDir);
        updated();
    }

    private void updated() {
        if (camera == null) {
            return;
        }
        camera.updated();
        // udpate ray
        markerRay.start.set(camera.position);
        camera.target.sub(camera.position, markerRay.direction).normalize();
        markerRay.start.add(markerRay.direction);
        markerRay.start.sub(0, 0.5f, 0);
        markerRay.getTransform(markerModel, 10, 0.05f);
    }

    private void rotate(double dx, double dy) {
        // snapping
        // final double speed = dx * dx + dy * dy; // or 3
        // final double absDx = Math.abs(dx);
        // final double absDy = Math.abs(dy);
        // final double magicValue = Math.abs((absDx - absDy) / (absDx + absDy));
        // final double diff = absDx - absDy;
        // if (!(dx == 0 || dy == 0 || diff == 0) && (magicValue > 0.45 || speed < 10)) {
        //     if (absDx > absDy) {
        //         dy = 0;
        //     } else {
        //         dx = 0;
        //     }
        // }

        final Quaternionf spin = new Quaternionf().rotateAxis((float) -dx / 1200f, camera.upVector);
        final Vector3f yawAxis = new Vector3f();
        final Vector3f translatedCam = new Vector3f();
        camera.target.sub(camera.position, translatedCam);
        camera.upVector.cross(translatedCam, yawAxis);
        final Quaternionf yaw = new Quaternionf().rotateAxis((float) dy / 900f, yawAxis);

        translatedCam.rotate(spin).rotate(yaw);
        translatedCam.add(camera.position, camera.target);
        updated();
    }

    private void setMouseCapture(boolean lock) {
        if (lock) {
            mouseX = windowManager.lastMousePos[0];
            mouseY = windowManager.lastMousePos[1];
            windowManager.captureCursor();
        } else {
            windowManager.releaseCursor();
        }
        mouseCaptured = lock;
    }

    @Override
    public void setCamera(Camera camera) {
        // TODO reset camera
        if (this.camera != null) {
            setMouseCapture(false);
        }
        this.camera = camera;
        if (this.camera != null) {
            setMouseCapture(mouseCaptured);
            updated();
        }

        mouseX = windowManager.lastMousePos[0];
        mouseY = windowManager.lastMousePos[1];
    }

    @Override
    public boolean onMouseButton(int button, int action, int mode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCursorPos(double xpos, double ypos) {
        if (camera == null || !mouseCaptured) {
            return false;
        }
        rotate(xpos - mouseX, ypos - mouseY);
        mouseX = xpos;
        mouseY = ypos;
        return true;
    }

    @Override
    public boolean onCursorEnter(boolean entered) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(double xoffset, double yoffset) {
        // TODO Auto-generated method stub
        return false;
    }

    private Set<Integer> activeKeys = new HashSet<>();

    @Override
    public boolean onKey(int key, int code, int action, int mods) {

        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            if (key == GLFW_KEY_A || key == GLFW_KEY_W || key == GLFW_KEY_S || key == GLFW_KEY_D) {
                activeKeys.add(key);
                return true;
            }
        } else {
            activeKeys.remove(key);
        }
        return false;
    }

    @Override
    public boolean onChar(int character) {
        if (camera == null) {
            return false;
        }
        if (character == 'q') {
            setMouseCapture(!mouseCaptured);
            return true;
        }
        return activeKeys.size() != 0;
    }
}
