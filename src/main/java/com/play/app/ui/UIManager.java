package com.play.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.play.app.basics.Drawable;
import com.play.app.graphics.ShaderProgram;
import com.play.app.ui.elements.UIElement;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;
import com.play.app.utils.WindowManager.Layer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import lombok.extern.log4j.Log4j2;

/**
 * Handle alllllll the UIs
 * Event Handling
 */
@Log4j2
public class UIManager implements Drawable {

    public final ShaderProgram uiShader, textShader;
    public final WindowManager windowManager;
    public final List<UIElement> roots = new ArrayList<>();

    private final Matrix4f UItoGL = new Matrix4f();

    public UIManager(WindowManager windowManager) {
        this.windowManager = windowManager;
        uiShader = ShaderUtils.getShader("UI");
        textShader = ShaderUtils.getShader("Text");
        windowManager.windowSizeEvent.addListener(s -> computeScreeProjection(s[0], s[1]));
        computeScreeProjection(windowManager.windowSize[0], windowManager.windowSize[1]);

        // add events
        windowManager.addMouseButtonCallback(Layer.UI,
                (window, button, action, mods) -> onMouseButton(button, action, mods));
        windowManager.addScrollCallback(Layer.UI, (window, xoffset, yoffset) -> onScroll(xoffset, yoffset));
        windowManager.addCursorEnterCallback(Layer.UI, (window, entered) -> onMouseEnter(entered));
        windowManager.addCursorPosCallback(Layer.UI, (window, xpos, ypos) -> onMouseMove(xpos, ypos));
        windowManager.addWindowSizeCallback(Layer.UI, (window, width, height) -> onWindowResize(width, height));
        windowManager.addKeyCallback(Layer.UI,
                (window, key, scancode, action, mods) -> onKey(key, scancode, action, mods));
        windowManager.addCharCallback(Layer.UI, (window, c) -> onChar(c));
    }

    //////////////////////////////////////////////
    // provide a common shader for all UIElements
    //////////////////////////////////////////////

    private void computeScreeProjection(float screenW, float screenH) {
        UItoGL.identity();
        // to gl space
        UItoGL.scale(2f / screenW, -2f / screenH, 1);
        UItoGL.translate(-screenW / 2, -screenH / 2, 0);
        uiShader.uniformMatrix4fv("UItoGL", UItoGL);
        textShader.uniformMatrix4fv("UItoGL", UItoGL);
    }

    @Override
    public void draw() {
        // alpha blending for UI, TODO not implemented
        GL11.glDepthFunc(GL11.GL_ALWAYS);

        // GL11.glEnable(GL11.GL_BLEND);
        // GL45.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
        //         GL11.GL_ONE_MINUS_SRC_ALPHA);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_ALPHA_SATURATE);
        // GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        roots.forEach(e -> e.draw(new Matrix4f()));

        GL11.glDepthFunc(GL11.GL_LESS);
    }

    ///////////////////////////////////
    // ui elements gets window event from here
    ///////////////////////////////////

    public boolean onChar(int c) {
        for (final UIElement root : roots) {
            if (root.onChar(c) != null) {
                windowManager.stopPropagation();
                return true;
            }
        }
        return false;
    }

    private UIElement prevOnKey;

    public boolean onKey(int key, int scancode, int action, int mods) {
        ButtonAction keyAction = ButtonAction.fromInt(action);

        if (keyAction != ButtonAction.PRESS && prevOnKey != null) {
            // could be handled
            prevOnKey.onKey(key, scancode, keyAction, mods);
            prevOnKey = null;
            windowManager.stopPropagation();
            return true;
        }

        for (final UIElement root : roots) {
            // clicked this element
            final UIElement handler = root.onKey(key, scancode, keyAction, mods);
            if (handler != null) {
                if (keyAction == ButtonAction.PRESS) {
                    prevOnKey = handler;
                }
                windowManager.stopPropagation();
                return true;
            }
        }

        return false;
    }

    private UIElement prevOnMouseButton = null;

    public boolean onMouseButton(int button, int action, int mods) {
        final MouseButtonType buttonType = MouseButtonType.fromInt(button);
        final ButtonAction buttonAction = ButtonAction.fromInt(action);
        // traverse the tree and find the element
        final float buttonX = windowManager.lastMousePos[0];
        final float buttonY = windowManager.lastMousePos[1];

        // see if this is captured
        if (prevOnMouseButton != null && buttonAction != ButtonAction.PRESS) {
            // TODO, how to give th relative positions to the element? not -1, -1
            prevOnMouseButton.onMouseButton(buttonType, buttonAction, mods, -1, -1);
            prevOnMouseButton = null;
            windowManager.stopPropagation();
            return true;
        }

        for (final UIElement root : roots) {
            if (root.contains(buttonX, buttonY)) {
                // clicked this element
                final UIElement handler = root.onMouseButton(buttonType, buttonAction, mods, buttonX, buttonY);
                if (handler != null) {
                    if (buttonAction == ButtonAction.PRESS) {
                        prevOnMouseButton = handler;
                    }
                    windowManager.stopPropagation();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onMouseEnter(boolean entered) {
        return false;
    }

    // shoudl this one be stopped?
    public boolean onMouseMove(double xpos, double ypos) {
        for (final UIElement root : roots) {
            if (root.onMouseMove(xpos, ypos) != null) {
                windowManager.stopPropagation();
                return true;
            }
        }
        return false;
    }

    public boolean onScroll(double xoffset, double yoffset) {
        // traverse the tree and find the element
        final float buttonX = windowManager.lastMousePos[0];
        final float buttonY = windowManager.lastMousePos[1];

        for (final UIElement root : roots) {
            if (!root.contains(buttonX, buttonY)) {
                continue;
            }
            if (root.onScroll(buttonX, buttonY, xoffset, yoffset) != null) {
                windowManager.stopPropagation();
                return true;
            }
        }
        return false;
    }

    public void onWindowResize(int width, int height) {
        for (final UIElement root : roots) {
            root.onWindowResize(width, height);
        }
    }

}
