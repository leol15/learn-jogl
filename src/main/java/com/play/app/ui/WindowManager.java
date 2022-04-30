package com.play.app.ui;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.play.app.utils.Func;

import org.lwjgl.glfw.*;
import org.lwjgl.system.CallbackI;

/**
 * manages GLFW callbacks
 * and some basic UI layering
 */
public class WindowManager {

    public enum Layer {
        UI, SCENE,
    }

    public enum CallbackType {
        MouseButton,
        Scroll,
        CursorEnter,
        CursorPos,
        WindowSize,
        Key,
    }

    private static final Layer[] LAYER_ORDER = { Layer.UI, Layer.SCENE };

    public final long window;

    private final Map<Layer, List<GLFWMouseButtonCallbackI>> mouseButtoncallbacks = new TreeMap<>();
    private final Map<Layer, List<GLFWScrollCallbackI>> scrollCallbacks = new TreeMap<>();
    private final Map<Layer, List<GLFWCursorEnterCallbackI>> cursorEnterCallbacks = new TreeMap<>();
    private final Map<Layer, List<GLFWCursorPosCallbackI>> cursorPosCallbacks = new TreeMap<>();
    private final Map<Layer, List<GLFWWindowSizeCallbackI>> windowSizeCallbacks = new TreeMap<>();
    private final Map<Layer, List<GLFWKeyCallbackI>> keyCallbacks = new TreeMap<>();

    public WindowManager(long window) {
        this.window = window;
        glfwSetMouseButtonCallback(window, this::mouseButtonCallback);
        glfwSetScrollCallback(window, this::scrollCallback);
        glfwSetCursorEnterCallback(window, this::cursorEnterCallback);
        glfwSetCursorPosCallback(window, this::cursorPosCallback);
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);
        glfwSetKeyCallback(window, this::keyCallback);

        for (Layer layer : LAYER_ORDER) {
            mouseButtoncallbacks.put(layer, new ArrayList<>());
            scrollCallbacks.put(layer, new ArrayList<>());
            cursorEnterCallbacks.put(layer, new ArrayList<>());
            cursorPosCallbacks.put(layer, new ArrayList<>());
            windowSizeCallbacks.put(layer, new ArrayList<>());
            keyCallbacks.put(layer, new ArrayList<>());
        }
    }

    // key and button have 2 related events
    private boolean stopPropagation = false;

    public void stopPropagation() {
        stopPropagation = true;
    }

    private void resetStopPropagation() {
        stopPropagation = false;
    }

    private boolean shouldStopPropagation() {
        return stopPropagation;
    }

    ////////////////////////////////////
    // should use these to add callback
    ////////////////////////////////////
    public void addMouseButtonCallback(Layer layer, GLFWMouseButtonCallbackI callback) {
        mouseButtoncallbacks.get(layer).add(callback);
    }

    public void addScrollCallback(Layer layer, GLFWScrollCallbackI callback) {
        scrollCallbacks.get(layer).add(callback);
    }

    public void addCursorEnterCallback(Layer layer, GLFWCursorEnterCallbackI callback) {
        cursorEnterCallbacks.get(layer).add(callback);
    }

    public void addCursorPosCallback(Layer layer, GLFWCursorPosCallbackI callback) {
        cursorPosCallbacks.get(layer).add(callback);
    }

    public void addWindowSizeCallback(Layer layer, GLFWWindowSizeCallbackI callback) {
        windowSizeCallbacks.get(layer).add(callback);
    }

    public void addKeyCallback(Layer layer, GLFWKeyCallbackI callback) {
        keyCallbacks.get(layer).add(callback);
    }

    ////////////////////////////////////
    // actaul callbacks set in GLFW
    ////////////////////////////////////
    private void keyCallback(long window, int key, int scancode, int action, int mods) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWKeyCallbackI callback : keyCallbacks.get(layer)) {
                callback.invoke(window, key, scancode, action, mods);
                if (shouldStopPropagation()) {
                    break;
                }
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWMouseButtonCallbackI callback : mouseButtoncallbacks.get(layer)) {
                callback.invoke(window, button, action, mods);
                if (shouldStopPropagation()) {
                    break;
                }
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

    private void cursorPosCallback(long window, double xpos, double ypos) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWCursorPosCallbackI callback : cursorPosCallbacks.get(layer)) {
                callback.invoke(window, xpos, ypos);
                if (shouldStopPropagation())
                    break;
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

    private void cursorEnterCallback(long window, boolean entered) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWCursorEnterCallbackI callback : cursorEnterCallbacks.get(layer)) {
                callback.invoke(window, entered);
                if (shouldStopPropagation())
                    break;
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

    private void scrollCallback(long window, double xoffset, double yoffset) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWScrollCallbackI callback : scrollCallbacks.get(layer)) {
                callback.invoke(window, xoffset, yoffset);
                if (shouldStopPropagation())
                    break;
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

    private void windowSizeCallback(long window, int width, int height) {
        for (final Layer layer : LAYER_ORDER) {
            for (final GLFWWindowSizeCallbackI callback : windowSizeCallbacks.get(layer)) {
                callback.invoke(window, width, height);
                if (shouldStopPropagation())
                    break;
            }
            if (shouldStopPropagation())
                break;
        }
        resetStopPropagation();
    }

}
