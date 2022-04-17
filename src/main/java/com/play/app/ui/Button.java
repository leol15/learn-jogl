


package com.play.app.ui;

import org.lwjgl.*;
import org.joml.*;

import java.util.*;
import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

import com.play.app.graphics.*;
import com.play.app.geometry.*;


public class Button {

    // state
    private Vector3f buttonColor = new Vector3f(0.8f, 0.8f, 0.8f);
    private boolean visible = true;
    private Runnable action = null;

    // static things
    static ShaderProgram buttonShader;
    static List<Button> buttons = new ArrayList<>();
    static float windowWidth, windowHeight;

    // internal things
    private int vao;
    private Rect bounds; // in window space


    public Button(long window, float x, float y, float width, float height) {
        if (buttonShader == null) {
            initStatic(window);
        }

        bounds = new Rect(x, y, width, height);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        x = toGLPosition(x, windowWidth, false);
        y = toGLPosition(y, windowHeight, true);
        width = width / windowWidth * 2;
        height = height / windowHeight * 2;

        FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * (3 + 3));
        vertices.put(x).put(y);
        vertices.put(x + width).put(y);
        vertices.put(x + width).put(y - height);
        vertices.put(x).put(y - height);
        vertices.flip();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        IntBuffer elements = BufferUtils.createIntBuffer(6 * 6);
        elements.put(0).put(1).put(2)
                .put(0).put(2).put(3);
        elements.flip();
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glBindVertexArray(0);

        buttons.add(this);
    }

    public void setAction(Runnable r) { action = r; }
    public void setVisible(boolean v) { visible = v; }
    
    public void show() {
        if (!visible) {
            return;
        }

        buttonShader.useProgram();
        glBindVertexArray(vao);
        buttonShader.uniform3f("color", buttonColor);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glUseProgram(0);
        glBindVertexArray(0);
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

    private float toGLPosition(float value, float max, boolean invert) {
        if (invert) {
            return (max - value) * 2 / max - 1.0f;
        } else {
            return value * 2 / max - 1.0f;
        }
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
                if (buttons.get(i).handleClick(mouseX.get(0), mouseY.get(0))) {
                    break;
                }
            }
        });

        // get window stats
        IntBuffer windowWidthBuffer = BufferUtils.createIntBuffer(1); // int*
        IntBuffer windowHeightBuffer = BufferUtils.createIntBuffer(1); // int*
        // Get the window size passed to glfwCreateWindow
        glfwGetWindowSize(window, windowWidthBuffer, windowHeightBuffer);
        windowWidth = windowWidthBuffer.get();
        windowHeight = windowHeightBuffer.get();
    }

}

