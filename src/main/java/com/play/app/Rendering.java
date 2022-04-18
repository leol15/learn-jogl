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
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.play.app.graphics.*;
import com.play.app.utils.VAO;

public class Rendering {

    public Rendering(long window) {
        glEnable(GL_DEPTH_TEST);  

        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // setup shader
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();
        shaderProgram.useProgram();


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


		glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        model.scale(1.3f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time =glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            model.rotate(0.01f, new Vector3f(1, 1, 1));
            model.get(fbModel);

            cubeVao.bind();
            shaderProgram.uniformMatrix4fv("model", fbModel);

            // glDrawArrays(GL_TRIANGLES, 0, 3);
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);


            glfwPollEvents();
        }
    }

}
