package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.*;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.ui.*;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;
import com.play.app.graphics.*;

import org.joml.Math;
import org.joml.Matrix4f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DrawAScene {

    public DrawAScene(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraControl camera = new CameraControl(windowManager);
        PropertyEditor propertyEditor = new PropertyEditor(windowManager);

        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.geom", GL_GEOMETRY_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        simple3DShader.uniform("debug", 1);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        // scene
        final SceneNode penTip = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CONE, new SpacialThing()));
        penTip.modelInfo.scale.mul(0.5f);
        penTip.modelInfo.translation.add(0, 2, 0);
        final SceneNode penBody = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CYCLINDER, new SpacialThing()));
        penBody.modelInfo.scale.mul(0.5f, 2, 0.5f);

        final SceneNode pen = new SceneNode().addChild(penTip).addChild(penBody);

        pen.modelInfo.rotation.setAngleAxis((float) Math.PI / 4, 0, 0, 1);
        pen.modelInfo.translation.add(2, 0, 0);
        pen.modelInfo.scale.set(0.5f);

        final SceneNode rootSceneNode = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CUBE, new SpacialThing()));
        // .addChild(pen);

        final Matrix4f identity = new Matrix4f();

        // add rays on click
        InstancingObject clickLines = new InstancingObject()
                .setMesh(Mesh.createCyclinderMesh(3));
        clickLines.setColor(Func.toVec4(Color.YELLOW));
        clickLines.setShader(simple3DShader);

        SceneNode lineSceneNode = new SceneNode().setSceneObject(clickLines);
        rootSceneNode.addChild(lineSceneNode);

        // add all shapes

        final SpacialThing planeModel = new SpacialThing();
        planeModel.translation.set(-2, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.PLANE, planeModel)));

        final SpacialThing coneModel = new SpacialThing();
        coneModel.translation.set(3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CONE, coneModel)));

        final SpacialThing cyclinderModel = new SpacialThing();
        cyclinderModel.translation.set(4.5f, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CYCLINDER, cyclinderModel)));

        final SpacialThing sphereModel = new SpacialThing();
        sphereModel.translation.set(6, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.SPHERE, sphereModel)));

        final ShaderProgram lineShader = new ShaderProgram()
                .withShader("resources/shaders/Line.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Line.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        final SpacialThing cicleModel = new SpacialThing();
        cicleModel.translation.set(-3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(lineShader, Mesh.CIRCLE, cicleModel)));

        SceneNode[] selectedNode = { null };
        windowManager.addCharCallback(Layer.SCENE, (window2, character) -> {
            if (character == 'a') {
                windowManager.stopPropagation();
                final Ray ray = camera.getRay(windowManager.lastMousePos[0],
                        windowManager.lastMousePos[1]);

                SpacialThing lineTransform = Func.createLine(ray, 10, 0.03f);
                clickLines.addInstance(lineTransform);

                // select
                SceneNode node = rootSceneNode.castRay(ray);
                propertyEditor.clear();
                if (selectedNode[0] != null) {
                    selectedNode[0].deselect(propertyEditor);
                    propertyEditor.clear();
                }
                if (node != null) {
                    selectedNode[0] = node;
                    selectedNode[0].select(propertyEditor);
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
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewAndProjection(simple3DShader);
            camera.setViewAndProjection(lineShader);
            // scene
            rootSceneNode.draw(identity);

            // UI
            camera.draw();
            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            fpsCounter.draw();

            togglePolygonMode.show();
            propertyEditor.show();

            previousTime = time;
            glfwPollEvents();
        }
    }

    private SceneObject createInstancingObjectHelper(ShaderProgram shader, Mesh mesh, SpacialThing spacialThing) {
        final InstancingObject o = new InstancingObject()
                .addInstance(spacialThing)
                .setMesh(mesh)
                .setCollidable(new Cube());
        o.setShader(shader);
        return o;
    }

}
