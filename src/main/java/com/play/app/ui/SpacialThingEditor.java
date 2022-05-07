package com.play.app.ui;

import java.lang.Math;

import com.play.app.basics.SpacialThing;
import com.play.app.graphics.Text;
import com.play.app.utils.WindowManager;

import org.joml.*;

/**
 * a form like thing to edit attribute of SpacialThing
 */
public class SpacialThingEditor extends UIBase {

    private SpacialThing spacialThing;
    private final Vector3f eulerAngles = new Vector3f();

    private final Text positionLabel, rotationLabel, scaleLabel;
    private final Vector3fEditor posEditor, scaEditor, rotEditor;

    private static final float ROW_HEIGHT = 40;
    private static final float LABEL_SIZE = 150;
    private static final Vector4f DEFAULT_BG_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 0.95f);
    private static final Vector4f LABEL_COLOR = new Vector4f(0.9f, 0.9f, 0.9f, 1);

    public SpacialThingEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        setBackgroundColor(DEFAULT_BG_COLOR);
        // labels
        positionLabel = new Text(windowManager, "position:", x, y);
        scaleLabel = new Text(windowManager, "scale:", x, y + ROW_HEIGHT);
        rotationLabel = new Text(windowManager, "rotation:", x, y + ROW_HEIGHT * 2);
        positionLabel.setColor(LABEL_COLOR);
        scaleLabel.setColor(LABEL_COLOR);
        rotationLabel.setColor(LABEL_COLOR);
        // inputs
        posEditor = new Vector3fEditor(windowManager, x + LABEL_SIZE, y);
        scaEditor = new Vector3fEditor(windowManager, x + LABEL_SIZE, y + ROW_HEIGHT);
        rotEditor = new Vector3fEditor(windowManager, x + LABEL_SIZE, y + ROW_HEIGHT * 2).setScrollDelta(5);

        // set bg size
        setBounds(x, y, LABEL_SIZE + posEditor.getWidth(), ROW_HEIGHT * 2 + rotEditor.getHeight());

        // configure callbacks
        // special handling for rotation, since need convert from vector3f to quat
        rotEditor.setOnChange(i -> updateRotation());
    }

    public SpacialThingEditor setSpacialThing(final SpacialThing s) {
        spacialThing = s;
        if (spacialThing != null) {
            posEditor.setVector3f(s.translation);
            scaEditor.setVector3f(s.scale);

            spacialThing.rotation.getEulerAnglesXYZ(eulerAngles);
            eulerAngles.set(Math.toDegrees(eulerAngles.x),
                    Math.toDegrees(eulerAngles.y),
                    Math.toDegrees(eulerAngles.z));
            rotEditor.setVector3f(eulerAngles);
        } else {
            posEditor.setVector3f(null);
            scaEditor.setVector3f(null);
            rotEditor.setVector3f(null);
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
    protected void showInternal() {
        showBackground();

        positionLabel.draw();
        rotationLabel.draw();
        scaleLabel.draw();

        posEditor.show();
        scaEditor.show();
        rotEditor.show();
    }

}
