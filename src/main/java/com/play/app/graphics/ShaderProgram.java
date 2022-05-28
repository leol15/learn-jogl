package com.play.app.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.play.app.basics.*;
import com.play.app.utils.*;

import org.joml.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ShaderProgram implements Savable {

    private int id;
    private final FloatBuffer tmpMatrixBuffer = Func.newMatBuffer();

    public ShaderProgram() {
        id = glCreateProgram();
        if (id == 0) {
            log.error("Error creating shader program");
        }
    }

    public int getId() {
        return id;
    }

    public ShaderProgram attachShader(String source, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(shaderId));
        }

        glAttachShader(id, shaderId);
        glDeleteShader(shaderId);
        return this;
    }

    public ShaderProgram linkProgram() {
        // set the out variable name in all fragment shader
        glBindFragDataLocation(id, 0, CONST.FRAG_OUTPUT_NAME);

        glLinkProgram(id);
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(id));
        }

        // setup UBOs
        UBO.instance().configureShader(this);
        return this;
    }

    public void useProgram() {
        glUseProgram(id);
    }

    public void unuseProgram() {
        glUseProgram(0);
    }

    // sets a uniform mat4 in the shader
    public void uniformMatrix4fv(String uniformName, FloatBuffer buffer) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniformMatrix4fv(uniformId, false, buffer);
        unuseProgram();
    }

    public void uniformMatrix4fv(String uniformName, final Matrix4f mat) {
        mat.get(tmpMatrixBuffer);
        uniformMatrix4fv(uniformName, tmpMatrixBuffer);
    }

    // sets the location of texture
    public void uniform(String uniformName, int v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform1i(uniformId, v);
        unuseProgram();
    }

    public void uniformf(String uniformName, float v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform1f(uniformId, v);
        unuseProgram();
    }

    public void uniform2f(String uniformName, Vector2f v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform2f(uniformId, v.x, v.y);
        unuseProgram();
    }

    public void uniform3f(String uniformName, Vector3f v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform3f(uniformId, v.x, v.y, v.z);
        unuseProgram();
    }

    public void uniform4f(String uniformName, Vector4f v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform4f(uniformId, v.x, v.y, v.z, v.w);
        unuseProgram();
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        // a bit weird, should save the shader path info, instead of the shader shource code
        ShaderUtils.save(this, writer);
    }

    public static ShaderProgram create(WorldSerializer reader) throws IOException {
        return ShaderUtils.load(reader);
    }

}