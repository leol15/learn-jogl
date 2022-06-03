package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.graphics.Texture;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.scene.SceneNode;
import com.play.app.scene.lights.SpotLight;
import com.play.app.scene.sceneobject.LightSceneObject;
import com.play.app.scene.sceneobject.ParticlesSceneObject;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.CONST;
import com.play.app.utils.SceneManager;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

import org.joml.Random;

public class UseParticleSystem {
    public UseParticleSystem(long window) {

        final WindowManager windowManager = new WindowManager(window);
        // final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSN = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, rootSN);

        final ParticlesSceneObject pSO = new ParticlesSceneObject(sceneManager.getCameraManager().getCamera());

        rootSN.createChild().setSceneObject(pSO);
        pSO.shape.setUnitGeometry(Type.Cube);
        pSO.property.setShader(ShaderUtils.getShader("BlinnPhong"));
        pSO.force.set(0, -0.3, 0);
        pSO.TTL.setValue(5);
        pSO.emitTime.setValue(0.001f);
        pSO.property.material.color.set(0, 0.3, 0.6, 1);

        final Texture treeTex = new Texture(CONST.TEXTURE_FOLDER + "dash.png");
        pSO.property.setTexture(treeTex);

        final SimpleSceneObject sphereSO = new SimpleSceneObject();
        sphereSO.property.setShader(ShaderUtils.getShader("Simple3D"));
        sphereSO.shape.setUnitGeometry(Type.Sphere);
        sphereSO.property.material.color.set(1, 0, 0, 0.5);
        final SceneNode sphereNode = rootSN.createChild().setSceneObject(sphereSO);
        sphereNode.modelInfo.translation.set(1, 1, 1);

        final SimpleSceneObject sphereSO2 = new SimpleSceneObject();
        sphereSO2.property.setShader(ShaderUtils.getShader("Simple3D"));
        sphereSO2.shape.setUnitGeometry(Type.Sphere);
        sphereSO2.property.material.color.set(0, 1, 0, 0.5);
        final SceneNode sphereNode2 = rootSN.createChild().setSceneObject(sphereSO2);
        sphereNode2.modelInfo.translation.set(3, 1, 1);

        final Random rand = new Random(100);

        // final Text particleCount = new Text(windowManager);
        // particleCount.setColor(1, 1, 1, 1);
        // particleCount.setText("Particle Count: ", 400, 10);

        // lights
        final SpotLight spotLight = new SpotLight();
        spotLight.color.set(1, 1, 0);
        final SceneNode lightSN = rootSN.createChild().setSceneObject(new LightSceneObject(spotLight));
        lightSN.modelInfo.translation.set(0, 2, 0);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            // debug
            pSO.initalVelocity.set(
                    (rand.nextFloat() - 0.5f) / 20,
                    (rand.nextFloat() - 0.5f) / 20,
                    (rand.nextFloat() - 0.5f) / 20);
            // particleCount.draw();
            // particleCount.setText("Particle Count: " + pSO.getParticleCount());

            // debug
            // lightSN.draw(new Matrix4f());
            glfwPollEvents();
        }

    }
}
