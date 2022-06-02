package com.play.app.scene.camera;

import com.play.app.utils.SceneManager;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import lombok.Getter;

/**
 * sets the camera control style, ie, first person view, editor view
 */
public class CameraManager {
    // cam controller instances, default to this
    public final EditorCameraControl EDITOR_CAMERA_CONTROL;
    public final FirstPersonCameraControl FIRST_PRESON_CAMERA_CONTROL;

    @Getter
    private CameraControl activeController;
    @Getter
    private final Camera camera;
    private final WindowManager windowManager;

    public CameraManager(WindowManager windowManager, SceneManager sceneManager) {
        this.windowManager = windowManager;
        EDITOR_CAMERA_CONTROL = new EditorCameraControl(windowManager, sceneManager);
        FIRST_PRESON_CAMERA_CONTROL = new FirstPersonCameraControl(windowManager);

        camera = new Camera(windowManager);

        // call backs
        windowManager.addMouseButtonCallback(Layer.SCENE, this::mouseButtonCallback);
        windowManager.addScrollCallback(Layer.SCENE, (GLFWScrollCallbackI) this::scrollCallback);
        windowManager.addCursorEnterCallback(Layer.SCENE, this::cursorEnterCallback);
        windowManager.addCursorPosCallback(Layer.SCENE, (GLFWCursorPosCallbackI) this::cursorPosCallback);
        windowManager.addKeyCallback(Layer.SCENE, this::keyCallback);
        windowManager.addCharCallback(Layer.SCENE, this::charCallback);

        // default to this
        setControll(EDITOR_CAMERA_CONTROL);
    }

    @Deprecated
    public CameraManager(WindowManager windowManager) {
        this(windowManager, null);
    }

    public void setControll(CameraControl controller) {
        if (activeController != null) {
            activeController.setCamera(null);
        }
        activeController = controller;
        activeController.setCamera(camera);
    }

    public void show() {
        if (activeController != null) {
            activeController.show();
        }
    }

    private void mouseButtonCallback(long window, int button, int action, int mode) {
        if (activeController == null) {
            return;
        }
        if (activeController.onMouseButton(button, action, mode)) {
            windowManager.stopPropagation();
        }
    }

    private void cursorPosCallback(long window, double xpos, double ypos) {
        if (activeController == null) {
            return;
        }
        if (activeController.onCursorPos(xpos, ypos)) {
            windowManager.stopPropagation();
        }
    }

    private void cursorEnterCallback(long window, boolean entered) {
        if (activeController == null) {
            return;
        }
        if (activeController.onCursorEnter(entered)) {
            windowManager.stopPropagation();
        }
    }

    private void scrollCallback(long window, double xoffset, double yoffset) {
        if (activeController == null) {
            return;
        }
        if (activeController.onScroll(xoffset, yoffset)) {
            windowManager.stopPropagation();
        }
    }

    private void keyCallback(long window, int key, int code, int action, int mods) {
        if (activeController == null) {
            return;
        }
        if (activeController.onKey(key, code, action, mods)) {
            windowManager.stopPropagation();
        }
    }

    private void charCallback(long window, int character) {
        if (activeController == null) {
            return;
        }
        if (activeController.onChar(character)) {
            windowManager.stopPropagation();
        }
    }
}
