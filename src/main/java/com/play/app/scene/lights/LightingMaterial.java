package com.play.app.scene.lights;

import java.awt.Color;

import com.play.app.graphics.ShaderProgram;
import com.play.app.ui.PropertyEditor;
import com.play.app.utils.*;

import org.joml.*;

public class LightingMaterial {
    public Vector4f color = new Vector4f(1, 1, 1, 1);
    public Vector3f specularHardness = new Vector3f(15, 0, 0);

    public void configureShader(ShaderProgram shader) {
        shader.uniform4f(CONST.SHADER_COLOR, color);
        shader.uniformf(CONST.SHADER_SPECULAR_HARDNESS, specularHardness.x);
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
        editor.addProperty("Mat Color", color);
        editor.addProperty("specular hardness", specularHardness);
    }
}
