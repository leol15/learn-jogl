package com.play.app.scene.sceneobject;

import com.play.app.graphics.*;
import com.play.app.scene.lights.LightingMaterial;

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

    final protected void bindAll() {
        if (texture != null) {
            texture.bindTexture();
        }

        if (shader != null) {
            material.configureShader(shader);
            shader.useProgram();
        }
    }

    final protected void unbindAll() {
        if (shader != null) {
            shader.unuseProgram();
        }
        if (texture != null) {
            texture.unbindTexture();
        }
    }

}
