package com.play.app.scene.lights;

import java.awt.Color;
import java.io.IOException;

import com.play.app.basics.*;
import com.play.app.graphics.ShaderProgram;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.property.FloatProperty;
import com.play.app.utils.*;

import org.joml.*;

public class LightingMaterial implements Savable, Loadable {
    public final Vector4f color = new Vector4f(1, 1, 1, 1);
    public final FloatProperty specularHardness = new FloatProperty(100);

    public void configureShader(ShaderProgram shader) {
        shader.uniform4f(CONST.MATERIAL_COLOR, color);
        shader.uniformf(CONST.MATERIAL_SPECULAR_HARDNESS, specularHardness.getValue());
    }

    public boolean hasTransparency() {
        return color.w < 1;
    }

    public LightingMaterial setColor(final Color color) {
        this.setColor(Func.toVec4(color));
        return this;
    }

    public LightingMaterial setColor(final Vector4f color) {
        this.color.set(color);
        return this;
    }

    public void select(PropertyEditor editor) {
        editor.addProperty("Color", color);
        editor.addProperty("Shiness", specularHardness);
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("color", color);
        writer.writeObjectField("specularHardness", specularHardness.getValue());
        writer.writeEndObject();
    }

    public void load(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        reader.consumeObjectField("color", color);
        specularHardness.setValue(reader.consumeFloatField("specularHardness"));
        reader.consumeEndObject();
    }
}
