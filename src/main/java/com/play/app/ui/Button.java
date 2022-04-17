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
    static ShaderProgram buttonShader;
    static List<Button> buttons = new ArrayList<>();
    static float windowWidth, windowHeight;

    // internal things
    private VAO vao;
    private Rect bounds; // in window space

    // coordinates are in screen space
    public Button(long window, float x, float y, float width, float height) {
        if (buttonShader == null) {
            initStatic(window);
        }

        bounds = new Rect(x, y, width, height);
        
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
        vao.vertexAttribPointer(0, 3, GL_FLOAT, 3 * Float.BYTES);

        // create text
        text = new Text();
        // text.setText("Button", 0, 0);

        buttons.add(this);
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
    }

    public void show() {
        if (!visible) {
            return;
        }

        vao.bind();
        buttonShader.useProgram();
        buttonShader.uniform4f("color", buttonColor);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        vao.unbind();
        glUseProgram(0);

        // text.draw();
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
        // create one off shader
        buttonShader = new ShaderProgram();
        buttonShader.loadShaderFromPath("resources/shaders/UI.vert", GL_VERTEX_SHADER);
        buttonShader.loadShaderFromPath("resources/shaders/UI.frag", GL_FRAGMENT_SHADER);
        buttonShader.linkProgram();
        buttonShader.useProgram();
        glUseProgram(0);
        
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

        // setup projection matrix to screen space
        Matrix4f projection = new Matrix4f();
        projection.scale(2f / windowWidth, -2f / windowHeight, 1);
        projection.translate(- windowWidth / 2, -windowHeight / 2, 0);
        FloatBuffer screenToGLSpace = BufferUtils.createFloatBuffer(16);
        projection.get(screenToGLSpace);
        buttonShader.useProgram();
        buttonShader.uniformMatrix4fv("UItoGL", screenToGLSpace);
        glUseProgram(0);
    }
    
}

