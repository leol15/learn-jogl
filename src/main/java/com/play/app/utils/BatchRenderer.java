package com.play.app.utils;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import com.play.app.graphics.ShaderProgram;

/**
 * Renders a series of triangles with the same ShaderProgram
 */
public class BatchRenderer {

    public BatchRenderer() {
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vboVertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertices);

        int vboIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);

        glBindVertexArray(0);
    }

    public void useShaderProgram(final ShaderProgram program) {

    }

    public void draw() {

    }

}
