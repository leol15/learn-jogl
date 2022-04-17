package com.play.app.graphics;


import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.awt.image.AffineTransformOp;
import java.util.HashMap;
import java.util.Map;


import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;

import java.awt.Font;

import com.play.app.utils.*;

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
    

    private final Texture texture;
    /**
     * Contains the glyphs for each char.
     */
    private final Map<Character, Glyph> glyphs = new HashMap<>();
    private int fontHeight;
    private VAO vao;
    private int numChars;

    public Text() {
        texture = createFontTexture(new Font(MONOSPACED, PLAIN, 16), true);
        fontHeight = texture.getHeight();
        vao = new VAO();
    }

    public void setText(CharSequence text, float x, float y, float windowWidth, float windowHeight) {
        numChars = text.length();

        /**
         * Layout: 
         *  12 56
         *  43 87
         */
        FloatBuffer fb = BufferUtils.createFloatBuffer(text.length() * 4 * (3 + 2));
        IntBuffer ib = BufferUtils.createIntBuffer(text.length() * 6);
        
        float drawX = x;
        float drawY = y;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY -= fontHeight;
                drawX = x;
                continue;
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = glyphs.get(ch);

            int indexBase = fb.position();
            addChar(fb, drawX, drawY, g.x, g.y, g.width, g.height);
            // fb.put(drawX).put(drawY).put(0).put(g.x).put(g.y);
            // fb.put(drawX + g.width).put(drawY).put(0).put(g.x + g.width).put(g.y);
            // fb.put(drawX + g.width).put(drawY + g.height).put(0).put(g.x + g.width).put(g.y + g.height);
            // fb.put(drawX).put(drawY + g.height).put(0).put(g.x).put(g.y + g.height);

            ib.put(indexBase).put(indexBase + 1).put(indexBase + 2);
            ib.put(indexBase).put(indexBase + 2).put(indexBase + 3);

            drawX += g.width;
        }
        vao.bufferVerticies(fb);
        vao.bufferIndices(ib);
    }

    // coords in screen space
    private void addChar(FloatBuffer fb, float x, float y, float tx, float ty, float tw, float th) {
        float w = tw;
        float h = th;
        tx = tx / texture.getWidth();
        ty = ty / texture.getHeight();
        tw = tw / texture.getWidth();
        th = th / texture.getHeight();
        // Func.toGLPosition(x, max, isY)
    }


    /**
     * Draw text at the specified position and color.
     * @param renderer The renderer to use
     * @param text     Text to draw
     * @param x        X coordinate of the text position
     * @param y        Y coordinate of the text position
     * @param c        Color to use
     */
    public void draw() {
        texture.bindTexture();
        vao.bind();
        
        glDrawElements(GL_TRIANGLES, numChars * 6, GL_UNSIGNED_INT, 0);
        texture.unbindTexture();
        vao.unbind();
    }


    /**
     * Helper to load font into a texture
     * @param font
     * @param antiAlias
     * @return
     */
    private Texture createFontTexture(java.awt.Font font, boolean antiAlias) {
        int imageWidth = 0;
        int imageHeight = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127) continue;
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c, antiAlias);
            if (ch == null) continue;

            imageWidth += ch.getWidth();
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int x = 0;

        // draw font to image
        for (int i = 32; i < 256; i++) {
            if (i == 127) continue;
            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c, antiAlias);
            if (charImage == null) continue;
            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            glyphs.put(c, ch);

            System.out.println("char " + c + " w " + charWidth + " h " + charHeight);
        }

        /* Flip image Horizontal to get the origin to bottom left */
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform,
                                                            AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);
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
                buffer.put((byte) ((pixel >> 8 ) & 0xFF));
                buffer.put((byte) ((pixel >> 0 ) & 0xFF));
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
        if (charWidth == 0) return null;

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
}
