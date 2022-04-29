package com.play.app.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.utils.Func;
import com.play.app.utils.VAO;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.glDrawElements;

/**
 * Represent unit shapes
 * 
 * For example -Unit plane: [0, 1][0, 1][0, 0]
 * Vertex attribute layout: position(3) normal(3) uv(2)
 */
public class UnitGeometries {

    private static final VAO CubeVAO = createCube();
    private static final VAO PlaneVAO = createPlane();
    private static final VAO SphereVAO = createCube();
    private static final VAO ConeVAO = createCube();
    private static final VAO CyclinerVAO = createCube();
    private static final VAO PyramidVAO = createCube();
    
    private static final int ATTR_SIZE = 3 + 3 + 2;

    public static void drawCube() {
        CubeVAO.bind();
        glDrawElements(GL_TRIANGLES, 6 * 2 * 3, GL_UNSIGNED_INT, 0);
        CubeVAO.unbind();
    }
    public static void drawPlane() {
        PlaneVAO.bind();
        glDrawElements(GL_TRIANGLES, 2 * 3, GL_UNSIGNED_INT, 0);
        PlaneVAO.unbind();
    }

    private static VAO createCube() {
        VAO vao = new VAO();
        // 6 faces, each with 4 points
        FloatBuffer vertices = BufferUtils.createFloatBuffer(6 * 4 * ATTR_SIZE);
        // 6 faces, each with 2 triangles
        IntBuffer elements = BufferUtils.createIntBuffer(6 * 2 * 3);
        
        // front of cube | back
        // BC            | FG
        // AD            | EH
        final Vector3f A = new Vector3f(0, 0, 0);
        final Vector3f B = new Vector3f(0, 1, 0);
        final Vector3f C = new Vector3f(1, 1, 0);
        final Vector3f D = new Vector3f(1, 0, 0);
        final Vector3f E = new Vector3f(0, 0, 1);
        final Vector3f F = new Vector3f(0, 1, 1);
        final Vector3f G = new Vector3f(1, 1, 1);
        final Vector3f H = new Vector3f(1, 0, 1);

        addRect(vertices, elements, A, B, C, D, new Vector3f(0, 0, 1));        
        addRect(vertices, elements, E, F, B, A, new Vector3f(-1, 0, 0));        
        addRect(vertices, elements, H, G, F, E, new Vector3f(0, 0, -1));        
        addRect(vertices, elements, D, C, G, H, new Vector3f(1, 0, 0));        
        addRect(vertices, elements, B, F, G, C, new Vector3f(0, 1, 0));        
        addRect(vertices, elements, A, E, H, G, new Vector3f(0, -1, 0));        
        
        vertices.flip();
        elements.flip();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);


        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);
        return vao;
    }

    private static void addRect(final FloatBuffer vertices, final IntBuffer elements,
        final Vector3f a, final Vector3f b, final Vector3f c, final Vector3f d,
        final Vector3f normal) {
        
        int idx = vertices.position() / ATTR_SIZE;
        vertices.put(a.x).put(a.y).put(a.z);
        vertices.put(normal.x).put(normal.y).put(normal.z);
        vertices.put(0).put(0);
        
        vertices.put(b.x).put(b.y).put(b.z);
        vertices.put(normal.x).put(normal.y).put(normal.z);
        vertices.put(0).put(1);

        vertices.put(c.x).put(c.y).put(c.z);
        vertices.put(normal.x).put(normal.y).put(normal.z);
        vertices.put(1).put(1);

        vertices.put(d.x).put(d.y).put(d.z);
        vertices.put(normal.x).put(normal.y).put(normal.z);
        vertices.put(1).put(0);

        elements.put(idx).put(idx + 1).put(idx + 2);
        elements.put(idx).put(idx + 2).put(idx + 3);
    }


    private static VAO createPlane() {
        VAO vao = new VAO();
        FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * ATTR_SIZE);
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

        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);
        return vao;
    }
    
}
