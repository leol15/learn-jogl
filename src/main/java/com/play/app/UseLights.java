package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

import java.util.*;

import com.play.app.geometry.Cube;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.lights.*;
import com.play.app.utils.*;

import org.joml.Matrix4f;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UseLights {

    public UseLights(long window) {

        int max_uniform = glGetInteger(GL_MAX_UNIFORM_LOCATIONS);
        log.debug("max num uniforms {}", max_uniform);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSceneNode = new SceneNode();
        // test
        final SceneManager sceneManager = new SceneManager(rootSceneNode, cameraControl);
        // shaders needs to be with cameraControl
        final ShaderProgram simple3DShader = new ShaderProgram()
                .withShader(CONST.SHADER_FOLDER + "simple3D.vert")
                .withShader(CONST.SHADER_FOLDER + "simple3D.geom")
                .withShader(CONST.SHADER_FOLDER + "simple3D.frag")
                .linkProgram();
        final ShaderProgram lineShader = new ShaderProgram()
                .withShader(CONST.SHADER_DEFAULT_FOLDER + "Line.vert")
                .withShader(CONST.SHADER_DEFAULT_FOLDER + "Default.frag")
                .linkProgram();
        final ShaderProgram blinnPhong = new ShaderProgram()
                .withShader(CONST.SHADER_FOLDER + "BlinnPhong.vert")
                .withShader(CONST.SHADER_FOLDER + "BlinnPhong.frag")
                .linkProgram();
        sceneManager.addShader(simple3DShader);
        sceneManager.addShader(lineShader);
        sceneManager.addShader(blinnPhong);

        // construct scene

        final SimpleSceneObject cubeObject = new SimpleSceneObject()
                .setCollidable(new Cube())
                .setMesh(Mesh.CUBE);
        cubeObject.setShader(simple3DShader);
        rootSceneNode.createChild().setSceneObject(cubeObject);

        final LightSceneObject lightSO = new LightSceneObject(lineShader);
        final SceneNode lightNode = rootSceneNode.createChild().setSceneObject(lightSO);
        lightNode.modelInfo.translation.set(0, 2, 0);

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);

            sceneManager.render();

            glfwPollEvents();
        }

    }

    @RequiredArgsConstructor
    private class SceneManager {
        private final SceneNode root;
        private final CameraControl cam;
        private final List<ShaderProgram> shaders = new ArrayList<>();
        private final Matrix4f identity = new Matrix4f();

        public void render() {
            // prep
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            // draw
            root.draw(identity);
            cam.draw();
        }

        public SceneManager addShader(ShaderProgram sp) {
            shaders.add(sp);
            return this;
        }
    }
}
