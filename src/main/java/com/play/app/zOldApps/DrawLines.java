package com.play.app.zOldApps;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.nio.FloatBuffer;

import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.VAO;
import com.play.app.scene.camera.CameraManager;
import com.play.app.utils.CONST;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class DrawLines {
    public DrawLines(long window) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);

        final VAO lineVAO = new VAO();
        FloatBuffer positions = BufferUtils.createFloatBuffer(3 * 4);
        positions.put(0).put(0).put(0);
        positions.put(0.5f).put(0.5f).put(0);
        positions.put(0).put(0.5f).put(0);
        positions.put(0).put(0.5f).put(0.5f);
        positions.flip();
        lineVAO.bufferData(CONST.VERT_IN_POSITION, positions);
        lineVAO.setDrawFunction(() -> glDrawArrays(GL_LINE_LOOP, 0, 4));

        ShaderProgram lineShader = ShaderUtils.getShader("Line");

        CameraManager cam = new CameraManager(windowManager);

        // ui
        // final Button togglePolygonMode = new Button(windowManager, 0, 50, "Toggle Polygon Mode");
        // final int[] toggleState = new int[1];
        // final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        // togglePolygonMode.setAction(() -> {
        //     glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
        //     toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        // });

        // Text fpsCounter = new Text(windowManager, "FPS: 1", 0, 0);
        // fpsCounter.setColor(Color.RED);
        // double previousTime = 0;

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // float fps = (float) (1 / (time - previousTime));
            // fpsCounter.setText(String.format("FPS: %.2f", fps), 0, 0);
            // fpsCounter.draw();
            // previousTime = time;

            lineShader.uniform4f("color", new Vector4f(0, 0, 1, 1));
            lineShader.useProgram();
            UnitGeometries.drawCircle();
            lineShader.uniform4f("color", new Vector4f(1, 0, 1, 1));
            lineShader.useProgram();
            lineVAO.draw();
            lineShader.unuseProgram();

            // simple3DShader.useProgram();
            // UnitGeometries.drawCube();
            // simple3DShader.unuseProgram();

            cam.show();

            // togglePolygonMode.show();
            glfwPollEvents();
        }
    }
}
