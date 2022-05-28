package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.GL_MAX_UNIFORM_LOCATIONS;

import com.play.app.geometry.*;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.lights.*;
import com.play.app.scene.sceneobject.*;
import com.play.app.utils.*;

import org.joml.Math;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UseLights {

    public UseLights(long window) {

        int max_uniform = glGetInteger(GL_MAX_UNIFORM_LOCATIONS);
        log.debug("max num uniforms {}", max_uniform);

        glPolygonMode(GL_FRONT, GL_FILL);

        // init UBOs before shaders
        LightUBO.getInstance();
        CameraUBO.getInstance();

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSceneNode = new SceneNode();
        // test
        final SceneManager sceneManager = new SceneManager(windowManager, rootSceneNode, cameraControl);
        // shaders needs to be with cameraControl
        final ShaderProgram simple3DShader = ShaderUtils.getShader("simple3D");
        final ShaderProgram blinnPhong = ShaderUtils.getShader("BlinnPhong");

        // construct scene

        final SimpleSceneObject cubeObject = new SimpleSceneObject();
        cubeObject.shape.setCollidable(new Cube())
                .setMesh(Mesh.CUBE);
        cubeObject.property.setShader(blinnPhong);
        final SceneNode cubeSN = rootSceneNode.createChild().setSceneObject(cubeObject);
        cubeSN.modelInfo.rotation.setAngleAxis(Math.toRadians(30f), 1, 1, 1);
        cubeSN.modelInfo.translation.set(0, -2, 0);
        cubeSN.modelInfo.scale.set(2, 2, 2);

        final SimpleSceneObject bottomPlane = new SimpleSceneObject();
        bottomPlane.shape.setCollidable(new Plane())
                .setMesh(Mesh.PLANE);
        bottomPlane.property.setShader(blinnPhong);
        final SceneNode bottomPlaneSN = rootSceneNode.createChild().setSceneObject(bottomPlane);
        bottomPlaneSN.modelInfo.rotation.setAngleAxis(Math.toRadians(-90), 1, 0, 0);
        bottomPlaneSN.modelInfo.scale.set(15, 15, 1);

        final SimpleSceneObject sphere = new SimpleSceneObject();
        sphere.shape.setCollidable(new Sphere())
                .setMesh(Mesh.createSphereMesh(20));
        sphere.property.setShader(blinnPhong);
        final SceneNode sphereSN = rootSceneNode.createChild().setSceneObject(sphere);
        sphereSN.modelInfo.translation.set(5, 3, 5);

        final PointLight pointL = new PointLight();
        pointL.color.set(0, 1, 0, 1);
        final LightSceneObject lightSO1 = new LightSceneObject(pointL);
        lightSO1.property.setShader(simple3DShader);
        final SceneNode lightNode = rootSceneNode.createChild().setSceneObject(lightSO1);
        lightNode.modelInfo.translation.set(2, 2, -2);
        lightNode.modelInfo.scale.set(0.5, 0.5, 0.5);

        // directional light
        final DirectionalLight dirL = new DirectionalLight();
        dirL.color.set(1, 0, 0, 1);
        final LightSceneObject lightSO2 = new LightSceneObject(dirL);
        lightSO2.property.setShader(simple3DShader);
        final SceneNode dirLightNode = rootSceneNode.createChild().setSceneObject(lightSO2);
        dirLightNode.modelInfo.translation.set(2, 5, -2);
        dirLightNode.modelInfo.scale.set(0.5, 1, 0.5);
        dirLightNode.modelInfo.rotation.setAngleAxis(Math.toRadians(180f), 1, 0, 0);

        // spot light
        final SpotLight spotL = new SpotLight();
        spotL.color.set(0, 0, 1, 1);
        final LightSceneObject lightSO3 = new LightSceneObject(spotL);
        lightSO3.property.setShader(simple3DShader);
        final SceneNode spotLigtNode = rootSceneNode.createChild().setSceneObject(lightSO3);
        spotLigtNode.modelInfo.translation.set(3, 3, -3);
        spotLigtNode.modelInfo.scale.set(0.5, 0.5, 0.5);

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            glfwPollEvents();
        }

    }
}
