package com.play.app;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class GameTutorial {

    public GameTutorial(long window) {
        


        while (!glfwWindowShouldClose(window)) {
            // loop
            double time =glfwGetTime();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }

}
