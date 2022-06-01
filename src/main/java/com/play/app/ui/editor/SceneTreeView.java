package com.play.app.ui.editor;

import com.play.app.basics.Event;
import com.play.app.scene.SceneNode;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.DropdownList;
import com.play.app.ui.elements.Padding;
import com.play.app.ui.elements.UIText;
import com.play.app.ui.elements.UITransformer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Draws a screen graph as a tree
 */
@Log4j2
public class SceneTreeView extends AbstractUIWrapper {

    @Getter
    private SceneNode sceneNode;
    // TODO. who is the receiver of this event? rethink design
    public final Event<SceneTreeView> sceneNodeClickEvent;

    private float leftPadding = 20;
    private final UIText sceneNodeLabel;
    private final UIText sceneObjectLabel;
    // to add padding
    private final ContainerV container;
    private final UITransformer dropdownTransformer;
    private final Padding listDivider;
    private final DropdownList dropdownList;
    private final Button expandChildrenButton;

    public SceneTreeView(UIManager uiManager) {
        this(uiManager, null);
    }

    public SceneTreeView(UIManager uiManager, SceneNode node) {
        this(uiManager, node, true);
    }

    private SceneTreeView(UIManager uiManager, SceneNode node, boolean isRoot) {
        super(uiManager);
        sceneNode = node;

        sceneNodeLabel = new UIText(uiManager);
        sceneObjectLabel = new UIText(uiManager);
        dropdownList = new DropdownList(uiManager);
        dropdownTransformer = new UITransformer(uiManager, dropdownList);
        expandChildrenButton = new Button(uiManager);

        container = new ContainerV(uiManager);

        final ContainerH nodeLabelRow = new ContainerH(uiManager);
        final Button nodeLabelButton = new Button(uiManager, "+");
        nodeLabelButton.padding = 0;
        nodeLabelRow.addChild(sceneNodeLabel);
        nodeLabelRow.addChild(nodeLabelButton);
        nodeLabelRow.addChild(expandChildrenButton);

        container.addChild(nodeLabelRow);
        container.addChild(sceneObjectLabel);
        listDivider = new Padding(uiManager, 100, 6);
        container.addChild(listDivider);

        container.addChild(dropdownTransformer);
        dropdownTransformer.translation.x = leftPadding;

        dropdownList.setShowButton(false);
        expandChildrenButton.onClickEvent.addListener(this::toggleChildren);
        expandChildrenButton.textColor().set(1, 1, 0);
        expandChildrenButton.padding = 0;

        // events
        sceneNodeClickEvent = new Event<SceneTreeView>(this);
        nodeLabelButton.onClickEvent.addListener(e -> sceneNodeClickEvent.fire());

        sceneNodeUpdated();
    }

    public void setSceneNode(SceneNode node) {
        sceneNode = node;
        sceneNodeUpdated();
    }

    private void sceneNodeUpdated() {
        if (sceneNode != null) {
            sceneNodeLabel.setText("SceneNode Name");
            sceneObjectLabel.setText("SceneObject Name");
            dropdownList.clear();
            sceneNode.getChildren().forEach(n -> {
                dropdownList.addItem(new SceneTreeView(uiManager, n, false));
            });
        } else {
            sceneNodeLabel.setText("SceneNode Null");
            sceneObjectLabel.setText("Null");
            dropdownList.clear();
        }
        toggleUpdated();
    }

    private void toggleChildren(Button b) {
        dropdownList.toggle();
        toggleUpdated();
    }

    private void toggleUpdated() {
        if (sceneNode == null) {
            expandChildrenButton.setLabel("x");
            listDivider.bgColor.set(0.6, 0.6, 0.6);
        } else {
            final int numChildren = sceneNode.getNumChildren();
            if (dropdownList.isExpanded()) {
                expandChildrenButton.setLabel(String.format("v [%d]", numChildren));
                listDivider.bgColor.set(0, 0.3, 0.3);
            } else {
                expandChildrenButton.setLabel(String.format("< [%d]", numChildren));
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
