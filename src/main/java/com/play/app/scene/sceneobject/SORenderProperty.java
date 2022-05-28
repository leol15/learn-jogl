package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.*;
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
public class SORenderProperty implements Editable, Savable, Loadable {
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
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("shader", shader);
        writer.writeObjectField("texture", texture);
        writer.writeObjectField("material", material);
        writer.writeEndObject();
    }

    @Override
    public void load(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();

        reader.consumeFieldName("shader");
        shader = ShaderProgram.create(reader);
        reader.consumeFieldName("texture");
        texture = Texture.create(reader);
        reader.consumeObjectField("material", material);

        reader.consumeEndObject();
    }
}
