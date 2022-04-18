package com.play.app.ui;

import org.lwjgl.*;
import org.joml.*;

import java.util.*;
import java.util.List;
import java.nio.*;
import java.awt.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

import com.play.app.graphics.*;
import com.play.app.utils.VAO;
import com.play.app.geometry.*;


public class Button {

    // state
    private Vector4f buttonColor = new Vector4f(0.8f, 0.8f, 0.8f, 1f);
    private boolean visible = true;
    private Runnable action = null;
    private Text text;

    // static things
    static ShaderProgram uiShader;
    static List<Button> buttons = new ArrayList<>();
    static float windowWidth, windowHeight;

    // internal things
    private VAO vao;
    private Rect bounds; // in window space

    // coordinates are in screen space
    public Button(long window, float x, float y, float width, float height) {
        if (uiShader == null) {
            initStatic(window);
        }
        this.text = new Text(window, "Button", x, y);
        init(x, y, width, height);
    }
    
    public Button(long window, float x, float y, CharSequence label) {
        if (uiShader == null) {
            initStatic(window);
        }
        this.text = new Text(window, label, x, y);
        init(x, y, text.getWidth(), text.getHeight());
    }

    private void init(float x, float y, float width, float height) {
        bounds = new Rect(x, y, width, height);
        vao = createSquare(x, y, width, height);
        buttons.add(this);
    }
    
    private VAO createSquare(float x, float y, float width, float height) {
        FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 3);
        vertices.put(x).put(y).put(0);
        vertices.put(x + width).put(y).put(0);
        vertices.put(x + width).put(y + height).put(0);
        vertices.put(x).put(y + height).put(0);
        vertices.flip();
        IntBuffer elements = BufferUtils.createIntBuffer(6 * 6);
        elements.put(0).put(1).put(2)
                .put(0).put(2).put(3);
        elements.flip();

        vao = new VAO();
        vao.bufferVerticies(vertices);
        vao.bufferIndices(elements);
        vao.vertexAttribPointerF(0, 3, 3, 0);
        return vao;
    }

    public void setAction(Runnable r) { action = r; }
    public void setVisible(boolean v) { visible = v; }
    public void setColor(Color c) {
        setColor(c.getRed() / 255.0f,
                 c.getGreen() / 255.0f,
                 c.getBlue() / 255.0f,
                 c.getAlpha() / 255.0f);
    }
    public void setColor(float r, float g, float b, float a) {
        buttonColor.set(r, g, b, a);
        text.setColor(1 - r, 1 - g, 1 - b, 1);
    }

    public void show() {
        if (!visible) {
            return;
        }

        uiShader.uniform4f("color", buttonColor);
        
        vao.bind();
        uiShader.useProgram();

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glUseProgram(0);
        vao.unbind();
        
        text.draw();
    }
    
    private boolean handleClick(double x, double y) {
        if (bounds.inside((float) x, (float) y)) {
            System.out.println("Button Clicked");
            if (action != null) {
                action.run();
                return true;
            }
        }
        return false;
    }


    private void initStatic(long window) {
        // setup one callback on mouse click
        final DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
        final DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);
        glfwSetMouseButtonCallback(window, (window2, button, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }
            glfwGetCursorPos(window2, mouseX, mouseY);
            for (int i = 0; i < buttons.size(); i++) {
                if (!buttons.get(i).visible) continue;
                if (buttons.get(i).handleClick(mouseX.get(0), mouseY.get(0))) {
                    break;
                }
            }
        });
        
        // get window stats
        IntBuffer windowWidthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer windowHeightBuffer = BufferUtils.createIntBuffer(1);
        // Get the window size passed to glfwCreateWindow
        glfwGetWindowSize(window, windowWidthBuffer, windowHeightBuffer);
        windowWidth = windowWidthBuffer.get();
        windowHeight = windowHeightBuffer.get();

        // create one off shader
        uiShader = new ShaderProgram();
        uiShader.loadShaderFromPath("resources/shaders/UI.vert", GL_VERTEX_SHADER);
        uiShader.loadShaderFromPath("resources/shaders/UI.frag", GL_FRAGMENT_SHADER);
        uiShader.linkProgram();

        // setup projection matrix to screen space
        Matrix4f projection = new Matrix4f();
        projection.scale(2f / windowWidth, -2f / windowHeight, 1);
        projection.translate(- windowWidth / 2, -windowHeight / 2, 0);
        FloatBuffer screenToGLSpace = BufferUtils.createFloatBuffer(16);
        projection.get(screenToGLSpace);
        uiShader.uniformMatrix4fv("UItoGL", screenToGLSpace);
    }
    
}

