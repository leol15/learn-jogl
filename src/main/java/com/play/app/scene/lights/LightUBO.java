package com.play.app.scene.lights;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.ByteBuffer;
import java.util.*;

import com.play.app.graphics.UBO;
import com.play.app.scene.SceneNode;
import com.play.app.utils.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

import lombok.extern.log4j.Log4j2;

/**
 * A singleton, manages a UBO for all the kinds of lights
 */
@Log4j2
public class LightUBO {
    private static final LightUBO instance = new LightUBO();

    // layout
    private final int BUFFER_SIZE = CONST.SIZE_VEC4 * 3; // TODO
    private final int lightBufferObject;
    private final ByteBuffer buffer;
    private final LightSceneVisitor visitor;
    // collected lights
    private int numPointLights;
    private final List<struct_point_light> pointLights = new ArrayList<>();

    private LightUBO() {
        lightBufferObject = UBO.instance().createUboBuffer(CONST.UBO_LIGHTS);
        visitor = new LightSceneVisitor();
        buffer = BufferUtils.createByteBuffer(BUFFER_SIZE);
        numPointLights = 0;
        for (int i = 0; i < 1; i++) {
            // add point light
            pointLights.add(new struct_point_light());
        }
    }

    public static LightUBO instance() {
        return instance;
    }

    public void addAllLights(SceneNode root) {
        root.accept(visitor);
        // add collected lights
        for (int i = 0; i < pointLights.size(); i++) {
            final struct_point_light l = pointLights.get(i);
            final int bufOffset = i * l.size();
            // log.debug("adding point light to buffer {}, {}", i, l.intensity);
            l.position.get(bufOffset, buffer);
            l.intensity.get(bufOffset + CONST.SIZE_VEC3 * 1, buffer);
            l.attenuation.get(bufOffset + CONST.SIZE_VEC3 * 2, buffer);
        }
        // send to shader
        glBindBuffer(GL_UNIFORM_BUFFER, lightBufferObject);
        glBufferData(GL_UNIFORM_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        // reset
        numPointLights = 0;
    }

    public void acceptPointLight(PointLight p, Matrix4f worldTransform) {
        if (numPointLights >= pointLights.size()) {
            log.warn("max number of point light exceeded: {}", numPointLights);
            return;
        }
        final struct_point_light struct = pointLights.get(numPointLights++);
        struct.position.set(0, 0, 0, 1).mul(worldTransform);
        struct.intensity.set(p.color);
        struct.attenuation.set(p.attenuation.x, p.attenuation.y, p.attenuation.z, 1);
    }

    private class struct_point_light {
        public int size() {
            return CONST.SIZE_VEC4 * 3;
        }

        Vector4f position = new Vector4f();
        Vector4f intensity = new Vector4f();
        Vector4f attenuation = new Vector4f();
    }

}