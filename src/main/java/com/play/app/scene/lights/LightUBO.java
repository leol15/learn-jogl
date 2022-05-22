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
    private static final int NUM_LIGHTS = 1;

    // layout
    private final int BUFFER_SIZE = struct_point_light.SIZE + struct_dir_light.SIZE; // TODO
    private final int lightBufferObject;
    private final ByteBuffer buffer;
    private final LightSceneVisitor visitor;
    // collected lights
    private int numPointLights;
    private final List<struct_point_light> pointLights = new ArrayList<>(NUM_LIGHTS);

    private int numDirectionalLights;
    private final List<struct_dir_light> directionalLights = new ArrayList<>(NUM_LIGHTS);

    private LightUBO() {
        lightBufferObject = UBO.instance().createUboBuffer(CONST.UBO_LIGHTS);
        visitor = new LightSceneVisitor();
        buffer = BufferUtils.createByteBuffer(BUFFER_SIZE);
        for (int i = 0; i < NUM_LIGHTS; i++) {
            pointLights.add(new struct_point_light());
            directionalLights.add(new struct_dir_light());
        }
        numPointLights = 0;
        numDirectionalLights = 0;
    }

    public static LightUBO getInstance() {
        return instance;
    }

    public void addAllLights(SceneNode root) {
        root.accept(visitor);
        // add collected lights
        for (int i = 0; i < pointLights.size(); i++) {
            final struct_point_light l = pointLights.get(i);
            final int bufOffset = i * struct_point_light.SIZE;
            // log.debug("adding point light to buffer {}, {}", i, l.intensity);
            l.position.get(bufOffset, buffer);
            l.intensity.get(bufOffset + CONST.SIZE_VEC3 * 1, buffer);
            l.attenuation.get(bufOffset + CONST.SIZE_VEC3 * 2, buffer);
        }
        for (int i = 0; i < directionalLights.size(); i++) {
            final struct_dir_light l = directionalLights.get(i);
            final int bufOffset = pointLights.size() * struct_point_light.SIZE + i * struct_dir_light.SIZE;
            l.direction.get(bufOffset, buffer);
            l.intensity.get(bufOffset + CONST.SIZE_VEC3, buffer);
        }
        // send to shader
        glBindBuffer(GL_UNIFORM_BUFFER, lightBufferObject);
        glBufferData(GL_UNIFORM_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        // reset
        numPointLights = 0;
        numDirectionalLights = 0;
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

    public void accepDirectionalLight(DirectionalLight d, Matrix4f worldTransform) {
        if (numDirectionalLights >= directionalLights.size()) {
            log.warn("max number of directional light exceeded: {}", numDirectionalLights);
            return;
        }
        final struct_dir_light struct = directionalLights.get(numDirectionalLights++);
        struct.direction.set(d.getDirection()).mul(worldTransform).normalize();
        struct.intensity.set(d.color);
    }

    // these mirrors the light objects used in shaders
    private class struct_point_light {
        public static final int SIZE = CONST.SIZE_VEC4 * 3;

        Vector4f position = new Vector4f();
        Vector4f intensity = new Vector4f();
        Vector4f attenuation = new Vector4f();
    }

    private class struct_dir_light {
        public static final int SIZE = CONST.SIZE_VEC4 * 2;

        Vector4f direction = new Vector4f();
        Vector4f intensity = new Vector4f();
    }

}
