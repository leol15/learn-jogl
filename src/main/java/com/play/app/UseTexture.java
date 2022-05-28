package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

import com.play.app.graphics.*;
import com.play.app.utils.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

public class UseTexture {

    public UseTexture(long window) {
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        final VAO vao = new VAO();

        FloatBuffer positions = BufferUtils.createFloatBuffer(3 * 3);
        FloatBuffer UVs = BufferUtils.createFloatBuffer(3 * 2);
        positions.put(0).put(0).put(0);
        UVs.put(0).put(0);

        positions.put(1).put(0).put(0);
        UVs.put(1).put(0);

        positions.put(0).put(1).put(0);
        UVs.put(0).put(1);

        IntBuffer elements = BufferUtils.createIntBuffer(3);
        elements.put(0).put(1).put(2);

        positions.flip();
        UVs.flip();
        elements.flip();

        vao.bufferData(CONST.VERT_IN_POSITION, positions);
        vao.bufferData(CONST.VERT_IN_UV, UVs);
        vao.bufferIndices(elements);

        // setup shader
        ShaderProgram shaderProgram = ShaderUtils.getShader("Texture");
        shaderProgram.linkProgram();
        shaderProgram.useProgram();

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
