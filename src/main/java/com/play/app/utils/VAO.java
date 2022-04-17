package com.play.app.utils;


import org.lwjgl.*;

import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

public class VAO {

    int vao;
    int vboVertices;
    int vboIndices;

    public VAO() {
        vao = glGenVertexArrays();
        bind();
        vboVertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertices);
        
        vboIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
        unbind();
    }

    public void bufferVerticies(FloatBuffer vertices) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        unbind();
    }

    public void bufferIndices(IntBuffer elements) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        unbind();
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        if (vao != 0) glDeleteVertexArrays(vao);
        if (vboVertices != 0) glDeleteBuffers(vboVertices);
        if (vboIndices != 0) glDeleteBuffers(vboIndices);
    }

    public static VAO createCube() {
        VAO vao = new VAO();
        
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

        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        return vao;
    }
}
