package com.play.app.scene.sceneobject;

import java.awt.Color;

import com.play.app.graphics.*;
import com.play.app.scene.lights.LightingMaterial;
import com.play.app.utils.*;

import org.joml.Vector4f;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class SOBase {
    // TODO: this can be abtract to a material class

    @Setter
    protected ShaderProgram shader;

    // properties that can be applied to shader
    protected final LightingMaterial material = new LightingMaterial();

    @Setter
    protected Texture texture;

    protected void bindAll() {
        if (texture != null) {
            texture.bindTexture();
        }

        if (shader != null) {
            material.configureShader(shader);
            shader.useProgram();
        }
    }

    protected void unbindAll() {
        if (shader != null) {
            shader.unuseProgram();
        }
        if (texture != null) {
            texture.unbindTexture();
        }
    }

}
