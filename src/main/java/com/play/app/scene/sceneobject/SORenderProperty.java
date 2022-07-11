package com.play.app.scene.sceneobject;

import java.io.IOException;

import com.play.app.basics.Editable;
import com.play.app.basics.Loadable;
import com.play.app.basics.Savable;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Texture;
import com.play.app.scene.lights.LightingMaterial;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.property.StringProperty;
import com.play.app.utils.CONST;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Manages the shader for a SO and some shader related data
 */
@Accessors(chain = true)
public class SORenderProperty implements Editable, Savable, Loadable {

    @Getter
    private ShaderProgram shader;
    private StringProperty shaderName = new StringProperty("");
    @Setter
    public Texture texture;
    public final LightingMaterial material = new LightingMaterial();

    public SORenderProperty() {
        shaderName.updateEvent.addListener(sp -> {
            final ShaderProgram newShader = ShaderUtils.getShader(sp.getValue());
            if (newShader != null) {
                setShader(newShader);
            }
        });
    }

    public SORenderProperty setShader(ShaderProgram newShader) {
        shader = newShader;
        shaderName.setValue(ShaderUtils.getShaderName(shader));
        return this;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        material.select(editor);
        editor.addProperty("Shader", shaderName);
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
        setShader(ShaderProgram.create(reader));

        reader.consumeFieldName("texture");
        setTexture(Texture.create(reader));

        reader.consumeObjectField("material", material);

        reader.consumeEndObject();
    }
}
