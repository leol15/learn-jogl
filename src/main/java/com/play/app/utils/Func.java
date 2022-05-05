package com.play.app.utils;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.glfw.GLFW.*;

public class Func {
    public static void p(final String s) {
        System.err.println(s);
    }

    public static int[] getWindowSize(long window) {
        int[] size = new int[2];
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, w, h);
        size[0] = w.get();
        size[1] = h.get();
        return size;
    }

    public static Vector4f toVec4(Color c) {
        return new Vector4f(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).div(255);
    }

    public static Vector3f toVec3(final Vector4f v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    public static Vector4f toVec4(final Vector3f v) {
        return new Vector4f(v, 1);
    }

    public static FloatBuffer newMatBuffer() {
        return BufferUtils.createFloatBuffer(16);
    }

    public static SpacialThing createLine(final Vector3f start, final Vector3f end, final float lineWidth) {
        final SpacialThing transform = new SpacialThing();
        final Vector3f diff = new Vector3f();
        start.sub(end, diff);
        // set transform to be diff, then translate by "end"
        transform.scale.set(lineWidth, diff.length(), lineWidth);
        transform.rotation.rotateTo(new Vector3f(0, 1, 0), diff);
        transform.translation.set(end);

        return transform;
    }

    public static SpacialThing createLine(final Ray ray, final float length, final float lineWidth) {
        final Vector3f end = new Vector3f();
        ray.direction.mul(length, end).add(ray.start);
        return createLine(ray.start, end, lineWidth);
    }
}
