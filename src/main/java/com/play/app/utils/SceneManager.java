package com.play.app.utils;

import static org.lwjgl.opengl.GL11.*;

import com.play.app.geometry.Ray;
import com.play.app.scene.*;
import com.play.app.scene.camera.*;
import com.play.app.scene.lights.LightUBO;
import com.play.app.ui.Button;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WindowManager.Layer;

import org.joml.*;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * manages a window, a camera, a root scene node
 */
@Log4j2
public class SceneManager {
    private SceneNode root;
    // private final CameraControl cam;
    private final WindowManager windowManager;
    @Getter
    private final CameraManager cameraManager;

    private final PropertyEditor editor;
    private final Matrix4f identity = new Matrix4f();

    private final Button saveButton, loadButton;

    private SceneNode selectedNode;

    public SceneManager(WindowManager windowManager, SceneNode root) {
        this.root = root;
        // this.cam = cam;
        this.windowManager = windowManager;
        cameraManager = new CameraManager(windowManager, this);

        // static inits
        LightUBO.getInstance();
        CameraUBO.getInstance();

        // save/load
        saveButton = new Button(windowManager, 500, 50, "Save");
        loadButton = new Button(windowManager, 600, 50, "Load");
        setupSaveLoadButtons();

        // set up edit area
        editor = new PropertyEditor(windowManager);

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
        cameraManager.show();

        // draw UI with no depth info
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        saveButton.show();
        loadButton.show();
        editor.show();
    }

    public void selectSceneNode(Ray ray) {
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
    }

    public boolean focusSelect() {
        if (selectedNode != null) {
            cameraManager.getCamera().focusOn(selectedNode.getPosition(new Vector3f()));
            return true;
        } else {
            return false;
        }
    }

    private void setupSaveLoadButtons() {
        final WorldSerializer worldSerializer = new WorldSerializer(cameraManager.getCamera());
        saveButton.setAction(() -> {
            worldSerializer.save("test-scene.yaml", root);
        });
        loadButton.setAction(() -> {
            this.root = worldSerializer.load("test-scene.yaml");
        });
    }

}
