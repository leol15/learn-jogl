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
import static org.lwjgl.stb.STBImage.*;

import com.play.app.graphics.*;

public class UseTexture {

    public UseTexture(long window) {

        // debug
        // glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);

        // int vao = glGenVertexArrays();
        // glBindVertexArray(vao);

        // try (MemoryStack stack = MemoryStack.stackPush()) {
        //     FloatBuffer vertices = stack.mallocFloat(3 * 6);
        //     vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
        //     vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
        //     vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
        //     vertices.flip();

        //     int vbo = glGenBuffers();
        //     glBindBuffer(GL_ARRAY_BUFFER, vbo);
        //     glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        // }


        // shader setup
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Texture.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Texture.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();
        shaderProgram.useProgram();   
        shaderProgram.setVertexAttribPointer("position", 2, 7 * Float.BYTES, 0);
        shaderProgram.setVertexAttribPointer("color",    3, 7 * Float.BYTES, 2 * Float.BYTES);
        shaderProgram.setVertexAttribPointer("texcoord", 2, 7 * Float.BYTES, 5 * Float.BYTES);
        System.out.println("float size " + Float.BYTES);
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

        // set shader position
        shaderProgram.uniform("texImage", 0);

        // texture
        Texture texture = new Texture("resources/textures/tree.jpg");


        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Element Buffer Object
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        IntBuffer elements = BufferUtils.createIntBuffer(2 * 3);
        elements.put(0).put(1).put(2)
                .put(2).put(3).put(0);
        elements.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
        int width = widthBuffer.get();
        int height = heightBuffer.get();

        float x1 = (width - texture.getWidth()) / 2f;
        float y1 = (height - texture.getHeight()) / 2f;
        float x2 = x1 + texture.getWidth();
        float y2 = y1 + texture.getHeight();
        System.out.println("x1 " + x1);
        System.out.println("y1 " + y1);
        System.out.println("x2 " + x2);
        System.out.println("y2 " + y2);
        x1 = 0f;
        y1 = 0f;
        x2 = 10f;
        y2 = 10f;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 7);
        // vertices.put(x1).put(y1).put(1f).put(1f).put(1f).put(0f).put(0f);
        // vertices.put(x2).put(y1).put(1f).put(1f).put(1f).put(1f).put(0f);
        // vertices.put(x2).put(y2).put(1f).put(1f).put(1f).put(1f).put(1f);
        // vertices.put(x1).put(y2).put(1f).put(1f).put(1f).put(0f).put(1f);
    
        vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f).put(1);
        vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f).put(1);
        vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f).put(1);
        vertices.put(-0.6f).put(0.4f).put(0f).put(0f).put(0f).put(1f).put(1);
        vertices.flip();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT);

            model.rotate(0.01f, new Vector3f(0, 0, 1));
            model.get(fbModel);
            shaderProgram.uniformMatrix4fv("model", fbModel);

            // glDrawArrays(GL_TRIANGLES, 0, 3);
            glBindVertexArray(vao);
            texture.bindTexture();
            shaderProgram.useProgram();

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            glfwPollEvents();
        }
    }

    private void loadImage() {
        
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load("/textures/tree.jpg", w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();
        }
    }

}
