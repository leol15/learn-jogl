package com.play.app.graphics;

import static org.lwjgl.opengl.GL45.*;

import java.util.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UBO {

    private static UBO instance = new UBO();
    private static Map<String, Integer> ubos = new HashMap<>();

    private static Map<String, Integer> uboSizeCheck = new HashMap<>();

    public static int createUboBuffer(String uniformBlockName) {
        if (ubos.containsKey(uniformBlockName)) {
            log.warn("UBO exists for {}", uniformBlockName);
        }

        int nextBlockBindingIdx = ubos.size();
        ubos.put(uniformBlockName, nextBlockBindingIdx);

        int bufferIdx = glGenBuffers();
        glBindBufferBase(GL_UNIFORM_BUFFER, nextBlockBindingIdx, bufferIdx);

        // these seems not needed
        glBindBuffer(GL_UNIFORM_BUFFER, bufferIdx);
        glBufferData(bufferIdx, 0, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        return bufferIdx;
    }

    public static void configureShader(ShaderProgram sp) {
        final int shaderId = sp.getId();
        for (final String name : ubos.keySet()) {
            final int uboIdx = glGetUniformBlockIndex(shaderId, name);
            if (uboIdx != -1) {
                glUniformBlockBinding(shaderId, uboIdx, ubos.get(name));

                final int blockSize = glGetActiveUniformBlocki(shaderId, 0, GL_UNIFORM_BLOCK_DATA_SIZE);
                if (!uboSizeCheck.containsKey(name)) {
                    uboSizeCheck.put(name, blockSize);
                }
                if (uboSizeCheck.get(name) != blockSize) {
                    log.debug("block size differ for UBO {} [{} vs {}]", name, uboSizeCheck.get(name), blockSize);
                }
            }
        }
    }

}
