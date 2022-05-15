package com.play.app.scene.lights;

import com.play.app.graphics.UBO;
import com.play.app.scene.SceneNode;
import com.play.app.utils.CONST;

import org.joml.Matrix4f;

import lombok.extern.log4j.Log4j2;

// manages a UBO for all the kinds of lights
// a singleton? static class?
@Log4j2
public class LightUBO {

    // layout
    static int lightBufferObject;

    public static void init() {
        lightBufferObject = UBO.createUboBuffer(CONST.UBO_LIGHTS);
    }

    static final LightSceneVisitor VISITOR = new LightSceneVisitor();

    public static void addAllLights(SceneNode root) {
        root.accept(VISITOR);
    }

    public static void acceptPointLight(PointLight p, Matrix4f worldTransform) {
        log.debug("adding a pointlight to UBO");
    }

}
