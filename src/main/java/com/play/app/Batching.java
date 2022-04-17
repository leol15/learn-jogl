package com.play.app;

import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

import com.play.app.graphics.*;

public class Batching {
    public Batching(long window) {

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer vertices = BufferUtils.createFloatBuffer(4096);
        long size = vertices.capacity() * Float.BYTES;
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);


    }
}
