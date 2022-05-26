package com.play.app.utils;

import java.io.File;
import java.util.*;

import com.play.app.graphics.ShaderProgram;

import lombok.extern.log4j.Log4j2;

/**
 * All shaders come from here
 */
@Log4j2
public class ShaderUtils {
    private static final Map<String, ShaderProgram> SHADERS = new HashMap<>();

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
            shader.withShader(vert);
        }
        if (fileExists(geom)) {
            shader.withShader(geom);
        }

        if (fileExists(frag)) {
            shader.withShader(frag);
        }
        shader.linkProgram();

        SHADERS.put(fileName, shader);
        return shader;
    }

    private static boolean fileExists(String path) {
        final File f = new File(path);
        return f.exists() && !f.isDirectory();
    }
}
