package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Cube;
import com.play.app.geometry.Ray;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Text;
import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.SceneObject;
import com.play.app.ui.Button;
import com.play.app.ui.CameraControl;
import com.play.app.ui.WindowManager;
import com.play.app.ui.WindowManager.Layer;
import com.play.app.utils.Func;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SelectObject {

    public SelectObject(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraControl camera = new CameraControl(windowManager);

        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.geom", GL_GEOMETRY_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        simple3DShader.uniform("debug", 0);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        // scene
        final SceneNode rootSceneNode = new SceneNode();

        // pen
        // final SceneNode penTip = new SceneNode().setSceneObject(
        //         new SceneObject()
        //                 .setMesh(Mesh.CONE)
        //                 .setShader(simple3DShader)
        //                 .addInstance(new SpacialThing()));
        // penTip.modelInfo.scale.mul(0.5f);
        // penTip.modelInfo.translation.add(0, 2, 0);
        // final SceneNode penBody = new SceneNode().setSceneObject(
        //         new SceneObject()
        //                 .setMesh(Mesh.CYCLINDER)
        //                 .setShader(simple3DShader)
        //                 .addInstance(new SpacialThing()));
        // penBody.modelInfo.scale.mul(0.5f, 2, 0.5f);

        // final SceneNode pen = new SceneNode().addChild(penTip).addChild(penBody);
        // pen.modelInfo.rotation.setAngleAxis((float) Math.PI / 4, 0, 0, 1);
        // pen.modelInfo.translation.add(2, 0, 0);
        // pen.modelInfo.scale.set(0.5f);

        final SpacialThing cube1Transform = new SpacialThing();
        cube1Transform.translation.set(0, 0, 0);
        final SceneNode cube1SceneNode = new SceneNode().setSceneObject(
                new SceneObject()
                        .setShader(simple3DShader)
                        .setMesh(Mesh.CUBE)
                        .setCollidable(new Cube())
                        .addInstance(cube1Transform));
        rootSceneNode.addChild(cube1SceneNode);

        final Matrix4f identity = new Matrix4f();

        // add rays on click
        SceneObject hitLines = new SceneObject()
                .setMesh(Mesh.createCyclinderMesh(3))
                .setShader(simple3DShader)
                .setColor(Func.toVec4(Color.RED));
        rootSceneNode.addChild(new SceneNode().setSceneObject(hitLines));
        SceneObject missLines = new SceneObject()
                .setMesh(Mesh.createCyclinderMesh(3))
                .setShader(simple3DShader)
                .setColor(Func.toVec4(Color.YELLOW));
        rootSceneNode.addChild(new SceneNode().setSceneObject(missLines));

        SceneNode[] selectedNode = { null };

        windowManager.addKeyCallback(Layer.SCENE, (window2, key, code, action, mods) -> {
            if (key == GLFW_KEY_A && action == GLFW_PRESS) {
                windowManager.stopPropagation();
                final Ray ray = camera.getRay(windowManager.lastMousePos[0],
                        windowManager.lastMousePos[1]);

                // move ray forward a bit
                final Vector3f tmp = new Vector3f();
                ray.direction.mul(0.5f, tmp);
                ray.start.add(tmp);

                SpacialThing lineTransform = Func.createLine(ray, 10, 0.02f);
                SceneNode newSelectedNode = rootSceneNode.castRay(ray);
                if (newSelectedNode != null) {
                    if (selectedNode[0] != null) {
                        selectedNode[0].getSceneObject().setColor(Color.WHITE);
                    }
                    newSelectedNode.getSceneObject().setColor(Color.RED);
                    // selectedNode.modelInfo.scale.mul(0.2f);
                    selectedNode[0] = newSelectedNode;
                    hitLines.addInstance(lineTransform);
                } else {
                    missLines.addInstance(lineTransform);
                }
            }
        });

        // ui
        final Button togglePolygonMode = new Button(windowManager, 0, 50, "Toggle Polygon Mode");
        togglePolygonMode.setColor(Color.RED);
        final int[] toggleState = new int[1];
        final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        togglePolygonMode.setAction(() -> {
            glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
            toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        });
        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        // unselect thing when timeout
        int unselectTimer = 0;
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            if (selectedNode[0] != null) {
                unselectTimer--;
                if (unselectTimer == 0) {
                    selectedNode[0].getSceneObject().setColor(Color.WHITE);
                    selectedNode[0] = null;
                } else if (unselectTimer == -1) {
                    unselectTimer = 60;
                }
            }

            // drawing
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewAndProjection(simple3DShader);
            // scene
            rootSceneNode.draw(identity);

            // UI
            camera.draw();
            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            fpsCounter.draw();

            togglePolygonMode.show();

            previousTime = time;
            glfwPollEvents();
        }
    }
}
