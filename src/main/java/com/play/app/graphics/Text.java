package com.play.app.graphics;

import static java.awt.Font.BOLD;
import static java.awt.Font.MONOSPACED;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.awt.Font;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.play.app.mesh.NormalMesh;
import com.play.app.ui.UIManager;
import com.play.app.utils.CONST;
import com.play.app.utils.Color;
import com.play.app.utils.FontUtils;
import com.play.app.utils.FontUtils.FontPack;
import com.play.app.utils.FontUtils.Glyph;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

/**
 * draw some text starting at x, y
 * Uses a mesh with a font texture, uses Texture shader?
 */
public class Text {

    private final ShaderProgram textShader;

    // styles
    private final Color textColor = new Color(0.1);
    /**
     * Contains the glyphs for each char.
     */
    private final FontPack fontPack;

    private final int fontHeight;
    private final VAO vao;
    private int numChars;
    private float textWidth, textHeight;
    private float textX, textY;

    public Text(UIManager uiManager) {
        textShader = uiManager.textShader;

        fontPack = FontUtils.getInstance()
                .createFontPack(new Font(MONOSPACED, BOLD, 24), true);
        fontHeight = fontPack.texture.getHeight();
        vao = new VAO();
    }

    public Text(UIManager uiManager, CharSequence text, float x, float y) {
        this(uiManager);
        // setText(text, x, y);
    }

    public float getWidth() {
        return textWidth;
    }

    public float getHeight() {
        return textHeight;
    }

    public float getFontHeight() {
        return fontHeight;
    }

    public void setColor(java.awt.Color c) {
        textColor.set(c);
    }

    public void setColor(float r, float g, float b, float a) {
        textColor.set(r, g, b, a);
    }

    public void setColor(Vector4f c) {
        textColor.set(c);
    }

    public void setText(CharSequence text) {
        NormalMesh mesh = new NormalMesh();
        int x = 0;
        int y = 0;

        // textX = x;
        // textY = y;

        // TODO bug, resetting text messes up GL
        numChars = 0;
        textWidth = 0;
        textHeight = 0;
        /**
         * Layout:
         * 12 56
         * 43 87
         */
        FloatBuffer positions = BufferUtils.createFloatBuffer(text.length() * 4 * 3);
        FloatBuffer uvs = BufferUtils.createFloatBuffer(text.length() * 4 * 2);
        IntBuffer ib = BufferUtils.createIntBuffer(text.length() * 6);
        float drawX = 0;
        float drawY = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY += fontHeight;
                // drawX = x;
                continue;
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = fontPack.glyphs.get(ch);

            addCharToBuffer(positions, uvs, drawX, drawY, g.x, g.y, g.width, g.height);
            int indexBase = numChars * 4;
            ib.put(indexBase).put(indexBase + 1).put(indexBase + 2);
            ib.put(indexBase).put(indexBase + 2).put(indexBase + 3);

            numChars++;
            drawX += g.width;
            textWidth = Math.max(textWidth, drawX - x);
            textHeight = Math.max(textHeight, drawY + g.height - y);
        }
        positions.flip();
        uvs.flip();
        ib.flip();
        vao.bufferData(CONST.VERT_IN_POSITION, positions);
        vao.bufferData(CONST.VERT_IN_UV, uvs);
        vao.bufferIndices(ib);
    }

    public void draw() {
        int oldPolygonMode = glGetInteger(GL_POLYGON_MODE);
        glPolygonMode(GL_FRONT, GL_FILL);

        textShader.uniform4f(CONST.MATERIAL_COLOR, textColor.get());

        fontPack.texture.bindTexture();
        textShader.useProgram();
        vao.bind();

        // textShader.uniform("texcoord", 0);
        glDrawElements(GL_TRIANGLES, numChars * 6, GL_UNSIGNED_INT, 0);

        vao.unbind();
        textShader.unuseProgram();
        fontPack.texture.unbindTexture();

        glPolygonMode(GL_FRONT, oldPolygonMode);
    }

    // coords in screen space
    private void addCharToBuffer(FloatBuffer positions, FloatBuffer UVs, float x, float y, float tx, float ty, float tw,
            float th) {
        float w = tw;
        float h = th;
        tx = tx / fontPack.texture.getWidth();
        ty = ty / fontPack.texture.getHeight();
        tw = tw / fontPack.texture.getWidth();
        th = th / fontPack.texture.getHeight();

        positions.put(x).put(y).put(0);
        UVs.put(tx).put(ty);
        positions.put(x + w).put(y).put(0);
        UVs.put(tx + tw).put(ty);
        positions.put(x + w).put(y + h).put(0);
        UVs.put(tx + tw).put(ty + th);
        positions.put(x).put(y + h).put(0);
        UVs.put(tx).put(ty + th);
    }

}
