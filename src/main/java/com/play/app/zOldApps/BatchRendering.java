package com.play.app.zOldApps;

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

import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.scene.camera.CameraManager;
import com.play.app.utils.CONST;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

import org.joml.Matrix4f;

public class BatchRendering {

    public BatchRendering(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraManager cameraManager = new CameraManager(windowManager);

        ShaderProgram simple3DShader = ShaderUtils.getShader("Simple3D");

        // Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        // fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        final Matrix4f cubeModel = new Matrix4f().translate(-2.5f, 0, 0);
        final Matrix4f cyclinderModel = new Matrix4f().translate(-0.5f, 0, 0.5f);
        final Matrix4f coneModel = new Matrix4f().translate(1f, 0, 0.5f);
        final Matrix4f planeModel = new Matrix4f().translate(2, 0, 0);
        final Matrix4f sphereModel = new Matrix4f().translate(3.5f, 0.5f, 0.5f);

        // ui
        // final Button togglePolygonMode = new Button(windowManager, 0, 50, "Toggle Polygon Mode");
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

            // set uniform locations
            simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, cubeModel);
            simple3DShader.useProgram();
            UnitGeometries.drawCube();

            simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, cyclinderModel);
            simple3DShader.useProgram();
            UnitGeometries.drawCyclinder();

            simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, coneModel);
            simple3DShader.useProgram();
            UnitGeometries.drawCone();

            simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, planeModel);
            simple3DShader.useProgram();
            UnitGeometries.drawPlane();

            simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, sphereModel);
            simple3DShader.useProgram();
            UnitGeometries.drawSphere();

            simple3DShader.unuseProgram();

            // UI
            cameraManager.show();
            float fps = (float) (1 / (time - previousTime));
            // fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            // fpsCounter.draw();

            // togglePolygonMode.show();

            previousTime = time;
            glfwPollEvents();
        }
    }

}
