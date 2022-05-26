package com.play.app.graphics;

import static org.lwjgl.opengl.GL45.*;

import java.util.*;

import com.play.app.scene.CameraUBO;
import com.play.app.scene.lights.LightUBO;

import lombok.extern.log4j.Log4j2;

/**
 * Singleton, since UBOs are singleton for shaders
 */
@Log4j2
public class UBO {

    private UBO() {}

    private static final UBO o = new UBO();

    public static UBO instance() {
        return o;
    }

    private Map<String, Integer> ubBindingMap = new HashMap<>();

    private Map<String, Integer> uboSizeCheck = new HashMap<>();

    // register UBOs before shaders are created
    public int createUboBuffer(String uniformBlockName) {
        log.info("creating UBO: {}", uniformBlockName);
        if (ubBindingMap.containsKey(uniformBlockName)) {
            log.warn("UBO exists for {}", uniformBlockName);
        }

        int nextBlockBindingIdx = ubBindingMap.size();
        ubBindingMap.put(uniformBlockName, nextBlockBindingIdx);

        int bufferIdx = glGenBuffers();
        glBindBufferBase(GL_UNIFORM_BUFFER, nextBlockBindingIdx, bufferIdx);

        // these seems not needed
        glBindBuffer(GL_UNIFORM_BUFFER, bufferIdx);
        glBufferData(bufferIdx, 0, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        return bufferIdx;
    }

    public void configureShader(ShaderProgram sp) {
        // hack: these needs to be init'ed
        LightUBO.getInstance();
        CameraUBO.getInstance();

        final int shaderId = sp.getId();
        for (final String name : ubBindingMap.keySet()) {
            final int uboIdx = glGetUniformBlockIndex(shaderId, name);
            if (uboIdx != -1) {
                glUniformBlockBinding(shaderId, uboIdx, ubBindingMap.get(name));

                final int blockSize = glGetActiveUniformBlocki(shaderId, uboIdx, GL_UNIFORM_BLOCK_DATA_SIZE);
                if (!uboSizeCheck.containsKey(name)) {
                    uboSizeCheck.put(name, blockSize);
                }
                if (uboSizeCheck.get(name) != blockSize) {
                    log.warn("block size differ for UBO {} [{} vs {}]", name, uboSizeCheck.get(name), blockSize);
                }
                log.debug("UBO size {} {}", name, uboSizeCheck.get(name));
            }
        }
    }

}
