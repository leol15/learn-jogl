package com.play.app.mesh;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.*;
import java.util.*;

import com.play.app.graphics.VAO;
import com.play.app.utils.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * A normal(regular) mesh, with position, normal, and triangle data,
 * and maybe UVs
 */
public class NormalMesh implements Mesh {
    @Getter
    final VAO vao;
    final List<Vector3f> positions = new ArrayList<>();
    final List<Vector3f> normals = new ArrayList<>();
    final List<Vector2f> uvs = new ArrayList<>();
    final List<Integer> triangles = new ArrayList<>();
    @Setter
    private int glPrimitiveType = GL_TRIANGLES;

    public NormalMesh() {
        vao = new VAO();
    }

    public int numPos() {
        return positions.size();
    }

    public void reset() {
        positions.clear();
        normals.clear();
        uvs.clear();
        triangles.clear();
    }

    @Override
    public void draw() {
        // assume data is uploaded to VAO already
        vao.bind();
        glDrawElements(glPrimitiveType, triangles.size(), GL_UNSIGNED_INT, 0);
        vao.unbind();
    }

    public void addPos(final Vector3f p) {
        positions.add(p);
    }

    public void addPos(float x, float y, float z) {
        addPos(new Vector3f(x, y, z));
    }

    public void addNormal(final Vector3f n) {
        normals.add(n);
    }

    public void addNormal(float x, float y, float z) {
        addNormal(new Vector3f(x, y, z));
    }

    public void addUV(final Vector2f uv) {
        uvs.add(uv);
    }

    public void addUV(float u, float v) {
        addUV(new Vector2f(u, v));
    }

    public void addTriangle(int a, int b, int c) {
        triangles.add(a);
        triangles.add(b);
        triangles.add(c);
    }

    public void addElement(int a) {
        triangles.add(a);
    }

    public void uploadData() {
        // TODO, optimize
        final FloatBuffer positionsBuf = BufferUtils.createFloatBuffer(3 * positions.size());
        final FloatBuffer normalsBuf = BufferUtils.createFloatBuffer(3 * normals.size());
        final FloatBuffer uvsBuf = BufferUtils.createFloatBuffer(3 * uvs.size());
        final IntBuffer trianglesBuf = BufferUtils.createIntBuffer(triangles.size());

        for (int i = 0; i < positions.size(); i++) {
            positionsBuf.put(positions.get(i).x);
            positionsBuf.put(positions.get(i).y);
            positionsBuf.put(positions.get(i).z);
        }
        for (int i = 0; i < normals.size(); i++) {
            normalsBuf.put(normals.get(i).x);
            normalsBuf.put(normals.get(i).y);
            normalsBuf.put(normals.get(i).z);
        }
        for (int i = 0; i < uvs.size(); i++) {
            uvsBuf.put(uvs.get(i).x);
            uvsBuf.put(uvs.get(i).y);
        }
        for (int i = 0; i < triangles.size(); i++) {
            trianglesBuf.put(triangles.get(i));
        }

        positionsBuf.flip();
        normalsBuf.flip();
        uvsBuf.flip();
        trianglesBuf.flip();
        vao.bufferData(CONST.VERT_IN_POSITION, positionsBuf);
        vao.bufferData(CONST.VERT_IN_NORMAL, normalsBuf);
        vao.bufferData(CONST.VERT_IN_UV, uvsBuf);
        vao.bufferIndices(trianglesBuf);
    }

    @Override
    public void destroy() {
        vao.destroy();
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        // TODO save to a file with a random name?
    }
}
