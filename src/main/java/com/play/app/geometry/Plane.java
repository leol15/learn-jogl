package com.play.app.geometry;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.graphics.ShaderProgram;
import com.play.app.utils.VAO;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

/**
 * Represent a drawable cube
 */
public class Plane {

    private VAO vao;
    public Matrix4f model;
    private FloatBuffer modelBuffer;

    /**
     * Unit plane: [0, 1][0, 1][0, 0]
     * Vertex attribute layout: position(3) normal(3) uv(2)
     */
    public Plane() {
        vao = new VAO();
        FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * (3 + 3 + 2));
        vertices.put(0).put(0).put(0).put(0).put(0).put(1).put(0).put(0);
        vertices.put(0).put(1).put(0).put(0).put(0).put(1).put(0).put(1);
        vertices.put(1).put(1).put(0).put(0).put(0).put(1).put(1).put(1);
        vertices.put(1).put(0).put(0).put(0).put(0).put(1).put(1).put(0);
        
        IntBuffer elements = BufferUtils.createIntBuffer(6);
        elements.put(0).put(1).put(2);
        elements.put(0).put(2).put(3);

        vertices.flip();
        elements.flip();
            
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        int stride = 3 + 3 + 2;
        vao.vertexAttribPointerF(0, 3, stride, 0);
        vao.vertexAttribPointerF(1, 3, stride, 3);
        vao.vertexAttribPointerF(2, 2, stride, 6);

        model = new Matrix4f();
        modelBuffer = BufferUtils.createFloatBuffer(16);
    }


    public void draw(final ShaderProgram program) {
        model.get(modelBuffer);
        program.uniformMatrix4fv("model", modelBuffer);
        vao.bind();
        program.useProgram();
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        program.unuseProgram();
    }


        
}
