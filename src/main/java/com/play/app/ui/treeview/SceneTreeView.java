package com.play.app.ui.treeview;

import java.util.*;

import com.play.app.graphics.Text;
import com.play.app.scene.SceneNode;
import com.play.app.ui.*;
import com.play.app.utils.WindowManager;

public class SceneTreeView extends UIBase {
    // recursive, each one represents a SceneNode (NodeName, SceneObject)

    public static final int WIDTH = 400;
    private List<SceneTreeView> children = new ArrayList<>();

    private final Text nodeName;
    private final Text sceneNodeType;
    private final Button expandButton;
    private boolean expanded = true;

    private final int LEVEL_PADDING = 10;
    private int depth;

    public SceneTreeView(WindowManager windowManager, SceneNode node, float baseX, float y, int depth) {
        super(windowManager);
        this.depth = depth;
        nodeName = new Text(windowManager, "Node Name", 0, 0);
        sceneNodeType = new Text(windowManager, "SceneObjectType", 0, 0);
        expandButton = new Button(windowManager, 0, 0, String.format("Children (%d)", node.getNumChildren()));

        for (final SceneNode childNode : node.getChildren()) {
            children.add(new SceneTreeView(windowManager, childNode, baseX, y, depth + 1));
        }

        setBounds(baseX + depth * LEVEL_PADDING, y, WIDTH - depth * LEVEL_PADDING, 0);

        // coloring
        nodeName.setColor(1, 1, 1, 1);
        sceneNodeType.setColor(1, 1, 1, 1);
        expandButton.setColor(1, 1, 1, 1);
        expandButton.setAction(this::onClick);
        background.setColor(0, 0, 0, 1);
    }

    private void onClick() {
        expanded = !expanded;
        if (expanded) {
            expandButton.setColor(0.4f, 0.4f, 0.4f, 1);
        } else {
            expandButton.setColor(1, 1, 1, 1);
        }
        recomputeLayout();
    }

    private void recomputeLayout() {
        // a child have changed, or the expanded flag changed
    }

    // will calculate h dynamically
    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        h = 0;
        nodeName.setText("Node Name", x, y);
        h += nodeName.getHeight();
        sceneNodeType.setText("SceneObjectType", x, y + h);
        h += sceneNodeType.getHeight();
        expandButton.setBounds(x, y + h, w, nodeName.getHeight());
        h += expandButton.getHeight();
        // todo children
        if (expanded) {
            for (final SceneTreeView child : children) {
                child.setBounds(x + LEVEL_PADDING, y + h, w - LEVEL_PADDING, 0);
                h += child.getHeight();
            }
        }
        return super.setBounds(x, y, w, h);
    }

    @Override
    protected void showInternal() {
        super.showInternal();
        nodeName.draw();
        sceneNodeType.draw();
        expandButton.show();
        for (SceneTreeView child : children) {
            child.show();
        }
    }

}
