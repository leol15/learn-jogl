package com.play.app.utils;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import com.play.app.geometry.Ray;
import com.play.app.scene.SceneNode;
import com.play.app.scene.camera.CameraManager;
import com.play.app.scene.camera.CameraUBO;
import com.play.app.scene.lights.LightUBO;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.elements.ContainerH;
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

    private final PropertyEditor propertyEditor;

    private final Matrix4f identity = new Matrix4f();

    private SceneNode selectedNode;

    public SceneManager(WindowManager windowManager, SceneNode root) {
        this.root = root;
        // this.cam = cam;
        this.windowManager = windowManager;
        this.uiManager = new UIManager(windowManager);
        cameraManager = new CameraManager(windowManager, this);

        // static inits
        LightUBO.getInstance();
        CameraUBO.getInstance();

        // set up UI
        propertyEditor = new PropertyEditor(uiManager);
        UIElement topRow = createTopButtonRow();
        final ContainerH uiRoot = new ContainerH(uiManager);
        uiRoot.addChild(propertyEditor);
        uiRoot.addChild(topRow);
        uiManager.roots.add(uiRoot);

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

        uiManager.draw();
    }

    public void selectSceneNode(Ray ray) {
        // select
        SceneNode node = root.castRay(ray);
        propertyEditor.clear();
        if (selectedNode != null) {
            selectedNode.deselect(propertyEditor);
            propertyEditor.clear();
        }
        if (node != null) {
            selectedNode = node;
            selectedNode.select(propertyEditor);
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

    private UIElement createTopButtonRow() {
        // save/load
        final ContainerH row = new ContainerH(uiManager);
        final Button saveButton = new Button(uiManager, "Save");
        final Button loadButton = new Button(uiManager, "Load");
        final WorldSerializer worldSerializer = new WorldSerializer(cameraManager.getCamera());
        saveButton.onClickEvent.addListener(e -> {
            worldSerializer.save("test-scene.yaml", root);
        });
        loadButton.onClickEvent.addListener(e -> {
            this.root = worldSerializer.load("test-scene.yaml");
        });

        final Button switchControlsButton = new Button(uiManager, "Switch View");
        switchControlsButton.onClickEvent.addListener(e -> {
            if (cameraManager.getActiveController() == cameraManager.EDITOR_CAMERA_CONTROL) {
                cameraManager.setControll(cameraManager.FIRST_PRESON_CAMERA_CONTROL);
            } else {
                cameraManager.setControll(cameraManager.EDITOR_CAMERA_CONTROL);
            }
        });

        final Button polygonModeButton = new Button(uiManager, "Polygon Mode");
        final int[] toggleState = new int[1];
        final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        polygonModeButton.onClickEvent.addListener(e -> {
            glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
            toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        });

        row.addChild(saveButton);
        row.addChild(loadButton);
        row.addChild(switchControlsButton);
        row.addChild(polygonModeButton);

        return row;
    }

}
