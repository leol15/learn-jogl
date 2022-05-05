package com.play.app.ui;

import org.lwjgl.*;

import lombok.Setter;
import lombok.experimental.Accessors;

import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

import com.play.app.graphics.*;
import com.play.app.ui.WindowManager.Layer;

@Accessors(chain = true)
public class UIBase {

    // static things
    protected static ShaderProgram uiShader;

    // internal things
    protected WindowManager windowManager;

    // state
    @Setter
    protected boolean visible = true;

    // coordinates are in screen space
    public UIBase(WindowManager windowManager) {
        this.windowManager = windowManager;
        if (uiShader == null) {
            initStatic(windowManager);
        }
    }

    public void show() {
        if (!visible) {
            return;
        }

        int oldPolygonMode = glGetInteger(GL_POLYGON_MODE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // actual draw

        glPolygonMode(GL_FRONT_AND_BACK, oldPolygonMode);

    }

    private static void initStatic(WindowManager windowManager) {

        // create one off shader
        uiShader = new ShaderProgram();
        uiShader.loadShaderFromPath("resources/shaders/UI.vert", GL_VERTEX_SHADER);
        uiShader.loadShaderFromPath("resources/shaders/UI.frag", GL_FRAGMENT_SHADER);
        uiShader.linkProgram();

        // setup projection matrix to screen space
        onWindowSizeChanged(windowManager.window,
                windowManager.windowSize[0],
                windowManager.windowSize[1]);
        windowManager.addWindowSizeCallback(Layer.ALWAYS, UIBase::onWindowSizeChanged);
    }

    private static void onWindowSizeChanged(long window, int width, int height) {
        Matrix4f projection = new Matrix4f();
        projection.scale(2f / width, -2f / height, 1);
        projection.translate(-width / 2, -height / 2, 0);
        FloatBuffer screenToGLSpace = BufferUtils.createFloatBuffer(16);
        projection.get(screenToGLSpace);
        uiShader.uniformMatrix4fv("UItoGL", screenToGLSpace);
    }

}
