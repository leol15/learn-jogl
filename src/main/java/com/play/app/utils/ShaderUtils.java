package com.play.app.utils;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.graphics.ShaderProgram;

import lombok.extern.log4j.Log4j2;

/**
 * All shaders come from here
 */
@Log4j2
public class ShaderUtils {
    private static final Map<String, ShaderProgram> SHADERS = new HashMap<>();
    private static final String SHADER_HELPER_SOURCE = AssetTools.loadTextFile(CONST.SHADER_HELPER_FILE);

    public static ShaderProgram getShader(String fileName) {
        if (SHADERS.containsKey(fileName)) {
            return SHADERS.get(fileName);
        }

        // create it then
        final String pathPrefix;
        if (fileExists(String.format("%s%s.vert", CONST.SHADER_DEFAULT_FOLDER, fileName))) {
            pathPrefix = CONST.SHADER_DEFAULT_FOLDER;
        } else {
            pathPrefix = CONST.SHADER_FOLDER;
        }

        final String vert = String.format("%s%s.vert", pathPrefix, fileName);
        final String geom = String.format("%s%s.geom", pathPrefix, fileName);
        final String frag = String.format("%s%s.frag", pathPrefix, fileName);

        if (!fileExists(vert)) {
            log.warn("Cannot file shader file {}", vert);
            return null;
        }

        final ShaderProgram shader = new ShaderProgram();
        if (fileExists(vert)) {
            loadShaderFile(shader, vert);
        }
        if (fileExists(geom)) {
            loadShaderFile(shader, geom);
        }

        if (fileExists(frag)) {
            loadShaderFile(shader, frag);
        } else {
            loadShaderFile(shader, CONST.DEFAULT_FRAG_SHADER_PATH);
        }
        shader.linkProgram();

        SHADERS.put(fileName, shader);
        return shader;
    }

    public static String getShaderName(ShaderProgram shader) {
        for (final String k : SHADERS.keySet()) {
            if (SHADERS.get(k).equals(shader)) {
                return k;
            }
        }
        log.warn("Shader name not found, shader is not managed by this: {}", shader);
        return null;
    }

    private static boolean fileExists(String path) {
        final File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    private static void loadShaderFile(ShaderProgram shaderProgram, String path) {
        if (path.endsWith(".frag")) {
            loadShaderFile(shaderProgram, path, GL_FRAGMENT_SHADER);
        } else if (path.endsWith(".geom")) {
            loadShaderFile(shaderProgram, path, GL_GEOMETRY_SHADER);
        } else if (path.endsWith(".vert")) {
            loadShaderFile(shaderProgram, path, GL_VERTEX_SHADER);
        } else {
            log.warn("Unsupported shader file {}", path);
        }
    }

    private static void loadShaderFile(ShaderProgram shaderProgram, String path, int type) {
        log.info("loading shader from path: {}", path);

        final String source = AssetTools.loadTextFile(path);
        if (source == null) {
            return;
        }
        final int versionIdx = source.indexOf("#version");
        final int nextLine = source.indexOf("\n", versionIdx);
        final StringBuilder appendedSource = new StringBuilder(source);
        appendedSource.replace(versionIdx, nextLine, SHADER_HELPER_SOURCE);

        shaderProgram.attachShader(appendedSource.toString(), type);
    }

    // save just the name to file
    public static void save(ShaderProgram shader, YAMLGenerator generator) throws IOException {
        generator.writeString(getShaderName(shader));
    }
}
