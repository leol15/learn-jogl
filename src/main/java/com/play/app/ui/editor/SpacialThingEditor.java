package com.play.app.ui.editor;

import com.play.app.basics.SpacialThing;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.UIElement;
import com.play.app.ui.elements.UIText;

import org.joml.Vector3f;

/**
 * a form like thing to edit attribute of SpacialThing
 */
public class SpacialThingEditor extends AbstractUIWrapper {

    private SpacialThing spacialThing;
    private final Vector3f eulerAngles = new Vector3f();

    private final UIText positionLabel, rotationLabel, scaleLabel;
    private final VectorXfEditor posEditor, scaEditor, rotEditor;

    private final ContainerV container;

    public SpacialThingEditor(UIManager uiManager) {
        super(uiManager);
        positionLabel = new UIText(uiManager, "position");
        rotationLabel = new UIText(uiManager, "rotation");
        scaleLabel = new UIText(uiManager, "scale");

        posEditor = new VectorXfEditor(uiManager);
        scaEditor = new VectorXfEditor(uiManager);
        rotEditor = new VectorXfEditor(uiManager);

        final ContainerH row1 = new ContainerH(uiManager);
        row1.addChild(positionLabel);
        row1.addChild(posEditor);
        final ContainerH row2 = new ContainerH(uiManager);
        row2.addChild(scaleLabel);
        row2.addChild(scaEditor);
        final ContainerH row3 = new ContainerH(uiManager);
        row3.addChild(rotationLabel);
        row3.addChild(rotEditor);

        container = new ContainerV(uiManager);
        container.addChild(row1);
        container.addChild(row2);
        container.addChild(row3);

        rotEditor.valueChangeEvent.addListener(e -> updateRotation());
    }

    public SpacialThingEditor setSpacialThing(final SpacialThing s) {
        spacialThing = s;
        if (spacialThing != null) {
            posEditor.setVector(s.translation);
            scaEditor.setVector(s.scale);

            spacialThing.rotation.getEulerAnglesXYZ(eulerAngles);
            eulerAngles.set(Math.toDegrees(eulerAngles.x),
                    Math.toDegrees(eulerAngles.y),
                    Math.toDegrees(eulerAngles.z));
            rotEditor.setVector(eulerAngles);
        } else {
            posEditor.clear();
            scaEditor.clear();
            rotEditor.clear();
        }
        return this;
    }

    private void updateRotation() {
        if (spacialThing == null) {
            return;
        }
        spacialThing.rotation.rotationXYZ(
                (float) Math.toRadians(eulerAngles.x),
                (float) Math.toRadians(eulerAngles.y),
                (float) Math.toRadians(eulerAngles.z));
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
