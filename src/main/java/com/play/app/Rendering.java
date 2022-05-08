package com.play.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.nio.*;

import com.play.app.graphics.*;
import com.play.app.scene.CameraControl;
import com.play.app.utils.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

public class Rendering {

    public Rendering(long window) {
        glEnable(GL_DEPTH_TEST);
        // glDepthFunc(GL_LESS);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // setup shader
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.vert", GL_VERTEX_SHADER);
        shaderProgram.loadShaderFromPath("resources/shaders/Simple.frag", GL_FRAGMENT_SHADER);
        shaderProgram.linkProgram();

        // set uniform locations
        final WindowManager windowManager = new WindowManager(window);
        final CameraControl camera = new CameraControl(windowManager);
        Matrix4f model = new Matrix4f();
        FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
        model.get(fbModel);
        shaderProgram.uniformMatrix4fv("model", fbModel);

        // ShaderProgram planeShader = new ShaderProgram()
        //         .withShader("resources/shaders/Simple3D.vert", GL_VERTEX_SHADER)
        //         .withShader("resources/shaders/Simple3D.geom", GL_GEOMETRY_SHADER)
        //         .withShader("resources/shaders/Simple3D.frag", GL_FRAGMENT_SHADER)
        //         .linkProgram();

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        model.scale(0.6f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewAndProjection(shaderProgram);
            model.rotate(0.01f, new Vector3f(1, 1, 1));
            model.get(fbModel);

            shaderProgram.uniformMatrix4fv(CONST.MODEL_MATRIX, fbModel);

            shaderProgram.useProgram();
            UnitGeometries.drawCube();

            camera.draw();

            glfwPollEvents();
        }
    }
}
