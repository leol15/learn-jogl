package com.play.app.ui.editor;

import com.play.app.basics.Listener;
import com.play.app.scene.SceneNode;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.DropdownList;
import com.play.app.ui.elements.Padding;
import com.play.app.ui.elements.TextInput;
import com.play.app.ui.elements.UIText;
import com.play.app.ui.elements.UITransformer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Draws a screen graph as a tree
 */
@Log4j2
public class SceneTreeView extends AbstractUIWrapper {

    private static final String EDIT_NODE_BUTTON_TEXT = "o";

    @Getter
    private SceneNode sceneNode;

    private final TextInput sceneNodeLabel;
    private final UIText sceneObjectType;
    private final DropdownList childrenDropdown;
    private final Button expandChildrenButton;
    private final Button focusNodeBtn;

    private float leftPadding = 20;
    // to add padding
    private final ContainerV container;
    private final UITransformer dropdownTransformer;
    private final Padding listDivider;

    private Listener<SceneTreeView> focusNodeListener;

    public SceneTreeView(UIManager uiManager) {
        this(uiManager, null, null);
    }

    public SceneTreeView(UIManager uiManager, SceneNode node) {
        this(uiManager, node, null);
    }

    public SceneTreeView(UIManager uiManager, SceneNode node, Listener<SceneTreeView> sceneNodeListener) {
        super(uiManager);
        sceneNode = node;
        this.focusNodeListener = sceneNodeListener;

        sceneNodeLabel = new TextInput(uiManager);
        sceneNodeLabel.setWidth(200);
        sceneNodeLabel.setPadding(0);
        sceneObjectType = new UIText(uiManager);
        childrenDropdown = new DropdownList(uiManager);
        dropdownTransformer = new UITransformer(uiManager, childrenDropdown);
        expandChildrenButton = new Button(uiManager);

        container = new ContainerV(uiManager);

        final ContainerH nodeLabelRow = new ContainerH(uiManager);
        focusNodeBtn = new Button(uiManager, EDIT_NODE_BUTTON_TEXT);
        focusNodeBtn.padding = 0;
        nodeLabelRow.addChild(sceneNodeLabel);
        nodeLabelRow.addChild(focusNodeBtn);
        nodeLabelRow.addChild(expandChildrenButton);

        container.addChild(nodeLabelRow);
        container.addChild(sceneObjectType);
        listDivider = new Padding(uiManager, 100, 6);
        container.addChild(listDivider);

        container.addChild(dropdownTransformer);
        dropdownTransformer.translation.x = leftPadding;

        childrenDropdown.setShowButton(false);
        expandChildrenButton.onClickEvent.addListener(this::toggleChildren);
        expandChildrenButton.textColor().set(1, 1, 0);
        expandChildrenButton.padding = 0;

        // events
        focusNodeBtn.onClickEvent.addListener(e -> sceneNodeClicked());
        sceneNodeLabel.changeEvent.addListener(e -> updateNodeName(e.getAsString()));

        sceneNodeUpdated();
    }

    public void setSceneNode(SceneNode node) {
        sceneNode = node;
        sceneNodeUpdated();
    }

    /**
     * UI indication of it being edited 
     */
    public void focus() {
        focusNodeBtn.setDrawBackground(false);
    }

    public void defocus() {
        focusNodeBtn.setDrawBackground(true);
    }

    private void sceneNodeClicked() {
        if (focusNodeListener != null) {
            focusNodeListener.hey(this);
        }
    }

    private void updateNodeName(String name) {
        if (sceneNode != null) {
            sceneNode.setName(name);
        }
    }

    /**
     * A change in SN happened, need to rebuild view
     */
    public void sceneNodeUpdated() {
        childrenDropdown.clear();
        if (sceneNode != null) {
            sceneNode.getChildren().forEach(n -> {
                childrenDropdown.addItem(new SceneTreeView(uiManager, n, focusNodeListener));
            });
        }
        configureLabels();
        toggleUpdated();
    }

    private void configureLabels() {
        if (sceneNode != null) {
            sceneNodeLabel.setContent(sceneNode.getName());
            String sceneObjectName = null;
            if (sceneNode.getSceneObject() != null) {
                sceneObjectName = sceneNode.getSceneObject().getClass().getSimpleName();
            }
            sceneObjectType.setText(String.valueOf(sceneObjectName));
        } else {
            sceneNodeLabel.setContent("Empty");
            sceneObjectType.setText("Null");
        }
    }

    private void toggleChildren(Button b) {
        childrenDropdown.toggle();
        toggleUpdated();
    }

    private void toggleUpdated() {
        if (sceneNode == null) {
            expandChildrenButton.setLabel("x");
            listDivider.bgColor.set(0.6, 0.6, 0.6);
        } else {
            final int numChildren = sceneNode.getNumChildren();
            if (childrenDropdown.isExpanded()) {
                expandChildrenButton.setLabel(String.format("- [%d]", numChildren));
                listDivider.bgColor.set(0, 0.3, 0.3);
            } else {
                expandChildrenButton.setLabel(String.format("+ [%d]", numChildren));
                if (numChildren == 0) {
                    listDivider.bgColor.set(0.6, 0.2, 0.2);
                } else {
                    listDivider.bgColor.set(0, 0.7, 0.7);
                }
            }
        }
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
