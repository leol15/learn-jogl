package com.play.app.scene;

import java.awt.Color;

import com.play.app.graphics.*;
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
    protected final Vector4f color = new Vector4f(0.7f, 0.7f, 0.7f, 1);

    @Setter
    protected Texture texture;

    protected void bindAll() {
        if (texture != null) {
            texture.bindTexture();
        }

        if (shader != null) {
            if (color != null) {
                shader.uniform4f(CONST.SHADER_COLOR, color);
            }
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

    public SOBase setColor(final Color color) {
        this.setColor(Func.toVec4(color));
        return this;
    }

    public SOBase setColor(final Vector4f color) {
        this.color.set(color);
        return this;
    }

}
