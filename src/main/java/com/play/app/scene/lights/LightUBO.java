package com.play.app.scene.lights;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.ByteBuffer;

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
    private final int lightBufferObject;
    private final ByteBuffer buffer;
    private final int BUFFER_SIZE = NUM_LIGHTS * (struct_point_light.SIZE +
            struct_dir_light.SIZE + struct_spot_light.SIZE) + CONST.SIZE_VEC4;
    private final Vector4f numActiveLights = new Vector4f();

    private final LightSceneVisitor visitor;

    // collected lights
    private int numPointLight = 0;
    private final struct_point_light[] pointLights = new struct_point_light[NUM_LIGHTS];

    private int numDirectionalLight = 0;
    private final struct_dir_light[] directionalLights = new struct_dir_light[NUM_LIGHTS];

    private int numSpotLight = 0;
    private final struct_spot_light[] spotLights = new struct_spot_light[NUM_LIGHTS];

    private LightUBO() {
        lightBufferObject = UBO.instance().createUboBuffer(CONST.UBO_LIGHTS);
        visitor = new LightSceneVisitor();
        buffer = BufferUtils.createByteBuffer(BUFFER_SIZE);
        for (int i = 0; i < NUM_LIGHTS; i++) {
            pointLights[i] = new struct_point_light();
            directionalLights[i] = new struct_dir_light();
            spotLights[i] = new struct_spot_light();
        }
    }

    public static LightUBO getInstance() {
        return instance;
    }

    public void addAllLights(SceneNode root) {
        // collect lights
        root.accept(visitor);

        // add collected lights
        int baseOffset = 0;
        for (int i = 0; i < numPointLight; i++) {
            pointLights[i].get(baseOffset + i * struct_point_light.SIZE, buffer);
        }
        baseOffset += pointLights.length * struct_point_light.SIZE;

        for (int i = 0; i < numDirectionalLight; i++) {
            directionalLights[i].get(baseOffset + i * struct_dir_light.SIZE, buffer);
        }
        baseOffset += directionalLights.length * struct_dir_light.SIZE;

        for (int i = 0; i < numSpotLight; i++) {
            spotLights[i].get(baseOffset + i * struct_spot_light.SIZE, buffer);
        }
        baseOffset += spotLights.length * struct_spot_light.SIZE;

        // write num lights
        numActiveLights.set(numPointLight, numDirectionalLight, numSpotLight, 0);
        numActiveLights.get(baseOffset, buffer);

        // send to shader
        glBindBuffer(GL_UNIFORM_BUFFER, lightBufferObject);
        glBufferData(GL_UNIFORM_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        // reset
        numPointLight = 0;
        numDirectionalLight = 0;
        numSpotLight = 0;
    }

    public void acceptPointLight(PointLight p, Matrix4f worldTransform) {
        if (numPointLight >= pointLights.length) {
            log.warn("max number of point light exceeded: {}", numPointLight);
            return;
        }
        final struct_point_light struct = pointLights[numPointLight++];
        struct.set(p, worldTransform);
    }

    public void acceptDirectionalLight(DirectionalLight d, Matrix4f worldTransform) {
        if (numDirectionalLight >= directionalLights.length) {
            log.warn("max number of directional light exceeded: {}", numDirectionalLight);
            return;
        }
        final struct_dir_light struct = directionalLights[numDirectionalLight++];
        struct.set(d, worldTransform);
    }

    public void acceptSpotLight(SpotLight s, Matrix4f worldTransform) {
        if (numSpotLight >= spotLights.length) {
            log.warn("max number of directional light exceeded: {}", numSpotLight);
            return;
        }
        final struct_spot_light struct = spotLights[numSpotLight++];
        struct.set(s, worldTransform);
    }

    // these mirrors the light objects used in shaders
    private class struct_point_light {
        public static final int SIZE = CONST.SIZE_VEC4 * 3;

        Vector4f position = new Vector4f();
        Vector4f intensity = new Vector4f();
        Vector4f attenuation = new Vector4f();

        public void get(int bufOffset, ByteBuffer buffer) {
            position.get(bufOffset, buffer);
            intensity.get(bufOffset + CONST.SIZE_VEC3 * 1, buffer);
            attenuation.get(bufOffset + CONST.SIZE_VEC3 * 2, buffer);
        }

        public void set(PointLight p, Matrix4f worldTransform) {
            position.set(0, 0, 0, 1).mul(worldTransform);
            intensity.set(p.color);
            attenuation.set(p.attenuation.x, p.attenuation.y, p.attenuation.z, 1);
        }
    }

    private class struct_dir_light {
        public static final int SIZE = CONST.SIZE_VEC4 * 2;

        Vector4f direction = new Vector4f();
        Vector4f intensity = new Vector4f();

        public void get(int bufOffset, ByteBuffer buffer) {
            direction.get(bufOffset, buffer);
            intensity.get(bufOffset + CONST.SIZE_VEC3, buffer);
        }

        public void set(DirectionalLight d, Matrix4f worldTransform) {
            direction.set(d.getDirection()).mul(worldTransform).normalize();
            intensity.set(d.color);
        }
    }

    private class struct_spot_light {
        public static final int SIZE = CONST.SIZE_VEC4 * 5;

        Vector4f position = new Vector4f();
        Vector4f intensity = new Vector4f();
        Vector4f attenuation = new Vector4f();
        Vector4f direction = new Vector4f();
        Vector4f angle = new Vector4f();

        public void get(int bufOffset, ByteBuffer buffer) {
            position.get(bufOffset, buffer);
            intensity.get(bufOffset + CONST.SIZE_VEC3 * 1, buffer);
            attenuation.get(bufOffset + CONST.SIZE_VEC3 * 2, buffer);
            direction.get(bufOffset + CONST.SIZE_VEC3 * 3, buffer);
            angle.get(bufOffset + CONST.SIZE_VEC3 * 4, buffer);
        }

        public void set(SpotLight s, Matrix4f worldTransform) {
            position.set(0, 0, 0, 1).mul(worldTransform);
            intensity.set(s.color);
            attenuation.set(s.attenuation.x, s.attenuation.y, s.attenuation.z, 1);
            direction.set(s.getDirection()).mul(worldTransform).normalize();
            angle.set(s.angle.x, s.angle.y, s.angle.z, 0);
        }
    }

}
