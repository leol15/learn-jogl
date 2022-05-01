package com.play.app.utils;

import org.lwjgl.*;

import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

public class VAO {

    private int vao;
    private int vboVertices;
    private int vboIndices;
    private Runnable drawFunction;

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
        glBindBuffer(GL_ARRAY_BUFFER, vboVertices);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        unbind();
    }

    public void bufferIndices(IntBuffer elements) {
        bind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        unbind();
    }

    public void vertexAttribPointerF(int index, int size, int stride, int offset) {
        bind();
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false,
                stride * Float.BYTES, offset * Float.BYTES);
        unbind();
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
        // glBindBuffer(GL_ARRAY_BUFFER, 0);
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void delete() {
        if (vao != 0)
            glDeleteVertexArrays(vao);
        if (vboVertices != 0)
            glDeleteBuffers(vboVertices);
        if (vboIndices != 0)
            glDeleteBuffers(vboIndices);
    }

    public void setDrawFunction(Runnable r) {
        drawFunction = r;
    }

    public void unsetDrawFunction() {
        drawFunction = null;
    }

    public void draw() {
        if (drawFunction == null) {
            Func.p("Drawing VAO without setting draw function");
            return;
        }
        bind();
        drawFunction.run();
        unbind();
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
