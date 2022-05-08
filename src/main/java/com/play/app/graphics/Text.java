package com.play.app.graphics;

import static java.awt.Font.*;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.lang.Math;
import java.nio.*;
import java.util.*;

import com.play.app.utils.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

/**
 * draw some text starting at x, y
 */
public class Text {

    public class Glyph {
        public final int width;
        public final int height;
        public final int x;
        public final int y;
        public final float advance;

        /**
         * Creates a font Glyph.
         *
         * @param width   Width of the Glyph
         * @param height  Height of the Glyph
         * @param x       X coordinate on the font texture
         * @param y       Y coordinate on the font texture
         * @param advance Advance width
         */
        public Glyph(int width, int height, int x, int y, float advance) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.advance = advance;
        }
    }

    // static things
    private static ShaderProgram textShader;

    // styles
    private Vector4f textColor = new Vector4f(0.1f, 0.1f, 0.1f, 1f);

    private Texture texture;
    /**
     * Contains the glyphs for each char.
     */
    private final Map<Character, Glyph> glyphs = new HashMap<>();
    private final int fontHeight;
    private final VAO vao;
    private int numChars;
    private float textWidth, textHeight;
    private float textX, textY;

    public Text(WindowManager windowManager) {
        if (textShader == null) {
            initStatic(windowManager);
        }
        texture = createFontTexture(new Font(MONOSPACED, BOLD, 24), true);
        // textShader.uniform("texImage", 0); for multiple texture

        fontHeight = texture.getHeight();
        vao = new VAO();
    }

    public Text(WindowManager windowManager, CharSequence text, float x, float y) {
        this(windowManager);
        setText(text, x, y);
    }

    public float getWidth() {
        return textWidth;
    }

    public float getHeight() {
        return textHeight;
    }

    public void setColor(Color c) {
        setColor(c.getRed() / 255.0f,
                c.getGreen() / 255.0f,
                c.getBlue() / 255.0f,
                c.getAlpha() / 255.0f);
    }

    public void setColor(float r, float g, float b, float a) {
        textColor.set(r, g, b, a);
    }

    public void setColor(Vector4f c) {
        setColor(c.x, c.y, c.z, c.w);
    }

    public void setText(CharSequence text) {
        setText(text, textX, textY);
    }

    public void setText(CharSequence text, float x, float y) {
        textX = x;
        textY = y;

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
        float drawX = x;
        float drawY = y;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY += fontHeight;
                drawX = x;
                continue;
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = glyphs.get(ch);

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
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        textShader.uniform4f("color", textColor);

        texture.bindTexture();
        textShader.useProgram();
        vao.bind();

        // textShader.uniform("texcoord", 0);
        glDrawElements(GL_TRIANGLES, numChars * 6, GL_UNSIGNED_INT, 0);

        vao.unbind();
        textShader.unuseProgram();
        texture.unbindTexture();

        glPolygonMode(GL_FRONT_AND_BACK, oldPolygonMode);
    }

    // coords in screen space
    private void addCharToBuffer(FloatBuffer positions, FloatBuffer UVs, float x, float y, float tx, float ty, float tw,
            float th) {
        float w = tw;
        float h = th;
        tx = tx / texture.getWidth();
        ty = ty / texture.getHeight();
        tw = tw / texture.getWidth();
        th = th / texture.getHeight();

        positions.put(x).put(y).put(0);
        UVs.put(tx).put(ty);
        positions.put(x + w).put(y).put(0);
        UVs.put(tx + tw).put(ty);
        positions.put(x + w).put(y + h).put(0);
        UVs.put(tx + tw).put(ty + th);
        positions.put(x).put(y + h).put(0);
        UVs.put(tx).put(ty + th);
    }

    /**
     * Helper to load font into a texture
     * 
     * @param font
     * @param antiAlias
     * @return
     */
    private Texture createFontTexture(java.awt.Font font, boolean antiAlias) {
        int imageWidth = 0;
        int imageHeight = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127)
                continue;
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c, antiAlias);
            if (ch == null)
                continue;

            imageWidth += ch.getWidth();
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int x = 0;

        // draw font to image
        for (int i = 32; i < 256; i++) {
            if (i == 127)
                continue;
            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c, antiAlias);
            if (charImage == null)
                continue;
            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            glyphs.put(c, ch);
        }

        /* Flip image Horizontal to get the origin to bottom left */
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        // image = operation.filter(image, null);
        /* Get charWidth and charHeight of image */
        int width = image.getWidth();
        int height = image.getHeight();

        // pixel data
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        // put in buffer
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i * width + j];
                // RGB
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) ((pixel >> 0) & 0xFF));
                // alpha
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        Texture fontTexture = Texture.createTexture(width, height, buffer);
        return fontTexture;
    }

    private BufferedImage createCharImage(java.awt.Font font, char c, boolean antiAlias) {
        // create temporary image to extract character size
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();

        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();
        if (charWidth == 0)
            return null;

        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        g.setPaint(java.awt.Color.WHITE);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();
        return image;
    }

    private void initStatic(WindowManager windowManager) {
        // get window stats
        IntBuffer windowWidthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer windowHeightBuffer = BufferUtils.createIntBuffer(1);
        // Get the window size passed to glfwCreateWindow
        glfwGetWindowSize(windowManager.window, windowWidthBuffer, windowHeightBuffer);
        int windowWidth = windowWidthBuffer.get();
        int windowHeight = windowHeightBuffer.get();

        textShader = new ShaderProgram()
                .withShader("resources/shaders/Text.vert", GL_VERTEX_SHADER)
                .withShader("resources/shaders/Text.frag", GL_FRAGMENT_SHADER);
        textShader.linkProgram();

        // screen to UI projection
        Matrix4f projection = new Matrix4f();
        projection.scale(2f / windowWidth, -2f / windowHeight, 1);
        projection.translate(-windowWidth / 2, -windowHeight / 2, 0);
        FloatBuffer screenToGLSpace = BufferUtils.createFloatBuffer(16);
        projection.get(screenToGLSpace);
        textShader.uniformMatrix4fv("UItoGL", screenToGLSpace);
    }

}
