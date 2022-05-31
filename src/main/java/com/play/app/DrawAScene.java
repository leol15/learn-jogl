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

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.UnitMesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.SceneObject;
import com.play.app.scene.camera.CameraManager;
import com.play.app.scene.sceneobject.InstancingObject;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.Func;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DrawAScene {

    public DrawAScene(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraManager cameraManager = new CameraManager(windowManager);
        // PropertyEditor propertyEditor = new PropertyEditor(windowManager);
        PropertyEditor propertyEditor = null;

        ShaderProgram simple3DShader = ShaderUtils.getShader("Simple3D");
        simple3DShader.uniform("debug", 1);

        // Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        // fpsCounter.setColor(Color.RED);
        // double previousTime = 0;

        // scene
        final SceneNode penTip = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, new SpacialThing(), Type.Cone));
        penTip.modelInfo.scale.mul(0.5f);
        penTip.modelInfo.translation.add(0, 2, 0);
        final SceneNode penBody = new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, new SpacialThing(), Type.Cyclinder));
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
        clickLines.shape.setMesh(new UnitMesh(UnitGeometries.Type.Cyclinder));
        // clickLines.setColor(Func.toVec4(Color.YELLOW));
        clickLines.property.shader = simple3DShader;

        SceneNode lineSceneNode = new SceneNode().setSceneObject(clickLines);
        rootSceneNode.addChild(lineSceneNode);

        // add all shapes

        final SpacialThing planeModel = new SpacialThing();
        planeModel.translation.set(-2, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, planeModel, Type.Plane)));

        final SpacialThing coneModel = new SpacialThing();
        coneModel.translation.set(3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, coneModel, Type.Cone)));

        final SpacialThing cyclinderModel = new SpacialThing();
        cyclinderModel.translation.set(4.5f, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, cyclinderModel, Type.Cyclinder)));

        final SpacialThing sphereModel = new SpacialThing();
        sphereModel.translation.set(0, 1, 0);
        sphereModel.scale.set(1, 2, 1);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, sphereModel, Type.Sphere)));

        final SpacialThing cubeModel = new SpacialThing();
        cubeModel.translation.set(6, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(simple3DShader, cubeModel, Type.Cube)));

        final ShaderProgram lineShader = ShaderUtils.getShader("Line");
        final SpacialThing cicleModel = new SpacialThing();
        cicleModel.translation.set(-3, 0, 0);
        rootSceneNode.addChild(new SceneNode().setSceneObject(
                createInstancingObjectHelper(lineShader, cicleModel, Type.Circle)));

        SceneNode[] selectedNode = { null };
        windowManager.addCharCallback(Layer.SCENE, (window2, character) -> {
            if (character == 'a') {
                windowManager.stopPropagation();
                final Ray ray = cameraManager.getCamera().getRay(windowManager.lastMousePos[0],
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
                    SpacialThing toSelectLineModel = Func.createLine(
                            new Vector3f(cameraManager.getCamera().position),
                            intersect, 0.1f);
                    clickLines.addInstance(toSelectLineModel);
                }
            }
        });

        // ui
        // final Button togglePolygonMode = new Button(windowManager,
        //         windowManager.windowSize[0] - 200, 50,
        //         "Toggle Polygon Mode");
        // togglePolygonMode.setColor(Color.RED);
        final int[] toggleState = new int[1];
        final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        // togglePolygonMode.setAction(() -> {
        //     glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
        //     toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        // });
        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // scene
            rootSceneNode.draw(identity);

            // UI
            cameraManager.show();
            // float fps = (float) (1 / (time - previousTime));
            // fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            // fpsCounter.draw();

            // togglePolygonMode.show();
            // propertyEditor.show();

            // previousTime = time;
            glfwPollEvents();
        }
    }

    private SceneObject createInstancingObjectHelper(
            ShaderProgram shader, SpacialThing spacialThing, Type type) {
        final InstancingObject o = new InstancingObject()
                .addInstance(spacialThing);
        o.shape.setUnitGeometry(type);
        o.property.setShader(shader);
        return o;
    }

}
