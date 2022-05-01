package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Texture;
import com.play.app.utils.VAO;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class UseTexture {

    public UseTexture(long window) {
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        VAO vao = new VAO();

        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * (3 + 2));
        vertices.put(0).put(0).put(0).put(0).put(0);
        vertices.put(1).put(0).put(0).put(1).put(0);
        vertices.put(0).put(1).put(0).put(0).put(1);
        vertices.flip();

        IntBuffer elements = BufferUtils.createIntBuffer(3);
        elements.put(0).put(1).put(2);
        elements.flip();

        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        // setup shader
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Texture.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Texture.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();
        shaderProgram.useProgram();

        vao.vertexAttribPointerF(0, 3, 5, 0);
        vao.vertexAttribPointerF(1, 2, 5, 3);
        // shaderProgram.setVertexAttribPointer("position", 3, 6 * Float.BYTES, 0);
        // shaderProgram.setVertexAttribPointer("color", 3, 6 * Float.BYTES, 3 *
        // Float.BYTES);

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

        // set up texture
        Texture texture = new Texture("resources/textures/tree.jpg");
        // shaderProgram.uniform("texImage", 0);
        // int loc = glGetUniformLocation(shaderProgram.getId(), "texImage");
        // int uniTex = program.getUniformLocation("texImage");
        // program.setUniform(uniTex, 0);

        glClearColor(0.2f, 0.2f, 0.2f, 0.0f);

        model.scale(0.5f);

        Vector3f rotateAxis = new Vector3f(1, 0, 0);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            model.rotate(0.05f, rotateAxis);
            model.get(fbModel);
            shaderProgram.uniformMatrix4fv("model", fbModel);

            vao.bind();
            shaderProgram.useProgram();
            texture.bindTexture();

            glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);

            vao.unbind();
            shaderProgram.unuseProgram();
            texture.unbindTexture();

            glfwPollEvents();
        }
    }

}
