package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.basics.*;
import com.play.app.graphics.*;
import com.play.app.scene.lights.LightingMaterial;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.Matrix4f;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Manages the shader for a SO and some shader related data
 */
@Accessors(chain = true)
public class SORenderProperty implements Editable, SaveLoad {
    @Setter
    public ShaderProgram shader;
    @Setter
    public Texture texture;
    public final LightingMaterial material = new LightingMaterial();

    @Override
    public void addToEditor(PropertyEditor editor) {
        material.select(editor);
    }

    public void bind(Matrix4f modelMatrix) {
        if (texture != null) {
            texture.bindTexture();
        }

        if (shader != null) {
            material.configureShader(shader);
            shader.uniformMatrix4fv(CONST.MODEL_MATRIX, modelMatrix);
            shader.useProgram();
        }
    }

    public void unbind() {
        if (shader != null) {
            shader.unuseProgram();
        }
        if (texture != null) {
            texture.unbindTexture();
        }
    }

    @Override
    public void save(YAMLGenerator generator) throws IOException {
        generator.writeStartObject();
        WorldSerializer.writeObjectField("shader", shader, generator);
        WorldSerializer.writeObjectField("texture", texture, generator);
        WorldSerializer.writeObjectField("material", material, generator);
        generator.writeEndObject();
    }
}
