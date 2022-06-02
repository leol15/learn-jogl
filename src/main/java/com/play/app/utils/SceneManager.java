package com.play.app.utils;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import com.play.app.geometry.Ray;
import com.play.app.scene.SceneNode;
import com.play.app.scene.camera.CameraManager;
import com.play.app.scene.camera.CameraUBO;
import com.play.app.scene.lights.LightUBO;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.Button;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * manages a window, a camera, a root scene node
 */
@Log4j2
public class SceneManager {
    private SceneNode root;

    private final WindowManager windowManager;
    public final UIManager uiManager;
    @Getter
    private final CameraManager cameraManager;

    private final EditorUI editorUI;
    private SceneNode selectedNode;

    private final Matrix4f identity = new Matrix4f();

    public SceneManager(WindowManager windowManager, SceneNode root) {
        this.root = root;
        // this.cam = cam;
        this.windowManager = windowManager;
        this.uiManager = new UIManager(windowManager);
        cameraManager = new CameraManager(windowManager, this);
        editorUI = new EditorUI(uiManager);

        // UBO static inits
        LightUBO.getInstance();
        CameraUBO.getInstance();

        // some editor buttons
        createTopButtonRow();
        editorUI.treeViewSelectNodeEvent.addListener(this::selectSceneNode);

        // set background color
        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);
    }

    public void render() {
        // prep
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        LightUBO.getInstance().addAllLights(root);

        // draw scene
        renderNodeWithTransparency();

        // camera might draw
        cameraManager.show();

        // draw UI with no depth info
        uiManager.draw();
    }

    // select node via ray (click in scene)
    public void selectSceneNode(Ray ray) {
        selectSceneNode(root.castRay(ray));
    }

    private void selectSceneNode(SceneNode newSceneNode) {
        editorUI.propertyEditor.clear();
        if (selectedNode != null) {
            selectedNode.deselect(editorUI.propertyEditor);
            editorUI.propertyEditor.clear();
        }
        if (newSceneNode != null) {
            selectedNode = newSceneNode;
            selectedNode.select(editorUI.propertyEditor);
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

    private void renderNodeWithTransparency() {
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
    }

    private void createTopButtonRow() {
        // save/load
        final Button saveButton = new Button(uiManager, "Save");
        final Button loadButton = new Button(uiManager, "Load");
        saveButton.onClickEvent.addListener(e -> saveScene());
        loadButton.onClickEvent.addListener(e -> loadScene());

        // switch control
        final Button switchControlsButton = new Button(uiManager, "Switch View");
        switchControlsButton.onClickEvent.addListener(e -> switchCameraControl());

        editorUI.topRow.addChild(saveButton);
        editorUI.topRow.addChild(loadButton);
        editorUI.topRow.addChild(switchControlsButton);
    }

    private void loadScene() {
        final WorldSerializer worldSerializer = new WorldSerializer(cameraManager.getCamera());
        root = worldSerializer.load("test-scene.yaml");
        editorUI.sceneTreeView.setSceneNode(this.root);
    }

    private void saveScene() {
        final WorldSerializer worldSerializer = new WorldSerializer(cameraManager.getCamera());
        worldSerializer.save("test-scene.yaml", root);
    }

    private void switchCameraControl() {
        if (cameraManager.getActiveController() == cameraManager.EDITOR_CAMERA_CONTROL) {
            cameraManager.setControll(cameraManager.FIRST_PRESON_CAMERA_CONTROL);
        } else {
            cameraManager.setControll(cameraManager.EDITOR_CAMERA_CONTROL);
        }
    }

}
