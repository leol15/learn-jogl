package com.play.app;

import static org.lwjgl.glfw.GLFW.*;

import com.play.app.graphics.*;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.lights.SpotLight;
import com.play.app.scene.sceneobject.*;
import com.play.app.utils.*;

import org.joml.*;

public class UseParticleSystem {
    public UseParticleSystem(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSN = new SceneNode();

        final ParticlesSceneObject pSO = new ParticlesSceneObject(cameraControl);
        rootSN.createChild().setSceneObject(pSO);
        pSO.setMesh(Mesh.CUBE);
        pSO.setShader(ShaderUtils.getShader("BlinnPhong"));
        // pSO.setShader(ShaderUtils.getShader("Texture"));
        pSO.force.set(0, -0.3, 0);
        pSO.TTL.setValue(5);
        pSO.emitTime.setValue(0.001f);
        pSO.material.color.set(0, 0.3, 0.6, 1);

        final Texture treeTex = new Texture(CONST.TEXTURE_FOLDER + "dash.png");
        pSO.setTexture(treeTex);

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
            double time = glfwGetTime();
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
            lightSN.draw(new Matrix4f());
            glfwPollEvents();
        }

    }
}
