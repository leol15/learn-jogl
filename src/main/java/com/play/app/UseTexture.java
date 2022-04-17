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

public class UseTexture {

    public UseTexture(long window) {
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 7);
        // vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f).put(0f);
        // vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f).put(1f);
        // vertices.put(0.6f).put(0.4f).put(0f).put(0f).put(0f).put(1f).put(0f);
        // vertices.put(-0.6f).put(0.4f).put(0f).put(0f).put(0f).put(1f).put(1f);
        // vertices.flip();
        // int vbo = glGenBuffers();
        // glBindBuffer(GL_ARRAY_BUFFER, vbo);
        // glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * (3 + 3));
        vertices.put(0).put(0).put(0).put(0).put(0).put(0);
        vertices.put(1).put(0).put(0).put(1).put(0).put(0);
        vertices.put(0).put(1).put(0).put(0).put(1).put(0);
        vertices.put(0).put(0).put(1).put(0).put(0).put(1);
        vertices.put(1).put(1).put(0).put(1).put(1).put(0);
        vertices.put(1).put(0).put(1).put(1).put(0).put(1);
        vertices.put(0).put(1).put(1).put(0).put(1).put(1);
        vertices.put(1).put(1).put(1).put(1).put(1).put(1);
        vertices.flip();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Element Buffer Object
        // IntBuffer elements = BufferUtils.createIntBuffer(2 * 3);
        // elements.put(0).put(1).put(2)
        //         .put(2).put(3).put(0);
        // elements.flip();
        // int ebo = glGenBuffers();
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        // glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

        IntBuffer elements = BufferUtils.createIntBuffer(6 * 6);
        elements.put(0).put(4).put(1)
            .put(0).put(2).put(4)
            .put(0).put(5).put(1)
            .put(0).put(3).put(5)
            .put(0).put(6).put(2)
            .put(0).put(3).put(6)

            .put(7).put(5).put(1)
            .put(7).put(1).put(4)
            .put(7).put(3).put(5)
            .put(7).put(6).put(3)
            .put(7).put(4).put(2)
            .put(7).put(2).put(6);
        elements.flip();
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);


        // setup shader
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();
        shaderProgram.useProgram();   

        shaderProgram.setVertexAttribPointer("position", 3, 6 * Float.BYTES, 0);
        shaderProgram.setVertexAttribPointer("color",    3, 6 * Float.BYTES, 3 * Float.BYTES);

        // shaderProgram.setVertexAttribPointer("position", 2, 7 * Float.BYTES, 0);
        // shaderProgram.setVertexAttribPointer("color",    3, 7 * Float.BYTES, 2 * Float.BYTES);
        // shaderProgram.setVertexAttribPointer("texcoord", 2, 7 * Float.BYTES, 5 * Float.BYTES);

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
        // Matrix4f projection = new Matrix4f();
        // projection.lookAt(new Vector3f(2, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        // projection.setPerspective(1, 1, 0.5f, 1.5f);
        FloatBuffer fbProjection = BufferUtils.createFloatBuffer(16);
        projection.get(fbProjection);
        shaderProgram.uniformMatrix4fv("projection", fbProjection);

        // set up texture
        Texture texture = new Texture("resources/textures/tree.jpg");
        shaderProgram.uniform("texcoord", 0);


        glBindVertexArray(0);

		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        model.scale(0.5f);

        Vector3f rotateAxis = new Vector3f(1, 1, 1);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time =glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT);

            model.rotate(0.01f, rotateAxis);
            model.get(fbModel);
            shaderProgram.uniformMatrix4fv("model", fbModel);

            // glDrawArrays(GL_TRIANGLES, 0, 3);
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);

            glfwPollEvents();
        }
    }

}
