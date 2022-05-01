package com.play.app.utils;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

    public static FloatBuffer newMatBuffer() {
        return BufferUtils.createFloatBuffer(16);
    }
}
