
package com.play.app.graphics;

import org.lwjgl.*;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.*;

import com.fasterxml.jackson.dataformat.yaml.*;
import com.play.app.basics.*;
import com.play.app.utils.WorldSerializer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

@Log4j2
public class Texture implements Savable {

    private int id;
    private int width, height;
    private String textureFilePath;

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
        textureFilePath = path;
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

        texture.setParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        texture.texImage2D(GL_RGBA8, width, height, GL_RGBA, data);

        return texture;
    }

    // getters
    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setParameteri(int name, int value) {
        bindTexture();
        glTexParameteri(GL_TEXTURE_2D, name, value);
        unbindTexture();
    }

    public void texImage2D(int internalFormat, int width, int height,
            int format, ByteBuffer data) {
        bindTexture();
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0,
                format, GL_UNSIGNED_BYTE, data);
        unbindTexture();
    }

    public void detele() {
        glDeleteTextures(id);
    }

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

        setParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        texImage2D(GL_RGBA8, width, height, GL_RGBA, data);

        System.out.println("Loadeding texture from file: " + path + " [w " + width + ", h " + height + "]");
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeString(textureFilePath);
    }

    public static Texture create(WorldSerializer reader) throws IOException {
        final String path = reader.consumeString();
        if (path == null || path.equals("null")) {
            return null;
        } else {
            return new Texture(path);
        }
    }

}
