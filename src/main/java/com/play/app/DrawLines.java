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
import com.play.app.utils.VAO;

public class DrawLines {
    public DrawLines(long window) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);

        VAO lineVAO = new VAO();
        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * 4);
        vertices.put(0).put(0).put(0);
        vertices.put(0.5f).put(0.5f).put(0);
        vertices.put(0).put(0.5f).put(0);
        vertices.put(0).put(0.5f).put(0.5f);
        vertices.flip();
        lineVAO.bufferVerticies(vertices);
        lineVAO.vertexAttribPointerF(0, 3, 3, 0);

        ShaderProgram shader = new ShaderProgram()
                .withShader(CONST.SHADER_FOLDER + "Line.vert", GL_VERTEX_SHADER)
                .withShader(CONST.SHADER_FOLDER + "Line.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        CameraControl cam = new CameraControl(windowManager);

        FloatBuffer idenBuf = BufferUtils.createFloatBuffer(16);
        Matrix4f iden = new Matrix4f();
        iden.get(idenBuf);
        shader.uniformMatrix4fv(CONST.MODEL_MATRIX, idenBuf);

        iden.get(idenBuf);
        ShaderProgram simple3DShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        simple3DShader.uniformMatrix4fv(CONST.MODEL_MATRIX, idenBuf);

        // ui
        final Button togglePolygonMode = new Button(windowManager, 0, 50, "Toggle Polygon Mode");
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

            cam.setViewAndProjection(shader);
            cam.setViewAndProjection(simple3DShader);

            shader.useProgram();
            lineVAO.bind();
            glDrawArrays(GL_LINE_LOOP, 0, 4);
            lineVAO.unbind();
            shader.unuseProgram();

            simple3DShader.useProgram();
            UnitGeometries.drawCube();
            simple3DShader.unuseProgram();

            cam.draw();

            togglePolygonMode.show();
            glfwPollEvents();
        }
    }
}
