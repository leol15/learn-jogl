package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.awt.Color;
import java.nio.FloatBuffer;

import com.play.app.graphics.*;
import com.play.app.scene.CameraControl;
import com.play.app.ui.Button;
import com.play.app.utils.*;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class DrawLines {
    public DrawLines(long window) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);

        final VAO lineVAO = new VAO();
        FloatBuffer positions = BufferUtils.createFloatBuffer(3 * 4);
        positions.put(0).put(0).put(0);
        positions.put(0.5f).put(0.5f).put(0);
        positions.put(0).put(0.5f).put(0);
        positions.put(0).put(0.5f).put(0.5f);
        positions.flip();
        lineVAO.bufferData(CONST.VERT_IN_POSITION, positions);
        lineVAO.setDrawFunction(() -> glDrawArrays(GL_LINE_LOOP, 0, 4));

        ShaderProgram lineShader = new ShaderProgram()
                .withShader(CONST.SHADER_FOLDER + "Line.vert", GL_VERTEX_SHADER)
                .withShader(CONST.SHADER_FOLDER + "Line.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        CameraControl cam = new CameraControl(windowManager);

        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.geom", GL_GEOMETRY_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        // ui
        final Button togglePolygonMode = new Button(windowManager, 0, 50, "Toggle Polygon Mode");
        final int[] toggleState = new int[1];
        final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        togglePolygonMode.setAction(() -> {
            glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
            toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        });

        Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            fpsCounter.draw();
            previousTime = time;

            cam.setViewAndProjection(lineShader);
            cam.setViewAndProjection(simple3DShader);

            lineShader.uniform4f("color", new Vector4f(0, 0, 1, 1));
            lineShader.useProgram();
            UnitGeometries.drawCircle();
            lineShader.uniform4f("color", new Vector4f(1, 0, 1, 1));
            lineShader.useProgram();
            lineVAO.draw();
            lineShader.unuseProgram();

            // simple3DShader.useProgram();
            // UnitGeometries.drawCube();
            // simple3DShader.unuseProgram();

            cam.draw();

            togglePolygonMode.show();
            glfwPollEvents();
        }
    }
}
