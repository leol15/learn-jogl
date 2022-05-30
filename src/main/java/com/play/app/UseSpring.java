package com.play.app;

import static org.lwjgl.glfw.GLFW.*;

import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.SpringMesh;
import com.play.app.physics.TimeStepEngine;
import com.play.app.scene.SceneNode;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.*;

public class UseSpring {

    public UseSpring(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);

        final TimeStepEngine timeStepEngine = new TimeStepEngine();

        final SpringMesh mesh = new SpringMesh(timeStepEngine);
        final SimpleSceneObject simpleSO = new SimpleSceneObject();
        simpleSO.shape.setMesh(mesh);
        simpleSO.shape.setCollider(new UnitCollider(Type.Cube));
        simpleSO.property.shader = ShaderUtils.getShader("Simple3D");
        simpleSO.property.shader.uniform("debug", 1);
        root.createChild().setSceneObject(simpleSO);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);
            sceneManager.render();

            glfwPollEvents();
        }

        timeStepEngine.terminated = true;

    }
}
