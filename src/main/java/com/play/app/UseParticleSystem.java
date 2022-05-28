package com.play.app;

import static org.lwjgl.glfw.GLFW.*;

import com.play.app.geometry.Sphere;
import com.play.app.graphics.*;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.lights.SpotLight;
import com.play.app.scene.sceneobject.*;
import com.play.app.utils.*;

import org.joml.Random;

public class UseParticleSystem {
    public UseParticleSystem(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSN = new SceneNode();

        final ParticlesSceneObject pSO = new ParticlesSceneObject(cameraControl);
        rootSN.createChild().setSceneObject(pSO);
        pSO.shape.setMesh(Mesh.CUBE);
        pSO.property.setShader(ShaderUtils.getShader("BlinnPhong"));
        pSO.force.set(0, -0.3, 0);
        pSO.TTL.setValue(5);
        pSO.emitTime.setValue(0.001f);
        pSO.property.material.color.set(0, 0.3, 0.6, 1);

        final Texture treeTex = new Texture(CONST.TEXTURE_FOLDER + "dash.png");
        pSO.property.setTexture(treeTex);

        final SimpleSceneObject sphereSO = new SimpleSceneObject();
        sphereSO.property.setShader(ShaderUtils.getShader("Simple3D"));
        sphereSO.shape.setMesh(Mesh.createSphereMesh(20)).setCollidable(new Sphere());
        sphereSO.property.material.color.set(1, 0, 0, 0.5);
        final SceneNode sphereNode = rootSN.createChild().setSceneObject(sphereSO);
        sphereNode.modelInfo.translation.set(1, 1, 1);

        final SimpleSceneObject sphereSO2 = new SimpleSceneObject();
        sphereSO2.property.setShader(ShaderUtils.getShader("Simple3D"));
        sphereSO2.shape.setMesh(Mesh.createSphereMesh(20)).setCollidable(new Sphere());
        sphereSO2.property.material.color.set(0, 1, 0, 0.5);
        final SceneNode sphereNode2 = rootSN.createChild().setSceneObject(sphereSO2);
        sphereNode2.modelInfo.translation.set(3, 1, 1);

        final Random rand = new Random(100);

        final Text particleCount = new Text(windowManager);
        particleCount.setColor(1, 1, 1, 1);
        particleCount.setText("Particle Count: ", 400, 10);

        // lights
        final SpotLight spotLight = new SpotLight();
        spotLight.color.set(1, 1, 0);
        final SceneNode lightSN = rootSN.createChild().setSceneObject(new LightSceneObject(spotLight));
        lightSN.modelInfo.translation.set(0, 2, 0);

        final SceneManager sceneManager = new SceneManager(windowManager, rootSN, cameraControl);
        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            // debug
            pSO.initalVelocity.set(
                    (rand.nextFloat() - 0.5f) / 20,
                    (rand.nextFloat() - 0.5f) / 20,
                    (rand.nextFloat() - 0.5f) / 20);
            pSO.intialPositionDelta.set(
                    (rand.nextFloat() - 0.5f),
                    (rand.nextFloat() - 0.5f),
                    (rand.nextFloat() - 0.5f));
            particleCount.draw();
            particleCount.setText("Particle Count: " + pSO.getParticleCount());

            // debug
            // lightSN.draw(new Matrix4f());
            glfwPollEvents();
        }

    }
}
