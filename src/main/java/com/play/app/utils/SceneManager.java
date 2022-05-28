package com.play.app.utils;

import static org.lwjgl.opengl.GL11.*;

import com.play.app.geometry.Ray;
import com.play.app.scene.*;
import com.play.app.scene.lights.LightUBO;
import com.play.app.ui.Button;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;

/**
 * manages a window, a camera, a root scene node
 */
public class SceneManager {
    private SceneNode root;
    private final CameraControl cam;
    private final WindowManager windowManager;

    private final PropertyEditor editor;
    private final Matrix4f identity = new Matrix4f();

    private final Button saveButton, loadButton;

    private SceneNode selectedNode;

    public SceneManager(WindowManager windowManager, SceneNode root, CameraControl cam) {
        this.root = root;
        this.cam = cam;
        this.windowManager = windowManager;

        // static inits
        LightUBO.getInstance();
        CameraUBO.getInstance();

        // dev
        saveButton = new Button(windowManager, 500, 50, "Save");
        loadButton = new Button(windowManager, 600, 50, "Load");
        setupSaveLoadButtons();

        // set up edit area
        editor = new PropertyEditor(windowManager);
        // select action in scene
        configureSelect();
        // set background color
        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
    }

    public void render() {
        // prep
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        LightUBO.getInstance().addAllLights(root);
        // draw
        // since the scene has tranparent things, we need to draw transparent things last
        CONST.drawTransparent = false;
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        root.draw(identity);

        // draw transparent things
        CONST.drawTransparent = true;
        glDepthMask(false);
        glEnable(GL_BLEND);
        // TODO better blending function
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        // glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);
        root.draw(identity);

        glDepthMask(true);
        cam.draw();
        // draw UI with no depth info
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        saveButton.show();
        loadButton.show();
        editor.show();
    }

    private void configureSelect() {
        windowManager.addCharCallback(Layer.SCENE, (window2, character) -> {
            if (character == 'a') {
                windowManager.stopPropagation();
                final Ray ray = cam.getRay(windowManager.lastMousePos[0], windowManager.lastMousePos[1]);

                // select
                SceneNode node = root.castRay(ray);
                editor.clear();
                if (selectedNode != null) {
                    selectedNode.deselect(editor);
                    editor.clear();
                }
                if (node != null) {
                    selectedNode = node;
                    selectedNode.select(editor);
                }
            } else if (character == 'f') {
                // focus
                if (selectedNode != null) {
                    final Vector3f position = new Vector3f();
                    cam.focusOn(selectedNode.getPosition(position));
                }
            }
        });

    }

    private void setupSaveLoadButtons() {
        final WorldSerializer worldSerializer = new WorldSerializer(cam);
        saveButton.setAction(() -> {
            worldSerializer.save("test-scene.yaml", root);
        });
        loadButton.setAction(() -> {
            this.root = worldSerializer.load("test-scene.yaml");
        });
    }

}
