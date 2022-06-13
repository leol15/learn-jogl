package com.play.app.utils;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import com.play.app.basics.Event;
import com.play.app.scene.SceneNode;
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
    public final SceneTreeView sceneTreeView;
    public final PropertyEditor propertyEditor;
    public final Event<SceneNode> treeViewSelectNodeEvent;

    public final ContainerH topRow;
    public final ContainerV rightColumnContents;
    public final ContainerV leftColumnContents;

    private final UIManager uiManager;
    private final float SIDE_BAR_WIDTH = 400f;

    public EditorUI(UIManager uiManager) {
        this.uiManager = uiManager;

        // 3 panels
        topRow = new ContainerH(uiManager);
        leftColumnContents = new ContainerV(uiManager);
        rightColumnContents = new ContainerV(uiManager);

        // 2 prebuild ui
        sceneTreeView = new SceneTreeView(uiManager, null, this::treeViewClickedNode);
        leftColumnContents.addChild(sceneTreeView);

        propertyEditor = new PropertyEditor(uiManager);
        rightColumnContents.addChild(propertyEditor);

        // start building UI
        final ContainerH leftTopContainer = new ContainerH(uiManager);
        uiManager.roots.add(leftTopContainer);

        // left panel
        final UIAligner leftColumnAligner = leftTopContainer.addChild(new UIAligner(uiManager, leftColumnContents));
        leftColumnAligner.setWidth(SIDE_BAR_WIDTH);
        leftColumnAligner.setHorizontalAlignment(Alignment.START);

        // top row
        leftTopContainer.addChild(topRow);

        // right column
        final UIAligner rightColumnAligner = new UIAligner(uiManager, rightColumnContents);
        rightColumnAligner.setWidth(uiManager.windowManager.windowSize[0]);
        rightColumnAligner.setHorizontalAlignment(Alignment.END);
        uiManager.roots.add(rightColumnAligner);
        uiManager.windowManager.addWindowSizeCallback(Layer.ALWAYS, (window, w, h) -> {
            rightColumnAligner.setWidth(w);
        });

        // events
        treeViewSelectNodeEvent = new Event<SceneNode>(null);

        // intialize
        setupTopRowButtons();

    }

    private void treeViewClickedNode(SceneNode node) {
        treeViewSelectNodeEvent.fire(node);
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

}
