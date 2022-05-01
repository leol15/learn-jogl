package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.awt.Color;

import com.play.app.graphics.Text;
import com.play.app.ui.Button;
import com.play.app.ui.WindowManager;

public class Input {

    public Input(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        WindowManager windowManager = new WindowManager(window);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 200, 200);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        Button button = new Button(windowManager, 600, 300, 100f, 100f);
        button.setColor(Color.GREEN);
        Button button2 = new Button(windowManager, 100, 100, 100f, 100f);
        Button button3 = new Button(windowManager, 300, 300, "Yoooo");

        int[] tmp = new int[1];
        button.setAction(() -> {
            System.out.println("Hello world button");
            tmp[0]++;
            button.setColor(tmp[0] % 2 == 0 ? Color.RED : Color.GREEN);
        });

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            double time = glfwGetTime();

            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 200, 200);
            fpsCounter.draw();
            previousTime = time;

            button.show();
            button2.show();
            button3.show();

            glfwPollEvents();
        }
    }

}
