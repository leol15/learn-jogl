package com.play.app.graphics;

import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

import com.play.app.utils.Func;

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
        // crashes
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

}
