package com.play.app.ui.elements;

import static java.awt.Font.BOLD;
import static java.awt.Font.MONOSPACED;

import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.NormalMesh;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;
import com.play.app.utils.CONST;
import com.play.app.utils.Color;
import com.play.app.utils.FontUtils;
import com.play.app.utils.FontUtils.FontPack;
import com.play.app.utils.FontUtils.Glyph;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Uses a mesh with a font texture
 */
@Log4j2
public class UIText extends AbstractUIElement {

    private final ShaderProgram textShader;

    @Getter
    private CharSequence content;
    public final Color textColor = new Color(1);
    /**
     * Contains the glyphs for each char.
     */
    private final FontPack fontPack;
    private final NormalMesh mesh;
    private float width, height;

    public UIText(UIManager uiManager) {
        this(uiManager, "UIText");
    }

    public UIText(UIManager uiManager, CharSequence text) {
        super(uiManager);
        textShader = uiManager.textShader;
        mesh = new NormalMesh();
        fontPack = FontUtils.getInstance()
                .createFontPack(MONOSPACED, BOLD, 24);
        setText(text);
    }

    public void setText(CharSequence text) {
        content = text;
        mesh.reset();

        width = 0;
        height = 0;

        float drawX = 0;
        float drawY = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY += fontPack.texture.getHeight();
                drawX = 0;
                continue;
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = fontPack.glyphs.get(ch);

            addChar(drawX, drawY, g.width, g.height, ch);

            drawX += g.width;
            width = Math.max(width, drawX);
            height = Math.max(height, drawY + g.height);
        }
        mesh.uploadData();
    }

    // coords in screen space
    private void addChar(float x, float y, float w, float h, Character c) {
        /**
        * Triangle layout:
        * 12 56
        * 43 87
        */
        final Vector4f uv_wh = fontPack.getUVforChar(c);

        final int idx = mesh.numPos();

        mesh.addPos(x, y, 0);
        mesh.addPos(x + w, y, 0);
        mesh.addPos(x + w, y + h, 0);
        mesh.addPos(x, y + h, 0);

        mesh.addUV(uv_wh.x, uv_wh.y);
        mesh.addUV(uv_wh.x + uv_wh.z, uv_wh.y);
        mesh.addUV(uv_wh.x + uv_wh.z, uv_wh.y + uv_wh.w);
        mesh.addUV(uv_wh.x, uv_wh.y + uv_wh.w);

        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);
        mesh.addNormal(0, 0, 1);

        mesh.addTriangle(idx, idx + 1, idx + 2);
        mesh.addTriangle(idx, idx + 2, idx + 3);
    }

    @Override
    protected float getW() {
        return width;
    }

    @Override
    protected float getH() {
        return height;
    }

    @Override
    public void drawInternal(Matrix4f transform) {
        drawBackground(transform);

        textShader.uniform4f(CONST.MATERIAL_COLOR, textColor.get());
        textShader.uniformMatrix4fv(CONST.MODEL_MATRIX, transform);

        fontPack.texture.bindTexture();
        textShader.useProgram();

        mesh.draw();

        textShader.unuseProgram();
        fontPack.texture.unbindTexture();
        // glPolygonMode(GL_FRONT, oldPolygonMode);
    }

    @Override
    public void destroy() {
        mesh.destroy();
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        log.trace("UIText {} {}", width, height);
        return this;
    }
}
