package com.play.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import com.play.app.graphics.UnitGeometries;
import com.play.app.utils.Func;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class App {

	// The window handle
	private long window;

	// int WINDOW_WIDTH = 1800;
	// int WINDOW_HEIGHT = 1200;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();

		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		UnitGeometries.initStatic();

		Func.p("running program");

		// run the main class
		// new GameTutorial(window);
		// new Rendering(window);
		// new Batching(window);
		// new BatchRendering(window);
		// new UseTexture(window);
		// new Input(window);
		// new DrawLines(window);
		new DrawAScene(window);
		// new SelectObject(window);

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_SAMPLES, 4);
		// glEnable(GL_MULTISAMPLE);

		// full size
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Create the window
		window = glfwCreateWindow(vidmode.width(), vidmode.height(), "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		glfwMakeContextCurrent(window);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		/* Enable blending */
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);

		glEnable(GL_DEPTH_TEST);

		// lines
		glEnable(GL_LINE_SMOOTH);
		glEnable(GL_LINE_STIPPLE);
		glLineWidth(5); // does not work

		glPointSize(3);

	}

	public static void main(String[] args) {
		new App().run();
		// new GameTutorial();
	}

}