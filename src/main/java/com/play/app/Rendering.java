package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.nio.FloatBuffer;

import com.play.app.geometry.Plane;
import com.play.app.graphics.ShaderProgram;
import com.play.app.utils.VAO;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Rendering {

    public Rendering(long window) {
        glEnable(GL_DEPTH_TEST);
        // glDepthFunc(GL_LESS);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // setup shader
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();

        VAO cubeVao = VAO.createCube();
        cubeVao.vertexAttribPointerF(0, 3, 6, 0);
        cubeVao.vertexAttribPointerF(1, 3, 6, 3);

        // set uniform locations
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

        ShaderProgram planeShader = new ShaderProgram()
                .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();
        planeShader.uniformMatrix4fv("view", fbView);
        planeShader.uniformMatrix4fv("projection", fbProjection);
        Plane p = new Plane();
        p.model
                .rotate(0.5f, new Vector3f(1, 0, 0))
                .translate(0, 0, -0.5f);

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        model.scale(0.6f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            model.rotate(0.01f, new Vector3f(1, 1, 1));
            model.get(fbModel);

            shaderProgram.uniformMatrix4fv("model", fbModel);

            shaderProgram.useProgram();
            // glDrawArrays(GL_TRIANGLES, 0, 3);
            cubeVao.bind();
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);

            p.model.translate(0, 0, 0.001f);
            p.draw(planeShader);

            glfwPollEvents();
        }
    }

}
