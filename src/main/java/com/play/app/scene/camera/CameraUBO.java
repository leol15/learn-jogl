package com.play.app.scene.camera;

import java.nio.ByteBuffer;

import com.play.app.graphics.UBO;
import com.play.app.utils.CONST;

import static org.lwjgl.opengl.GL45.*;
import org.joml.*;
import org.lwjgl.BufferUtils;

import lombok.Getter;

/**
 * Singleton UBO for camera state
 */
public class CameraUBO {
    @Getter
    private static CameraUBO instance = new CameraUBO();
    // camera info is passed to shaders via UBO
    private final int viewProjectionUbo;
    private final int UBO_SIZE = 2 * CONST.SIZE_MAT4 + 2 * CONST.SIZE_VEC3;
    private final ByteBuffer uboBuffer = BufferUtils.createByteBuffer(UBO_SIZE);

    private CameraUBO() {
        viewProjectionUbo = UBO.instance().createUboBuffer(CONST.UBO_CAMERA_INFO);
    }

    public void setData(Matrix4f view, Matrix4f projection, Vector3f cameraPosition, Vector3f ambientColor) {
        view.get(uboBuffer);
        projection.get(CONST.SIZE_MAT4, uboBuffer);
        cameraPosition.get(2 * CONST.SIZE_MAT4, uboBuffer);
        ambientColor.get(2 * CONST.SIZE_MAT4 + CONST.SIZE_VEC3, uboBuffer);

        glBindBuffer(GL_UNIFORM_BUFFER, viewProjectionUbo);
        glBufferData(GL_UNIFORM_BUFFER, uboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

}
