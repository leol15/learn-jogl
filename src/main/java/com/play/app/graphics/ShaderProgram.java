package com.play.app.graphics;

import org.joml.*;

import lombok.extern.log4j.Log4j2;

import java.nio.*;
import static org.lwjgl.opengl.GL45.*;

import com.play.app.utils.*;

@Log4j2
public class ShaderProgram {

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

    public ShaderProgram withShader(String path, int type) {
        loadShaderFromPath(path, type);
        return this;
    }

    public ShaderProgram withShader(String path) {
        if (path.endsWith(".frag")) {
            return withShader(path, GL_FRAGMENT_SHADER);
        } else if (path.endsWith(".geom")) {
            return withShader(path, GL_GEOMETRY_SHADER);
        } else if (path.endsWith(".vert")) {
            return withShader(path, GL_VERTEX_SHADER);
        } else {
            log.warn("Unsupported shader file {}", path);
            return this;
        }
    }

    @Deprecated
    public void loadShaderFromPath(String path, int type) {
        log.info("loading shader from path: {}", path);

        int shaderId = glCreateShader(type);
        String source = AssetTools.loadTextFile(path);
        if (source == null) {
            glDeleteShader(shaderId);
            return;
        }

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(shaderId));
        }

        glAttachShader(id, shaderId);
        glDeleteShader(shaderId);
    }

    public ShaderProgram linkProgram() {
        // default out variable name in fragment shader, hack, TODO
        glBindFragDataLocation(id, 0, "fragColor");

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

    // note! Need a valid VAO to be bound
    // deprecated, wrong, should `glVertexAttribPointer` directly to a VAO
    public void setVertexAttribPointer(String attributeName, int size, int stride, int offset) {
        int attributeId = glGetAttribLocation(id, attributeName);
        glEnableVertexAttribArray(attributeId);
        glVertexAttribPointer(attributeId, size, GL_FLOAT, false, stride, offset);
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

    // sets the location of texture
    public void uniform(String uniformName, int v) {
        useProgram();
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniform1i(uniformId, v);
        unuseProgram();
    }

}