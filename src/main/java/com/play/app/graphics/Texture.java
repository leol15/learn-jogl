
package com.play.app.graphics;

import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int id;
    private int width, height;

    public Texture() {
        id = glGenTextures();
    }
    public Texture(int width, int height) {
        id = glGenTextures();
        this.width = width;
        this.height = height;
    }

    public Texture(String path) {
        this();
        loadTexture(path);
    }

    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width  Width of the texture
     * @param height Height of the texture
     * @param data   Picture Data in RGBA format
     *
     * @return Texture from the specified data
     */
    public static Texture createTexture(int width, int height, ByteBuffer data) {
        Texture texture = new Texture(width, height);

        texture.bindTexture();

        texture.setParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 
            width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        texture.unbindTexture();
        return texture;
    }

    
    // getters
    public int getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, id);
    }    
    
    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
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
        
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        data = stbi_load(path, w, h, comp, 4);
        if (data == null) {
            throw new RuntimeException("Cannot load texture file: " +
                path + "\n" + stbi_failure_reason());
        }
        width = w.get();
        height = h.get();

        bindTexture();
        setParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
            width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

}
