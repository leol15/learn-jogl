package com.play.app;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.scene.SceneNode;
import com.play.app.scene.camera.CameraManager;
import com.play.app.scene.sceneobject.InstancingObject;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UseInput {

    public UseInput(long window) {

        glPolygonMode(GL_FRONT, GL_LINE);

        final WindowManager windowManager = new WindowManager(window);
        final CameraManager cam = new CameraManager(windowManager);
        final ShaderProgram simple3dShader = ShaderUtils.getShader("Simple3D");

        final Matrix4f iden = new Matrix4f();
        final SceneNode rootNode = new SceneNode();
        final SpacialThing cube1Transform = new SpacialThing();

        InstancingObject cubes = new InstancingObject()
                .addInstance(cube1Transform);
        cubes.shape.setUnitGeometry(UnitGeometries.Type.Cube);
        cubes.property.setShader(simple3dShader);
        SceneNode cube1 = new SceneNode().setSceneObject(cubes);
        rootNode.addChild(cube1);

        // Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        // fpsCounter.setColor(Color.RED);
        // double previousTime = 0;

        // Button button = new Button(windowManager, 600, 300, 100f, 100f);
        // button.setColor(Color.GREEN);
        // Button button2 = new Button(windowManager, 100, 100, 100f, 100f);
        // Button button3 = new Button(windowManager, 300, 300, "Yoooo");

        // int[] tmp = new int[1];
        // button.setAction(() -> {
        //     System.out.println("Hello world button");
        //     tmp[0]++;
        //     button.setColor(tmp[0] % 2 == 0 ? Color.RED : Color.GREEN);
        // });

        // work
        // final TextInput textInput = new TextInput(windowManager, 300, 600);
        // final SpacialThingEditor spacialThingEditor = new SpacialThingEditor(windowManager, 100, 600);
        // final VectorXfEditor vectorXfEditor = new VectorXfEditor(windowManager, 100, 1000);
        final Vector3f testVec3f = new Vector3f().set(1, 2, 3);
        // vectorXfEditor.setVector(testVec3f);

        // select
        SceneNode[] selectedNode = { null };
        windowManager.addKeyCallback(Layer.SCENE, (window2, key, code, action, mods) -> {
            if (key == GLFW_KEY_A && action == GLFW_PRESS) {
                windowManager.stopPropagation();
                final Ray ray = cam.getCamera().getRay(windowManager.lastMousePos[0],
                        windowManager.lastMousePos[1]);

                // move ray forward a bit
                final Vector3f rayStartDiff = new Vector3f();
                ray.direction.mul(0.5f, rayStartDiff);
                ray.start.add(rayStartDiff);

                // SpacialThing lineTransform = Func.createLine(ray, 10, 0.02f);
                SceneNode newNode = rootNode.castRay(ray);
                if (newNode == selectedNode[0]) {
                    newNode = null;
                }
                if (selectedNode[0] != null) {
                    // TODO edit
                    // selectedNode[0].getSceneObject().setColor(Color.WHITE);
                    selectedNode[0] = null;
                    // spacialThingEditor.setSpacialThing(null);
                    // spacialThingEditor.setVisible(false);
                }
                if (newNode != null) {
                    // newNode.getSceneObject().setColor(Color.RED);
                    selectedNode[0] = newNode;
                    // spacialThingEditor.setSpacialThing(newNode.modelInfo);
                    // spacialThingEditor.setVisible(true);
                }
            }
        });

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // draw

            // debug
            rootNode.draw(iden);
            cam.show();

            // float fps = (float) (1 / (time - previousTime));
            // fpsCounter.setText(String.format("FPS: %.2f", fps), 200, 200);
            // fpsCounter.draw();
            // previousTime = time;

            // button.show();
            // button2.show();
            // button3.show();

            // textInput.show();
            // spacialThingEditor.show();
            // vectorXfEditor.show();

            glfwPollEvents();
        }
    }

}
