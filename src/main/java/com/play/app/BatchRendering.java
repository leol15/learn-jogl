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
import com.play.app.ui.CameraControl;
import com.play.app.ui.WindowManager;
import com.play.app.utils.CONST;
import com.play.app.utils.VAO;

public class BatchRendering {

    public BatchRendering(long window) {

        // glDepthFunc(GL_LESS);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);
        CameraControl camera = new CameraControl(windowManager);

        // set uniform locations
        Matrix4f identity = new Matrix4f();
        FloatBuffer identifyBuffer = BufferUtils.createFloatBuffer(16);
        identity.get(identifyBuffer);

        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, identifyBuffer);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewAndProjection(simple3DShader);

            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            fpsCounter.draw();

            simple3DShader.useProgram();
            UnitGeometries.drawCube();
            simple3DShader.unuseProgram();

            camera.draw();

            previousTime = time;
            glfwPollEvents();
        }
    }

}
