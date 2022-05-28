package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.awt.Color;

import com.play.app.basics.*;
import com.play.app.geometry.*;
import com.play.app.graphics.*;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.scene.sceneobject.*;
import com.play.app.ui.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.joml.Math;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DrawAScene {

    public DrawAScene(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraControl camera = new CameraControl(windowManager);
        PropertyEditor propertyEditor = new PropertyEditor(windowManager);

        ShaderProgram simple3DShader = ShaderUtils.getShader("Simple3D");
        simple3DShader.uniform("debug", 1);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        // scene
        final SceneNode penTip = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CONE, new SpacialThing(),
                        new Cube()));
        penTip.modelInfo.scale.mul(0.5f);
        penTip.modelInfo.translation.add(0, 2, 0);
        final SceneNode penBody = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CYCLINDER, new SpacialThing(),
                        new Cube()));
        penBody.modelInfo.scale.mul(0.5f, 2, 0.5f);

        final SceneNode pen = new SceneNode().addChild(penTip).addChild(penBody);

        pen.modelInfo.rotation.setAngleAxis((float) Math.PI / 4, 0, 0, 1);
        pen.modelInfo.translation.add(2, 0, 0);
        pen.modelInfo.scale.set(0.5f);

        final SceneNode rootSceneNode = new SceneNode();
        rootSceneNode.addChild(pen);

        final Matrix4f identity = new Matrix4f();

        // add rays on click
        InstancingObject clickLines = new InstancingObject();
        clickLines.shape.setMesh(Mesh.createCyclinderMesh(3));
        // clickLines.setColor(Func.toVec4(Color.YELLOW));
        clickLines.property.shader = simple3DShader;

        SceneNode lineSceneNode = new SceneNode().setSceneObject(clickLines);
        rootSceneNode.addChild(lineSceneNode);

        // add all shapes

        final SpacialThing planeModel = new SpacialThing();
        planeModel.translation.set(-2, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.PLANE, planeModel, new Plane())));

        final SpacialThing coneModel = new SpacialThing();
        coneModel.translation.set(3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CONE, coneModel, new Cube())));

        final SpacialThing cyclinderModel = new SpacialThing();
        cyclinderModel.translation.set(4.5f, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CYCLINDER, cyclinderModel,
                        new Cube())));

        final SpacialThing sphereModel = new SpacialThing();
        sphereModel.translation.set(0, 1, 0);
        sphereModel.scale.set(1, 2, 1);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.SPHERE, sphereModel, new Sphere())));

        final SpacialThing cubeModel = new SpacialThing();
        cubeModel.translation.set(6, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, Mesh.CUBE, cubeModel, new Cube())));

        final ShaderProgram lineShader = ShaderUtils.getShader("Line");
        final SpacialThing cicleModel = new SpacialThing();
        cicleModel.translation.set(-3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(lineShader, Mesh.CIRCLE, cicleModel, new Cube())));

        SceneNode[] selectedNode = { null };
        windowManager.addCharCallback(Layer.SCENE, (window2, character) -> {
            if (character == 'a') {
                windowManager.stopPropagation();
                final Ray ray = camera.getRay(windowManager.lastMousePos[0],
                        windowManager.lastMousePos[1]);

                // SpacialThing lineTransform = Func.createLine(ray, 10, 0.03f);
                // clickLines.addInstance(lineTransform);

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
                    // draw a better line
                    final Vector3f intersect = selectedNode[0].getSceneObject().intersectRay(ray,
                            new Matrix4f());
                    if (intersect == null) {
                        log.error("could not find intersect of selected object");
                    }
                    SpacialThing toSelectLineModel = Func.createLine(camera.getCameraPosition(),
                            intersect, 0.1f);
                    clickLines.addInstance(toSelectLineModel);
                }
            }
        });

        // ui
        final Button togglePolygonMode = new Button(windowManager,
                windowManager.windowSize[0] - 200, 50,
                "Toggle Polygon Mode");
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

    private SceneObject createInstancingObjectHelper(
            ShaderProgram shader, Mesh mesh, SpacialThing spacialThing,
            Collidable collidable) {
        final InstancingObject o = new InstancingObject()
                .addInstance(spacialThing);
        o.shape.setMesh(mesh)
                .setCollidable(collidable);
        o.property.setShader(shader);
        return o;
    }

}
