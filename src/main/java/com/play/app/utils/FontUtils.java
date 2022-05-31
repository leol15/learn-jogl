package com.play.app.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Font.BOLD;
import static java.awt.Font.MONOSPACED;

import com.play.app.graphics.Texture;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Singleton
 */
@Log4j2
public class FontUtils {

    @Getter
    private static FontUtils instance = new FontUtils();

    private FontUtils() {}

    @AllArgsConstructor
    public class FontPack {
        public final Map<Character, Glyph> glyphs;
        public final Texture texture;

        // helper to convert to UV coordinates
        public Vector4f getUVforChar(Character c) {
            final Vector4f uv_wh = new Vector4f();
            final Glyph g = glyphs.get(c);
            if (g == null) {
                log.warn("Font pack does not have character [{}]", c);
            } else {
                uv_wh.x = g.x / (float) texture.getWidth();
                uv_wh.y = g.y / (float) texture.getHeight();
                // width and height
                uv_wh.z = g.width / (float) texture.getWidth();
                uv_wh.w = g.height / (float) texture.getHeight();
            }
            return uv_wh;
        }
    }

    @AllArgsConstructor
    public class Glyph {
        /**
         * Creates a font Glyph.
         *
         * @param width   Width of the Glyph
         * @param height  Height of the Glyph
         * @param x       X coordinate on the font texture
         * @param y       Y coordinate on the font texture
         * @param advance Advance width
         */
        public final int width;
        public final int height;
        public final int x;
        public final int y;
        public final float advance;
    }

    private Map<String, FontPack> FONT_PACKS = new HashMap<>();

    public FontPack createFontPack(String name, int style, int size) {
        final String key = String.format("%s %d %d", name, style, size);
        if (!FONT_PACKS.containsKey(key)) {
            final FontPack fontPack = createFontPackHelper(new Font(MONOSPACED, BOLD, 24), true);
            FONT_PACKS.put(key, fontPack);
        }
        return FONT_PACKS.get(key);
    }

    private FontPack createFontPackHelper(java.awt.Font font, boolean antiAlias) {
        final Map<Character, Glyph> glyphs = new HashMap<>();
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
        // AffineTransformOp operation = new AffineTransformOp(transform,
        //         AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
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

        return new FontPack(glyphs, fontTexture);
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
}
