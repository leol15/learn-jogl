
package com.play.app;

import static org.lwjgl.glfw.GLFW.*;

import com.play.app.scene.SceneNode;
import com.play.app.scene.camera.*;
import com.play.app.utils.*;

public class UseCameraControl {

    public UseCameraControl(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneManager sceneManager = new SceneManager(windowManager, new SceneNode());
        final CameraManager cameraManager = sceneManager.getCameraManager();

        cameraManager.setControll(cameraManager.FIRST_PRESON_CAMERA_CONTROL);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            glfwPollEvents();
        }

    }
}
