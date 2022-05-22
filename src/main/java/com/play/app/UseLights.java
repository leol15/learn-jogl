package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.GL_MAX_UNIFORM_LOCATIONS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.GL_MAX_UNIFORM_LOCATIONS;

import java.util.*;

import com.play.app.geometry.*;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.lights.LightUBO;
import com.play.app.scene.sceneobject.*;
import com.play.app.ui.PropertyEditor;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.joml.Math;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UseLights {

    public UseLights(long window) {

        int max_uniform = glGetInteger(GL_MAX_UNIFORM_LOCATIONS);
        log.debug("max num uniforms {}", max_uniform);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // init UBOs before shaders
        LightUBO.instance();
        CameraUBO.getInstance();

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cameraControl = new CameraControl(windowManager);
        final SceneNode rootSceneNode = new SceneNode();
        // test
        final SceneManager sceneManager = new SceneManager(windowManager, rootSceneNode, cameraControl);
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

        // construct scene

        final SimpleSceneObject cubeObject = new SimpleSceneObject()
                .setCollidable(new Cube())
                .setMesh(Mesh.CUBE);
        cubeObject.setShader(blinnPhong);
        final SceneNode cubeSN = rootSceneNode.createChild().setSceneObject(cubeObject);
        cubeSN.modelInfo.rotation.setAngleAxis(Math.toRadians(30f), 1, 1, 1);
        cubeSN.modelInfo.translation.set(0, -2, 0);
        cubeSN.modelInfo.scale.set(2, 2, 2);

        final SimpleSceneObject bottomPlane = new SimpleSceneObject()
                .setCollidable(new Plane())
                .setMesh(Mesh.PLANE);
        bottomPlane.setShader(blinnPhong);
        final SceneNode bottomPlaneSN = rootSceneNode.createChild().setSceneObject(bottomPlane);
        bottomPlaneSN.modelInfo.rotation.setAngleAxis(Math.toRadians(30f), 1, 1, 1);
        bottomPlaneSN.modelInfo.rotation.setAngleAxis(Math.toRadians(-90), 1, 0, 0);
        bottomPlaneSN.modelInfo.scale.set(15, 15, 1);

        final LightSceneObject lightSO = new LightSceneObject(lineShader);
        final SceneNode lightNode = rootSceneNode.createChild().setSceneObject(lightSO);
        lightNode.modelInfo.translation.set(2, 2, -2);

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);

            sceneManager.render();

            glfwPollEvents();
        }

    }

    private class SceneManager {
        private final SceneNode root;
        private final CameraControl cam;

        private final PropertyEditor editor;
        private final Matrix4f identity = new Matrix4f();

        private SceneNode selectedNode;

        public SceneManager(WindowManager windowManager, SceneNode root, CameraControl cam) {
            this.root = root;
            this.cam = cam;
            // set up edit area
            editor = new PropertyEditor(windowManager);
            windowManager.addCharCallback(Layer.SCENE, (window2, character) -> {
                if (character == 'a') {
                    windowManager.stopPropagation();
                    final Ray ray = cam.getRay(windowManager.lastMousePos[0], windowManager.lastMousePos[1]);

                    // select
                    SceneNode node = root.castRay(ray);
                    editor.clear();
                    if (selectedNode != null) {
                        selectedNode.deselect(editor);
                        editor.clear();
                    }
                    if (node != null) {
                        selectedNode = node;
                        selectedNode.select(editor);
                    }
                }
            });
        }

        public void render() {
            // prep
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            LightUBO.instance().addAllLights(root);
            // draw
            root.draw(identity);

            cam.draw();
            editor.show();
        }

    }
}
