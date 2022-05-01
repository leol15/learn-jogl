package com.play.app;

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
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Text;
import com.play.app.mesh.CubeMesh;
import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.SceneObject;
import com.play.app.ui.Button;
import com.play.app.ui.CameraControl;
import com.play.app.ui.WindowManager;

import org.joml.Math;
import org.joml.Matrix4f;

public class DrawAScene {

    public DrawAScene(long window) {

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
        final SceneNode penTip = new SceneNode().setSceneObject(
                new SceneObject()
                        .setMesh(Mesh.CUBE)
                        .setShader(simple3DShader)
                        .addInstance(new SpacialThing()));
        penTip.modelInfo.scale.mul(0.5f);
        penTip.modelInfo.translation.add(0, 2, 0);
        final SceneNode penBody = new SceneNode().setSceneObject(
                new SceneObject()
                        .setMesh(Mesh.CUBE)
                        .setShader(simple3DShader)
                        .addInstance(new SpacialThing()));
        penBody.modelInfo.scale.mul(0.5f, 2, 0.5f);

        final SceneNode pen = new SceneNode().addChild(penTip).addChild(penBody);

        pen.modelInfo.rotation.setAngleAxis((float) Math.PI / 4, 0, 0, 1);
        pen.modelInfo.translation.add(2, 0, 0);
        pen.modelInfo.scale.set(0.5f);

        final SceneNode rootSceneNode = new SceneNode().setSceneObject(
                new SceneObject()
                        .setMesh(Mesh.CUBE)
                        .setShader(simple3DShader)
                        .addInstance(new SpacialThing()))
                .addChild(pen);

        final Matrix4f identity = new Matrix4f();

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
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
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
