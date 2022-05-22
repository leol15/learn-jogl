package com.play.app.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;
import java.util.*;

import com.play.app.utils.CONST;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

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
        // 6 faces, each with 4 points, 2 triangles
        final VAOHelper vHelper = new VAOHelper(6 * 4, 6 * 2 * 3);

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

        vHelper.addRect(A, B, C, D, new Vector3f(0, 0, -1));
        vHelper.addRect(E, F, B, A, new Vector3f(-1, 0, 0));
        vHelper.addRect(H, G, F, E, new Vector3f(0, 0, 1));
        vHelper.addRect(D, C, G, H, new Vector3f(1, 0, 0));
        vHelper.addRect(B, F, G, C, new Vector3f(0, 1, 0));
        vHelper.addRect(A, E, H, D, new Vector3f(0, -1, 0));

        vHelper.done();

        vHelper.modifyingVAO.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, 6 * 2 * 3, GL_UNSIGNED_INT, 0));

        return vHelper.modifyingVAO;
    }

    private static VAO createPlane() {
        VAOHelper vHelper = new VAOHelper(4, 6);

        // 13
        // 02
        for (int i : new int[] { 0, 1 }) {
            for (int j : new int[] { 0, 1 }) {
                vHelper.addPosition(i, j, 0);
                vHelper.addNormals(0, 0, 1);
                vHelper.addUV(i, j);
            }
        }

        vHelper.addElements(0, 1, 3);
        vHelper.addElements(0, 3, 2);

        vHelper.done();

        vHelper.modifyingVAO.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, 2 * 3, GL_UNSIGNED_INT, 0));
        return vHelper.modifyingVAO;
    }

    private static VAO createCircle(final int numSections) {
        final VAOHelper vHelper = new VAOHelper(numSections, 0);
        for (int i = 0; i < numSections; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numSections);
            float y = (float) Math.cos(i * Math.PI * 2 / numSections);
            vHelper.positions.put(x).put(y).put(0);
        }

        vHelper.done();
        vHelper.modifyingVAO.disableVertexAttribArray(CONST.VERT_IN_NORMAL);
        vHelper.modifyingVAO.disableVertexAttribArray(CONST.VERT_IN_UV);
        vHelper.modifyingVAO.setDrawFunction(() -> glDrawArrays(GL_LINE_LOOP, 0, numSections));

        return vHelper.modifyingVAO;
    }

    private static VAO createCone(final int numSections) {
        // TODO add uv
        final int numCircleFragment = numSections;
        // double the circle since the normal is different
        final int numTriangles = numCircleFragment * 2;
        final int numVerticies = numCircleFragment * 2 + 2;

        final VAOHelper vHelper = new VAOHelper(numVerticies + 2, numCircleFragment * 2 * 3);

        // the tip of cone: (0, 1, 0)
        // vertices.put(0).put(1).put(0).put(0).put(1).put(0).put(0.5f).put(1);
        vHelper.addPosition(0, 1, 0);
        vHelper.addNormals(0, 1, 0);
        vHelper.addUV(0.5f, 1);

        // the side circle
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vHelper.addPosition(x, 0, y);
            vHelper.addNormals(x, 0, y);
            vHelper.addUV((float) i / numCircleFragment, 0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                vHelper.addElements(i + 1, 0, i + 2);
            }
        }

        // the base center point
        vHelper.addPosition(0, 0, 0);
        vHelper.addNormals(0, -1, 0);
        vHelper.addUV(0, 0);

        final int baseIdx = vHelper.positions.position() / 3 - 1;
        // the bottom circle
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vHelper.addPosition(x, 0, y);
            vHelper.addNormals(0, -1, 0);
            vHelper.addUV((float) i / numCircleFragment, 0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                vHelper.addElements(baseIdx + i + 1, baseIdx, baseIdx + i + 2);
            }
        }

        vHelper.done();

        vHelper.modifyingVAO.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vHelper.modifyingVAO;
    }

    private static VAO createCyclinder(final int numSections) {
        // TODO add uv
        final int numCircleFragment = numSections;
        // double the circle since the normal is different
        final int numTriangles = numCircleFragment * 4;
        final int numVerticies = numCircleFragment * 4 + 2;

        // +4 to make [start, end] meet
        final VAOHelper vHelper = new VAOHelper(numVerticies + 4, numCircleFragment * 4 * 3);

        int baseIdx;

        // the top circle
        vHelper.addPosition(0, 1, 0);
        vHelper.addNormals(0, 1, 0);
        vHelper.addUV(1, 1);

        // TODO change to 1
        baseIdx = vHelper.positions.position() / 3 - 1;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vHelper.addPosition(x, 1, y);
            vHelper.addNormals(0, 1, 0);
            vHelper.addUV((float) i / numCircleFragment, 0);

            // last point does not have next triangle
            if (i != numCircleFragment) {
                vHelper.addElements(baseIdx + i + 1, baseIdx, baseIdx + i + 2);
            }
        }

        // the base circle
        vHelper.addPosition(0, 0, 0);
        vHelper.addNormals(0, -1, 0);
        vHelper.addUV(0, 0);
        baseIdx = vHelper.positions.position() / 3 - 1;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            vHelper.addPosition(x, 0, y);
            vHelper.addNormals(0, -1, 0);
            vHelper.addUV((float) i / numCircleFragment, 0);
            // last point does not have next triangle
            if (i != numCircleFragment) {
                vHelper.addElements(baseIdx + i + 1, baseIdx, baseIdx + i + 2);
            }
        }

        // the side
        baseIdx = vHelper.positions.position() / 3;
        for (int i = 0; i < numCircleFragment + 1; i++) {
            float x = (float) Math.sin(i * Math.PI * 2 / numCircleFragment) / 2;
            float y = (float) Math.cos(i * Math.PI * 2 / numCircleFragment) / 2;
            // bottom
            vHelper.addPosition(x, 0, y);
            vHelper.addNormals(x * 2, 0, y * 2);
            vHelper.addUV((float) i / numCircleFragment, 0);
            // top
            vHelper.addPosition(x, 1, y);
            vHelper.addNormals(x * 2, 0, y * 2);
            vHelper.addUV((float) i / numCircleFragment, 0);

            // last point does not have next triangle
            if (i != numCircleFragment) {
                // 24
                // 13
                int triangleBase = baseIdx + i * 2;
                vHelper.addElements(triangleBase, triangleBase + 1, triangleBase + 3);
                vHelper.addElements(triangleBase, triangleBase + 3, triangleBase + 2);
            }
        }

        vHelper.done();
        vHelper.modifyingVAO.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vHelper.modifyingVAO;
    }

    private static VAO createSphere(final int numSlices, final int numLevels) {
        // layout:: ring major
        // TODO add uv
        // double the circle since the normal is different
        final int numTriangles = numSlices * 2 + (numLevels - 2) * 2 * numSlices;
        final int numVerticies = 2 + (numLevels - 1) * (numSlices + 1);

        // +4 to make [start, end] meet
        final VAOHelper vHelper = new VAOHelper(numVerticies, numTriangles * 3);

        int baseIdx;

        final Vector3f tmpV = new Vector3f();

        final float sliceAngle = (float) Math.PI / numSlices;
        float currSliceAngle = sliceAngle;
        float levelRadius = (float) Math.sin(currSliceAngle) / 2;
        float levelHeight = (float) Math.cos(currSliceAngle) / 2;

        // the top level
        vHelper.addPosition(0, 0.5f, 0);
        vHelper.addNormals(0, 1, 0);
        vHelper.addUV(1, 1);
        baseIdx = vHelper.positions.position() / 3 - 1;
        for (int i = 0; i < numSlices + 1; i++) {
            final float x = (float) Math.sin(i * Math.PI * 2 / numSlices) * levelRadius;
            final float y = (float) Math.cos(i * Math.PI * 2 / numSlices) * levelRadius;
            tmpV.set(x, levelHeight, y);
            vHelper.addVertexOnSphere(tmpV, (float) i / numSlices);
            // last point does not have next triangle
            if (i != numSlices) {
                vHelper.addElements(baseIdx + i + 1, baseIdx, baseIdx + i + 2);
            }
        }

        // the middle layers, previous layer is writter
        for (int level = 0; level < numLevels - 2; level++) {
            currSliceAngle += sliceAngle;
            levelRadius = (float) Math.sin(currSliceAngle) / 2;
            levelHeight = (float) Math.cos(currSliceAngle) / 2;
            baseIdx = vHelper.positions.position() / 3;
            for (int i = 0; i < numSlices + 1; i++) {
                final float x = (float) Math.sin(i * Math.PI * 2 / numSlices) * levelRadius;
                final float y = (float) Math.cos(i * Math.PI * 2 / numSlices) * levelRadius;
                tmpV.set(x, levelHeight, y);
                vHelper.addVertexOnSphere(tmpV, (float) i / numSlices);
                // last point does not have next triangle
                if (i != numSlices) {
                    vHelper.addElements(baseIdx + i,
                            baseIdx + i - (numSlices + 1),
                            baseIdx + i - (numSlices + 1) + 1);
                    vHelper.addElements(baseIdx + i,
                            baseIdx + i - (numSlices + 1) + 1,
                            baseIdx + i + 1);
                }
            }
        }

        // the bottom level, previous level is written
        currSliceAngle = (float) Math.PI - sliceAngle;
        levelRadius = (float) Math.sin(currSliceAngle) / 2;
        levelHeight = (float) Math.cos(currSliceAngle) / 2;
        vHelper.addPosition(0, -0.5f, 0);
        vHelper.addNormals(0, -1, 0);
        vHelper.addUV(1, 1);
        baseIdx = vHelper.positions.position() / 3 - 1;
        for (int i = 0; i < numSlices + 1; i++) {
            // last point does not have next triangle
            if (i != numSlices) {
                vHelper.addElements(baseIdx - i - 1, baseIdx, baseIdx - i - 2);
            }
        }

        vHelper.done();

        vHelper.modifyingVAO.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, numTriangles * 3, GL_UNSIGNED_INT, 0));

        return vHelper.modifyingVAO;
    }

    public static class VAOHelper {

        public final FloatBuffer positions, normals, uvs;
        public final IntBuffer elements;

        public final VAO modifyingVAO;

        public VAOHelper(int numVerticies, int numIndicies) {
            positions = BufferUtils.createFloatBuffer(numVerticies * 3);
            normals = BufferUtils.createFloatBuffer(numVerticies * 3);
            uvs = BufferUtils.createFloatBuffer(numVerticies * 2);
            elements = BufferUtils.createIntBuffer(numIndicies);
            modifyingVAO = new VAO();
        }

        public void done() {
            positions.flip();
            normals.flip();
            uvs.flip();
            elements.flip();

            modifyingVAO.bufferData(CONST.VERT_IN_POSITION, positions);
            modifyingVAO.bufferData(CONST.VERT_IN_NORMAL, normals);
            modifyingVAO.bufferData(CONST.VERT_IN_UV, uvs);
            modifyingVAO.bufferIndices(elements);
        }

        public void addPosition(float x, float y, float z) {
            positions.put(x).put(y).put(z);
        }

        public void addNormals(float x, float y, float z) {
            normals.put(x).put(y).put(z);
        }

        public void addUV(float u, float v) {
            uvs.put(u).put(v);
        }

        public void addElements(int a, int b, int c) {
            elements.put(a).put(b).put(c);
        }

        private void addRect(final Vector3f a, final Vector3f b, final Vector3f c, final Vector3f d,
                final Vector3f normal) {

            int idx = positions.position() / 3;
            positions.put(a.x).put(a.y).put(a.z);
            normals.put(normal.x).put(normal.y).put(normal.z);
            uvs.put(0).put(0);

            positions.put(b.x).put(b.y).put(b.z);
            normals.put(normal.x).put(normal.y).put(normal.z);
            uvs.put(0).put(1);

            positions.put(c.x).put(c.y).put(c.z);
            normals.put(normal.x).put(normal.y).put(normal.z);
            uvs.put(1).put(1);

            positions.put(d.x).put(d.y).put(d.z);
            normals.put(normal.x).put(normal.y).put(normal.z);
            uvs.put(1).put(0);

            elements.put(idx).put(idx + 1).put(idx + 2);
            elements.put(idx).put(idx + 2).put(idx + 3);
        }

        private void addVertexOnSphere(final Vector3f tmpV, final float sliceFraction) {
            positions.put(tmpV.x).put(tmpV.y).put(tmpV.z);
            tmpV.normalize();
            normals.put(tmpV.x).put(tmpV.y).put(tmpV.z);
            // uv
            uvs.put(sliceFraction).put(tmpV.y);
        }

    }
}
