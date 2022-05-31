package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.nio.FloatBuffer;

import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.scene.camera.CameraManager;
import com.play.app.utils.CONST;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Rendering {

    public Rendering(long window) {
        glEnable(GL_DEPTH_TEST);
        // glDepthFunc(GL_LESS);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // setup shader
        ShaderProgram shaderProgram = ShaderUtils.getShader("Simple");

        // set uniform locations
        final WindowManager windowManager = new WindowManager(window);
        final CameraManager camera = new CameraManager(windowManager);
        Matrix4f model = new Matrix4f();
        FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
        model.get(fbModel);
        shaderProgram.uniformMatrix4fv("model", fbModel);

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        model.scale(0.6f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            model.rotate(0.01f, new Vector3f(1, 1, 1));
            model.get(fbModel);

            shaderProgram.uniformMatrix4fv(CONST.MODEL_MATRIX, fbModel);

            shaderProgram.useProgram();
            UnitGeometries.drawCube();

            camera.show();

            glfwPollEvents();
        }
    }
}
