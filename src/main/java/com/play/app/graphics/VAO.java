package com.play.app.graphics;

import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

import com.play.app.utils.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class VAO {

    private int vao = 0;
    private int[] vertexShaderVBOs;
    private int elementVBO = 0;
    private Runnable drawFunction;

    public VAO() {
        vao = glGenVertexArrays();
        bind();

        // vertex attributes
        vertexShaderVBOs = new int[CONST.VERT_IN_ATTRS.length];
        glGenBuffers(vertexShaderVBOs);

        for (int i = 0; i < CONST.VERT_IN_ATTRS.length; i++) {
            final int attrIndex = CONST.VERT_IN_ATTRS[i][0];
            final int attrSize = CONST.VERT_IN_ATTRS[i][1];
            glBindBuffer(GL_ARRAY_BUFFER, vertexShaderVBOs[attrIndex]);
            glEnableVertexAttribArray(attrIndex);
            glVertexAttribPointer(attrIndex, attrSize, GL_FLOAT, false, attrSize * Float.BYTES, 0);
            glDisableVertexAttribArray(attrIndex);
        }

        // indices for draw
        elementVBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementVBO);

        unbind();
    }

    public VAO bufferData(int attrIndex, final FloatBuffer data) {
        bind();
        glEnableVertexAttribArray(attrIndex);
        glBindBuffer(GL_ARRAY_BUFFER, vertexShaderVBOs[attrIndex]);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        unbind();
        return this;
    }

    public VAO disableVertexAttribArray(int attrIndex) {
        bind();
        glDisableVertexAttribArray(attrIndex);
        unbind();
        return this;
    }

    public VAO enableVertexAttribArray(int attrIndex) {
        bind();
        glEnableVertexAttribArray(attrIndex);
        unbind();
        return this;
    }

    @Deprecated
    public void bufferVerticies(FloatBuffer vertices) {
        bind();
        glBindBuffer(GL_ARRAY_BUFFER, vertexShaderVBOs[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        unbind();
    }

    public void bufferIndices(IntBuffer elements) {
        bind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        unbind();
    }

    @Deprecated
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
        for (int i = 0; i < vertexShaderVBOs.length; i++) {
            if (vertexShaderVBOs[i] != 0)
                glDeleteBuffers(vertexShaderVBOs[i]);
        }
        if (elementVBO != 0)
            glDeleteBuffers(elementVBO);
    }

    public void setDrawFunction(Runnable r) {
        drawFunction = r;
    }

    public void draw() {
        if (drawFunction == null) {
            log.warn("Drawing VAO without setting draw function");
            return;
        }
        bind();
        drawFunction.run();
        unbind();
    }

}
