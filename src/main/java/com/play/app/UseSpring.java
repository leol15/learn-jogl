package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.SpringMesh;
import com.play.app.physics.PhysicsEngine;
import com.play.app.scene.SceneNode;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.SceneManager;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

public class UseSpring {

    public UseSpring(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);

        final PhysicsEngine physicsEngine = new PhysicsEngine();

        final SpringMesh mesh = new SpringMesh(physicsEngine);
        final SimpleSceneObject simpleSO = new SimpleSceneObject();
        simpleSO.shape.setMesh(mesh);
        simpleSO.shape.setCollider(new UnitCollider(Type.Cube));
        simpleSO.property.setShader(ShaderUtils.getShader("Simple3D"));
        simpleSO.property.getShader().uniform("debug", 1);
        root.createChild().setSceneObject(simpleSO);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);
            sceneManager.render();

            glfwPollEvents();
        }

        physicsEngine.setStop(true);

    }
}
