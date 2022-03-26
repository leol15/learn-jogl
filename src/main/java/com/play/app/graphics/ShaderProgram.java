package com.play.app.graphics;

import org.lwjgl.*;
import java.nio.*;
import static org.lwjgl.opengl.GL30.*;

import com.play.app.utils.*;

public class ShaderProgram {

    private int id;

    public ShaderProgram() {
        id = glCreateProgram();
        if (id == 0) {
            System.err.println("Error creating shader program");
        }
    }

    public int getId() {
        return id;
    }

    public void loadShaderFromPath(String path, int type) {
        System.out.println("loading shader from path: " + path);

        int shaderId = glCreateShader(type);
        String source = AssetTools.loadTextFile(path);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(shaderId));
        }
        glAttachShader(id, shaderId);
        glDeleteShader(shaderId);
    }

    public void linkProgram() {
        // default out variable name in fragment shader, hack, TODO
        glBindFragDataLocation(id, 0, "fragColor");

        glLinkProgram(id);
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(id));
        }
    }

    public void useProgram() {
        glUseProgram(id);
    }

    public void setVertexAttribPointer(String attributeName, int size, int stride, int offset) {
        int attributeId = glGetAttribLocation(id, attributeName);
        glEnableVertexAttribArray(attributeId);
        glVertexAttribPointer(attributeId, size, GL_FLOAT, false, stride, offset);
    }
    
    // sets a uniform mat4 in the shader
    public void uniformMatrix4fv(String uniformName, FloatBuffer buffer) {
        int uniformId = glGetUniformLocation(id, uniformName);
        glUniformMatrix4fv(uniformId, false, buffer);
    }


}