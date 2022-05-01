package com.play.app.geometry;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.VAO;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

/**
 * Represent a drawable cube
 */
public class Plane extends APhysicsObject {

    public Plane() {

    }

}
