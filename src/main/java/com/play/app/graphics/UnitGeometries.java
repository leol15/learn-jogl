package com.play.app.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.utils.Func;
import com.play.app.utils.VAO;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.*;

/**
 * Represent unit shapes
 * 
 * For example -Unit plane: [0, 1][0, 1][0, 0]
 * Vertex attribute layout: position(3) normal(3) uv(2)
 */
public class UnitGeometries {

    // solid
    private static VAO CubeVAO;
    private static VAO PlaneVAO;
    private static VAO SphereVAO;
    private static VAO ConeVAO;
    private static VAO CyclinderVAO;
    private static VAO PyramidVAO;

    // wireframe
    private static VAO Circle;

    private static final int ATTR_SIZE = 3 + 3 + 2;

    // must be called to initialize
    public static void initStatic() {
        // solid
        CubeVAO = createCube();
        PlaneVAO = createPlane();
        SphereVAO = createCube();
        ConeVAO = createCube();
        CyclinderVAO = createCube();
        PyramidVAO = createCube();

        // wireframe
        Circle = createCircle();
    }

    public static void drawCube() {
        CubeVAO.draw();
    }

    public static void drawPlane() {
        PlaneVAO.draw();
    }

    public static void drawSphere() {
        SphereVAO.draw();
    }

    public static void drawCone() {
        ConeVAO.draw();
    }

    public static void drawCyclinder() {
        CyclinderVAO.draw();
    }

    public static void drawPyramid() {
        PyramidVAO.draw();
    }

    public static void drawCircle() {
        Circle.draw();
    }

    private static VAO createCube() {
        VAO vao = new VAO();
        // 6 faces, each with 4 points
        FloatBuffer vertices = BufferUtils.createFloatBuffer(6 * 4 * ATTR_SIZE);
        // 6 faces, each with 2 triangles
        IntBuffer elements = BufferUtils.createIntBuffer(6 * 2 * 3);

        // front of cube | back
        // BC | FG
        // AD | EH
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
        addRect(vertices, elements, A, E, H, D, new Vector3f(0, -1, 0));

        vertices.flip();
        elements.flip();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);
        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, 6 * 2 * 3, GL_UNSIGNED_INT, 0));
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

        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, 2 * 3, GL_UNSIGNED_INT, 0));
        return vao;
    }

    private static VAO createCircle() {
        VAO vao = new VAO();
        final int numVerticies = 50;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * numVerticies * 3);
        // x, y
        for (int i = 0; i < numVerticies; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numVerticies);
            float y = (float) Math.cos(i * Math.PI * 2 / numVerticies);
            vertices.put(x).put(y).put(0);
            if (i == 0) {
                Func.p("x" + x + "y" + y);
            }
        }
        vertices.flip();
        vao.bufferVerticies(vertices);
        vao.vertexAttribPointerF(0, 3, 3, 0);

        vao.setDrawFunction(() -> glDrawArrays(GL_LINE_LOOP, 0, numVerticies));

        return vao;
    }
}
