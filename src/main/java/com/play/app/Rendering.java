package com.play.app;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.play.app.graphics.*;

public class Rendering {

    public Rendering(long window) {
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }


        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("/shaders/Simple.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("/shaders/Simple.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();
        shaderProgram.useProgram();   

        // setup VAO
        int floatSize = 4;

        shaderProgram.setVertexAttribPointer("position", 3, 6 * floatSize, 0);
        shaderProgram.setVertexAttribPointer("color",    3, 6 * floatSize, 3 * floatSize);

        // set uniform location
        Matrix4f model = new Matrix4f();
        FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
        model.get(fbModel);
        shaderProgram.uniformMatrix4fv("model", fbModel);

        Matrix4f view = new Matrix4f();
        FloatBuffer fbView = BufferUtils.createFloatBuffer(16);
        view.get(fbView);
        shaderProgram.uniformMatrix4fv("view", fbView);

        Matrix4f projection = new Matrix4f().ortho(-1f, 1f, -1f, 1f, -1f, 1f);
        FloatBuffer fbProjection = BufferUtils.createFloatBuffer(16);
        projection.get(fbProjection);
        shaderProgram.uniformMatrix4fv("projection", fbProjection);

		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time =glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT);

            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwPollEvents();
        }
    }

}
