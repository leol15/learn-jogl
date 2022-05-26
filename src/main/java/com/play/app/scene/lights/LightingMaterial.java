package com.play.app.scene.lights;

import java.awt.Color;

import com.play.app.graphics.ShaderProgram;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.*;

public class LightingMaterial {
    public final Vector4f color = new Vector4f(1, 1, 1, 1);
    public final Vector3f specularHardness = new Vector3f(100, 0, 0);

    public void configureShader(ShaderProgram shader) {
        shader.uniform4f(CONST.MATERIAL_COLOR, color);
        shader.uniformf(CONST.MATERIAL_SPECULAR_HARDNESS, specularHardness.x);
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
}
