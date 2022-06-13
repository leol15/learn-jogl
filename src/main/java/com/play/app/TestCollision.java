package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries;
import com.play.app.mesh.Mesh;
import com.play.app.mesh.UnitMesh;
import com.play.app.physics.Accumulator;
import com.play.app.physics.PhysicsEngine;
import com.play.app.physics.physicsentities.PhysicsEntity;
import com.play.app.scene.SceneNode;
import com.play.app.scene.lights.DirectionalLight;
import com.play.app.scene.sceneobject.LightSceneObject;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.SceneManager;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

public class TestCollision {
    public TestCollision(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);

        final PhysicsEngine physicsEngine = new PhysicsEngine();

        // experiment for very primitive setup
        // draw my self

        final SimpleSceneObject sphere = new SimpleSceneObject();
        root.createChild().setSceneObject(sphere);
        // sphere.shape.collider = new UnitCollider(UnitGeometries.Type.Sphere);
        sphere.shape.mesh = new UnitMesh(UnitGeometries.Type.Sphere);
        sphere.property.shader = ShaderUtils.getShader("Simple3D");

        physicsEngine.addGlobalActor(entity -> {
            entity.getAccumulator().force.add(0, -0.1f, 0);
        });

        // DEV
        // physicsEngine.addObject();

        sceneManager.rootNodeUpdate();

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            glfwPollEvents();
        }

        physicsEngine.setStop(true);
    }
}
