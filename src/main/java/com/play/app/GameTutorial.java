package com.play.app;

// import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.*;

public class GameTutorial {

    public GameTutorial(long window) {

        while (!glfwWindowShouldClose(window)) {
            // loop
            // double time = glfwGetTime();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }

}
