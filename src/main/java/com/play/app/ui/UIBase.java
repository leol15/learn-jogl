package com.play.app.ui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;
import java.nio.*;

import com.play.app.geometry.Rect;
import com.play.app.graphics.*;
import com.play.app.utils.*;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

/**
 * Every UI element should derive from UIBase
 * For simplicity, every UI element is a square
 */
@Log4j2
@Accessors(chain = true)
public class UIBase {

    // static things
    protected static ShaderProgram uiShader;
    private static final VAO backgroundVAO = createUnitPlane();

    // things
    protected final WindowManager windowManager;

    // UIBase managed things
    // postion related
    private final Matrix4f screenToGLSpace = new Matrix4f();
    private final Rect bound = new Rect(0, 0, 100, 100);
    private final Vector2f viewPortSize = new Vector2f();
    // properties
    protected final Vector4f backgroundColor = new Vector4f(0.3f, 0.3f, 0.3f, 1);

    // state
    @Setter
    protected boolean visible = true;

    // coordinates are in screen space
    public UIBase(WindowManager windowManager) {
        this.windowManager = windowManager;
        windowManager.addWindowSizeCallback(Layer.ALWAYS, this::onWindowSizeChanged);

        onWindowSizeChanged(windowManager.window,
                windowManager.windowSize[0],
                windowManager.windowSize[1]);

        if (uiShader == null) {
            // create one off shader
            uiShader = new ShaderProgram()
                    .withShader("resources/shaders/UI.vert", GL_VERTEX_SHADER)
                    .withShader("resources/shaders/UI.frag", GL_FRAGMENT_SHADER)
                    .linkProgram();
        }
    }

    public UIBase(WindowManager windowManager, float x, float y, float w, float h) {
        this.windowManager = windowManager;
        windowManager.addWindowSizeCallback(Layer.ALWAYS, this::onWindowSizeChanged);

        onWindowSizeChanged(windowManager.window,
                windowManager.windowSize[0],
                windowManager.windowSize[1]);

        setPosition(x, y);
        setSize(w, h);

        if (uiShader == null) {
            // create one off shader
            uiShader = new ShaderProgram()
                    .withShader("resources/shaders/UI.vert", GL_VERTEX_SHADER)
                    .withShader("resources/shaders/UI.frag", GL_FRAGMENT_SHADER)
                    .linkProgram();
        }
    }

    // TODO is this needed?
    private void onClick() {

    }

    public void show() {
        if (!visible) {
            return;
        }

        showInternal();
    }

    public UIBase setPosition(float x, float y) {
        bound.setX(x).setY(y);
        computeProjectionMatrix();
        return this;
    }

    public UIBase setSize(float width, float height) {
        bound.setW(width).setH(height);
        computeProjectionMatrix();
        return this;
    }

    public UIBase setBounds(float x, float y, float w, float h) {
        bound.setX(x).setY(y).setW(w).setH(h);
        computeProjectionMatrix();
        return this;
    }

    public void setColor(Color c) {
        setColor(c.getRed() / 255.0f,
                c.getGreen() / 255.0f,
                c.getBlue() / 255.0f,
                c.getAlpha() / 255.0f);
    }

    public float getWidth() {
        return bound.getW();
    }

    public float getHeight() {
        return bound.getH();
    }

    public float getX() {
        return bound.getX();
    }

    public float getY() {
        return bound.getY();
    }

    //////////////////
    // for subclasses
    //////////////////

    // implement this to draw 
    protected void showInternal() {
        showBackground();
    }

    // implement this to update color
    public UIBase setColor(float r, float g, float b, float a) {
        return this;
    }

    protected void showBackground() {
        int oldPolygonMode = glGetInteger(GL_POLYGON_MODE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        uiShader.uniformMatrix4fv("UItoGL", screenToGLSpace);
        uiShader.uniform4f(CONST.SHADER_COLOR, backgroundColor);
        // actual draw
        uiShader.useProgram();
        backgroundVAO.draw();
        uiShader.unuseProgram();

        glPolygonMode(GL_FRONT_AND_BACK, oldPolygonMode);

        // so subsequent draw can appear, TODO maybe just disable it
        glClear(GL_DEPTH_BUFFER_BIT);
    }

    protected UIBase setBackgroundColor(final Vector4f color) {
        return this.setBackgroundColor(color.x, color.y, color.z, color.w);
    }

    protected UIBase setBackgroundColor(float r, float g, float b, float a) {
        backgroundColor.set(r, g, b, a);
        return this;
    }

    protected UIBase setBackgroundColor(final Color color) {
        this.setBackgroundColor(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
        return this;
    }

    protected boolean inside(float screenX, float screenY) {
        return visible && bound.inside(screenX, screenY);
    }

    //////////////////
    // internal thing
    //////////////////

    private void setViewPortSize(int width, int height) {
        viewPortSize.set(width, height);
        computeProjectionMatrix();
    }

    private void onWindowSizeChanged(long window, int width, int height) {
        setViewPortSize(width, height);
    }

    private void computeProjectionMatrix() {
        screenToGLSpace.identity();
        // to gl space
        screenToGLSpace.scale(2f / viewPortSize.x, -2f / viewPortSize.y, 1);
        screenToGLSpace.translate(-viewPortSize.x / 2, -viewPortSize.y / 2, 0);
        // scale and move
        screenToGLSpace.scale(bound.getW(), bound.getH(), 1);
        screenToGLSpace.translate(bound.getX() / bound.getW(), bound.getY() / bound.getH(), 0);
    }

    private static VAO createUnitPlane() {
        // create a unit plane for all UI backgrouds
        final VAO vao = new VAO();
        final FloatBuffer positions = BufferUtils.createFloatBuffer(3 * 4);
        positions.put(0).put(0).put(0);
        positions.put(0).put(1).put(0);
        positions.put(1).put(1).put(0);
        positions.put(1).put(0).put(0);

        final IntBuffer elements = BufferUtils.createIntBuffer(2 * 3);
        elements.put(0).put(1).put(2);
        elements.put(0).put(2).put(3);

        positions.flip();
        elements.flip();

        vao.bufferData(CONST.VERT_IN_POSITION, positions);
        vao.bufferIndices(elements);

        vao.setDrawFunction(() -> glDrawElements(GL_TRIANGLES, 2 * 3, GL_UNSIGNED_INT, 0));

        return vao;
    }

}
