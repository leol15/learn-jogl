package com.play.app.utils;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import com.play.app.scene.SceneNode;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.ui.editor.SceneTreeView;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.UIAligner;
import com.play.app.ui.elements.UIAligner.Alignment;
import com.play.app.utils.WindowManager.Layer;

/**
 * Builds a UI for editing view
 */
public class EditorUI {

    // ui elements
    private final SceneTreeView sceneTreeView;
    private final PropertyEditor propertyEditor;
    private SceneNode selectedNode;
    private SceneTreeView selectedSceneTreeView;

    private final ContainerH topRow, sceneNodeEditRow;
    private final ContainerV leftColumnContents, middleColumnContents, rightColumnContents;

    private final UIManager uiManager;
    private final float SIDE_BAR_WIDTH = 400f;

    public EditorUI(UIManager uiManager) {
        this.uiManager = uiManager;

        // 3 panels
        leftColumnContents = new ContainerV(uiManager);
        rightColumnContents = new ContainerV(uiManager);
        middleColumnContents = new ContainerV(uiManager);

        // 2 prebuild ui
        sceneTreeView = new SceneTreeView(uiManager, null, this::selectSceneTreeView);
        propertyEditor = new PropertyEditor(uiManager);

        // left panel
        final UIAligner leftColumnAligner = new UIAligner(uiManager, leftColumnContents);
        leftColumnAligner.setWidth(SIDE_BAR_WIDTH);
        leftColumnAligner.setHorizontalAlignment(Alignment.START);
        leftColumnContents.addChild(sceneTreeView);

        // middle column row
        topRow = new ContainerH(uiManager);
        sceneNodeEditRow = new ContainerH(uiManager);
        middleColumnContents.addChild(topRow);
        middleColumnContents.addChild(sceneNodeEditRow);

        // right column
        final UIAligner rightColumnAligner = new UIAligner(uiManager, rightColumnContents);
        rightColumnAligner.setWidth(uiManager.windowManager.windowSize[0]);
        rightColumnAligner.setHorizontalAlignment(Alignment.END);
        rightColumnContents.addChild(propertyEditor);
        uiManager.roots.add(rightColumnAligner);
        uiManager.windowManager.addWindowSizeCallback(Layer.ALWAYS, (window, w, h) -> {
            rightColumnAligner.setWidth(w);
        });

        // putting it together
        final ContainerH leftTopContainer = new ContainerH(uiManager);
        uiManager.roots.add(leftTopContainer);
        leftTopContainer.addChild(leftColumnAligner);
        leftTopContainer.addChild(middleColumnContents);

        // intialize
        setupTopRowButtons();
        setupSceneNodeEditRow();
    }

    private void setupTopRowButtons() {
        final Button polygonModeButton = new Button(uiManager, "Polygon Mode");
        final int[] toggleState = new int[1];
        final int[] polygonMode = { GL_LINE, GL_FILL, GL_POINT };
        polygonModeButton.onClickEvent.addListener(e -> {
            glPolygonMode(GL_FRONT_AND_BACK, polygonMode[toggleState[0]]);
            toggleState[0] = (toggleState[0] + 1) % polygonMode.length;
        });

        topRow.addChild(polygonModeButton);
    }

    /**
     * Adds button to create child nodes
     */
    private void setupSceneNodeEditRow() {
        SceneNodeFactory.setupCreateButtons(uiManager, sceneNodeEditRow, this::addChildSceneNode);
        // final Button createCubeBtn = new Button(uiManager, "+ Cube");
        // createCubeBtn.onClickEvent.addListener(btn -> {
        //     addChildSceneNode(SceneNodeFactory.createCube());
        // });
        // sceneNodeEditRow.addChild(createCubeBtn);
    }

    private void addChildSceneNode(SceneNode node) {
        if (selectedNode != null) {
            selectedNode.addChild(node);
            if (selectedSceneTreeView != null) {
                selectedSceneTreeView.sceneNodeUpdated();
            } else {
                sceneTreeView.sceneNodeUpdated();
            }
        }
    }

    /**
     * start editing a SceneNode
     */
    public void selectSceneNode(SceneNode newSceneNode) {
        // clear selected state
        propertyEditor.clear();
        if (selectedSceneTreeView != null) {
            selectedSceneTreeView.defocus();
        }
        if (selectedNode != null) {
            selectedNode.deselect(propertyEditor);
        }

        if (newSceneNode != null) {
            selectedNode = newSceneNode;
            selectedNode.select(propertyEditor);
        }
    }

    private void selectSceneTreeView(SceneTreeView sceneTreeView) {
        selectSceneNode(sceneTreeView.getSceneNode());
        selectedSceneTreeView = sceneTreeView;
        sceneTreeView.focus();
    }

    public void setRootNode(SceneNode root) {
        sceneTreeView.setSceneNode(root);
    }

    public void addTopRowElement(UIElement el) {
        topRow.addChild(el);
    }
}
