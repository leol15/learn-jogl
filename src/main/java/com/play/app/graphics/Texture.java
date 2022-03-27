
package com.play.app.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int id;
    private int width, height;

    public Texture() {
        id = glGenTextures();
    }

    public Texture(String path) {
        this();
        loadTexture(path);
    }

    public int getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void setParameteri(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public void texImage2D(int internalFormat, int width, int height,
            int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0,
            format, GL_UNSIGNED_BYTE, data);
    }

    public void detele() { glDeleteTextures(id); }

    private void loadTexture(String path) {
        ByteBuffer data;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            data = stbi_load(path, w, h, comp, 4);
            if (data == null) {
                System.out.println("Working Directory = " + System.getProperty("user.dir"));

                throw new RuntimeException("Cannot load texture file: " +
                    path + "\n" + stbi_failure_reason());
            }
            width = w.get();
            height = h.get();
        }

        bindTexture();
        setParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
            width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

}
