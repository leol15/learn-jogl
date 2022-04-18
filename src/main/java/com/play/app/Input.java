package com.play.app;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.awt.Color;
import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.play.app.ui.*;

public class Input {

    public Input(long window) {

        /* Enable blending */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        Button button = new Button(window, 0, 0, 100f, 100f);
        button.setColor(Color.BLACK);
        Button button2 = new Button(window, 100, 100, 100f, 100f);
        Button button3 = new Button(window, 300, 300, "Yoooo");

        int[] tmp = new int[1];
        button.setAction(() -> {
            System.out.println("Hello world button");
            tmp[0]++;
            button.setColor(tmp[0] % 2 == 0 ? Color.RED : Color.GREEN);
        });

		glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time =glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            button.show();
            button2.show();
            button3.show();

            glfwPollEvents();
        }
    }


}
