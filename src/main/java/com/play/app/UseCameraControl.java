
package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.scene.SceneNode;
import com.play.app.scene.camera.CameraManager;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.SceneManager;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

public class UseCameraControl {

    public UseCameraControl(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);
        final CameraManager cameraManager = sceneManager.getCameraManager();

        final SimpleSceneObject simpleSO = new SimpleSceneObject();
        simpleSO.shape.setUnitGeometry(Type.Sphere);
        simpleSO.property.setShader(ShaderUtils.getShader("Weird"));
        root.createChild().setSceneObject(simpleSO);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);
            final float t = (float) glfwGetTime();
            simpleSO.property.getShader().uniformf("t", t);
            sceneManager.render();

            glfwPollEvents();
        }

    }
}
