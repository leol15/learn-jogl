package com.play.app.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

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

    // mapping from resolution to VAO
    // solid
    private static VAO CubeVAO;
    private static Map<Integer, VAO> SphereVAO = new HashMap<>();
    private static Map<Integer, VAO> ConeVAO = new HashMap<>();
    private static Map<Integer, VAO> CyclinderVAO = new HashMap<>();

    // wireframe
    private static VAO PlaneVAO;
    private static Map<Integer, VAO> CircleVAO = new HashMap<>();

    private static final int ATTR_SIZE = 3 + 3 + 2;
    private static final int DEFAULT_NUM_SECTIONS = 10;

    // must be called to initialize
    public static void initStatic() {
        // solid
        CubeVAO = createCube();
        SphereVAO.put(0, createSphere(DEFAULT_NUM_SECTIONS, DEFAULT_NUM_SECTIONS));
        ConeVAO.put(0, createCone(DEFAULT_NUM_SECTIONS));
        CyclinderVAO.put(0, createCyclinder(DEFAULT_NUM_SECTIONS));

        // wireframe
        PlaneVAO = createPlane();
        CircleVAO.put(0, createCircle(DEFAULT_NUM_SECTIONS));
    }

    ////////////////////
    // draw functions
    ////////////////////
    public static void drawCube() {
        CubeVAO.draw();
    }

    public static void drawPlane() {
        PlaneVAO.draw();
    }

    public static void drawSphere() {
        drawSphere(DEFAULT_NUM_SECTIONS);
    }

    public static void drawCone() {
        drawCone(DEFAULT_NUM_SECTIONS);
    }

    public static void drawCyclinder() {
        drawCyclinder(DEFAULT_NUM_SECTIONS);
    }

    public static void drawCircle() {
        drawCircle(DEFAULT_NUM_SECTIONS);
    }

    public static void drawSphere(final int numSections) {
        if (!SphereVAO.containsKey(numSections)) {
            VAO missing = createSphere(numSections, numSections);
            SphereVAO.put(numSections, missing);
        }
        SphereVAO.get(numSections).draw();
    }

    public static void drawCone(final int numSections) {
        if (!ConeVAO.containsKey(numSections)) {
            VAO missing = createCone(numSections);
            ConeVAO.put(numSections, missing);
        }
        ConeVAO.get(numSections).draw();
    }

    public static void drawCyclinder(final int numSections) {
        if (!CyclinderVAO.containsKey(numSections)) {
            VAO missing = createCyclinder(numSections);
            CyclinderVAO.put(numSections, missing);
        }
        CyclinderVAO.get(numSections).draw();
    }

    public static void drawCircle(final int numSections) {
        if (!CircleVAO.containsKey(numSections)) {
            VAO missing = createCircle(numSections);
            CircleVAO.put(numSections, missing);
        }
        CircleVAO.get(numSections).draw();
    }

    ////////////////////
    // construct these unit shapes
    ////////////////////
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

    private static VAO createCircle(final int numSections) {
        VAO vao = new VAO();
        final int numVerticies = numSections;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * numVerticies * 3);
        // x, y
        for (int i = 0; i < numVerticies; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numVerticies);
            float y = (float) Math.cos(i * Math.PI * 2 / numVerticies);
            vertices.put(x).put(y).put(0);
        }
        vertices.flip();
        vao.bufferVerticies(vertices);
        vao.vertexAttribPointerF(0, 3, 3, 0);

        vao.setDrawFunction(() -> glDrawArrays(GL_LINE_LOOP, 0, numVerticies));

        return vao;
    }

    private static VAO createCone(final int numSections) {
        // TODO add uv
        final VAO vao = new VAO();
        final int numCircleFragment = numSections;
        // double the circle since the normal is different
        final int numTriangles = numCircleFragment * 2;
        final int numVerticies = numCircleFragment * 2 + 2;

        final FloatBuffer vertices = BufferUtils.createFloatBuffer((numVerticies + 2) * ATTR_SIZE);
        final IntBuffer elements = BufferUtils.createIntBuffer(numCircleFragment * 2 * 3);

        // the tip of cone: (0, 1, 0)
        vertices.put(0).put(1).put(0).put(0).put(1).put(0).put(0.5f).put(1);
        // the side circle
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vertices.put(x).put(0).put(y);
            vertices.put(x).put(0).put(y);
            vertices.put((float) i / numCircleFragment).put(0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                elements.put(i + 1).put(0).put(i + 2);
            }
        }

        // the base center point
        vertices.put(0).put(0).put(0).put(0).put(-1).put(0).put(0f).put(0);
        final int baseIdx = vertices.position() / ATTR_SIZE - 1;
        // the bottom circle
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vertices.put(x).put(0).put(y);
            vertices.put(0).put(-1).put(0);
            vertices.put((float) i / numCircleFragment).put(0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                elements.put(baseIdx + i + 1).put(baseIdx).put(baseIdx + i + 2);
            }
        }

        vertices.flip();
        elements.flip();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);

        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vao;
    }

    private static VAO createCyclinder(final int numSections) {
        // TODO add uv
        final VAO vao = new VAO();
        final int numCircleFragment = numSections;
        // double the circle since the normal is different
        final int numTriangles = numCircleFragment * 4;
        final int numVerticies = numCircleFragment * 4 + 2;

        // +4 to make [start, end] meet
        final FloatBuffer vertices = BufferUtils.createFloatBuffer((numVerticies + 4) * ATTR_SIZE);
        final IntBuffer elements = BufferUtils.createIntBuffer(numCircleFragment * 4 * 3);
        int baseIdx;

        // the top circle
        vertices.put(0).put(1).put(0).put(0).put(1).put(0).put(1).put(1);
        baseIdx = vertices.position() / ATTR_SIZE - 1;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vertices.put(x).put(1).put(y);
            vertices.put(0).put(1).put(0);
            vertices.put((float) i / numCircleFragment).put(0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                elements.put(baseIdx + i + 1).put(baseIdx).put(baseIdx + i + 2);
            }
        }

        // the base circle
        vertices.put(0).put(0).put(0).put(0).put(-1).put(0).put(0f).put(0);
        baseIdx = vertices.position() / ATTR_SIZE - 1;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vertices.put(x).put(0).put(y);
            vertices.put(0).put(-1).put(0);
            vertices.put((float) i / numCircleFragment).put(0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                elements.put(baseIdx + i + 1).put(baseIdx).put(baseIdx + i + 2);
            }
        }

        // the side
        baseIdx = vertices.position() / ATTR_SIZE;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            // bottom
            vertices.put(x).put(0).put(y);
            vertices.put(x * 2).put(0).put(y * 2);
            vertices.put((float) i / numCircleFragment).put(0);
            // top
            vertices.put(x).put(1).put(y);
            vertices.put(x * 2).put(0).put(y * 2);
            vertices.put((float) i / numCircleFragment).put(0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                // 24
                // 13
                int triangleBase = baseIdx + i * 2;
                elements.put(triangleBase).put(triangleBase + 1).put(triangleBase + 3);
                elements.put(triangleBase).put(triangleBase + 3).put(triangleBase + 2);
            }
        }

        vertices.flip();
        elements.flip();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);

        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vao;
    }

    private static void addVertex(final FloatBuffer vertices, final Vector3f tmpV, final float sliceFraction) {
        vertices.put(tmpV.x).put(tmpV.y).put(tmpV.z);
        tmpV.normalize();
        vertices.put(tmpV.x).put(tmpV.y).put(tmpV.z);
        // uv
        vertices.put(sliceFraction).put(tmpV.y);
    }

    private static VAO createSphere(final int numSlices, final int numLevels) {
        // layout:: ring major
        // TODO add uv
        final VAO vao = new VAO();
        // double the circle since the normal is different
        final int numTriangles = numSlices * 2 + (numLevels - 2) * 2 * numSlices;
        final int numVerticies = 2 + (numLevels - 1) * (numSlices + 1);

        // +4 to make [start, end] meet
        final FloatBuffer vertices = BufferUtils.createFloatBuffer(numVerticies * ATTR_SIZE);
        final IntBuffer elements = BufferUtils.createIntBuffer(numTriangles * 3);
        int baseIdx;

        final Vector3f tmpV = new Vector3f();

        final float sliceAngle = (float) Math.PI / numSlices;
        float currSliceAngle = sliceAngle;
        float levelRadius = (float) Math.sin(currSliceAngle) / 2;
        float levelHeight = (float) Math.cos(currSliceAngle) / 2;

        // the top level
        vertices.put(0).put(0.5f).put(0).put(0).put(1).put(0).put(1).put(1);
        baseIdx = vertices.position() / ATTR_SIZE - 1;
        for (int i = 0; i < numSlices + 1; i++) {
            final float x = (float) Math.sin(i * Math.PI * 2 / numSlices) * levelRadius;
            final float y = (float) Math.cos(i * Math.PI * 2 / numSlices) * levelRadius;
            tmpV.set(x, levelHeight, y);
            addVertex(vertices, tmpV, (float) i / numSlices);
            // last point does not have next triangle
            if (i != numSlices) {
                elements.put(baseIdx + i + 1).put(baseIdx).put(baseIdx + i + 2);
            }
        }

        // the middle layers, previous layer is writter
        for (int level = 0; level < numLevels - 2; level++) {
            currSliceAngle += sliceAngle;
            levelRadius = (float) Math.sin(currSliceAngle) / 2;
            levelHeight = (float) Math.cos(currSliceAngle) / 2;
            baseIdx = vertices.position() / ATTR_SIZE;
            for (int i = 0; i < numSlices + 1; i++) {
                final float x = (float) Math.sin(i * Math.PI * 2 / numSlices) * levelRadius;
                final float y = (float) Math.cos(i * Math.PI * 2 / numSlices) * levelRadius;
                tmpV.set(x, levelHeight, y);
                addVertex(vertices, tmpV, (float) i / numSlices);
                // last point does not have next triangle
                if (i != numSlices) {
                    elements.put(baseIdx + i)
                            .put(baseIdx + i - (numSlices + 1))
                            .put(baseIdx + i - (numSlices + 1) + 1);

                    elements.put(baseIdx + i)
                            .put(baseIdx + i - (numSlices + 1) + 1)
                            .put(baseIdx + i + 1);
                }
            }
        }

        // the bottom level, previous level is written
        currSliceAngle = (float) Math.PI - sliceAngle;
        levelRadius = (float) Math.sin(currSliceAngle) / 2;
        levelHeight = (float) Math.cos(currSliceAngle) / 2;
        vertices.put(0).put(-0.5f).put(0).put(0).put(-1).put(0).put(1).put(1);
        baseIdx = vertices.position() / ATTR_SIZE - 1;
        for (int i = 0; i < numSlices + 1; i++) {
            // last point does not have next triangle
            if (i != numSlices) {
                elements.put(baseIdx - i - 1).put(baseIdx).put(baseIdx - i - 2);
            }
        }

        vertices.flip();
        elements.flip();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);

        vao.vertexAttribPointerF(0, 3, ATTR_SIZE, 0);
        vao.vertexAttribPointerF(1, 3, ATTR_SIZE, 3);
        vao.vertexAttribPointerF(2, 2, ATTR_SIZE, 6);

        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vao;
    }

}
