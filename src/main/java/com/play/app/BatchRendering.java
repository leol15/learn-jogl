package com.play.app;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.awt.Color;
import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.play.app.geometry.Plane;
import com.play.app.graphics.*;
import com.play.app.ui.Button;
import com.play.app.ui.CameraControl;
import com.play.app.ui.WindowManager;
import com.play.app.utils.CONST;
import com.play.app.utils.Func;
import com.play.app.utils.VAO;

public class BatchRendering {

    public BatchRendering(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraControl camera = new CameraControl(windowManager);

        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        final Matrix4f cubeModel = new Matrix4f().translate(1, 0, 0);
        final Matrix4f cyclinderModel = new Matrix4f().translate(2.5f, 0, 0.5f);
        final Matrix4f coneModel = new Matrix4f().translate(3.5f, 0, 0.5f);
        final Matrix4f planeModel = new Matrix4f().translate(-2, 0, 0);
        final Matrix4f sphereModel = new Matrix4f().translate(0, 0, 0);

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

            // set uniform locations
            camera.setViewAndProjection(simple3DShader);

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
