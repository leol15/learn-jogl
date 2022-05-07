package com.play.app;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

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
