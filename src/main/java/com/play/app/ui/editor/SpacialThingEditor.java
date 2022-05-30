package com.play.app.ui.editor;

import java.lang.Math;

import com.play.app.basics.SpacialThing;
import com.play.app.graphics.Text;
import com.play.app.ui.*;
import com.play.app.utils.WindowManager;

import org.joml.*;

/**
 * a form like thing to edit attribute of SpacialThing
 */
public class SpacialThingEditor extends UIBase {

    private SpacialThing spacialThing;
    private final Vector3f eulerAngles = new Vector3f();

    private final Text positionLabel, rotationLabel, scaleLabel;
    private final VectorXfEditor posEditor, scaEditor, rotEditor;

    private static final float ROW_HEIGHT = 40;
    private static final float LABEL_SIZE = 150;
    private static final float INPUT_WIDTH = 300;
    private static final Vector4f DEFAULT_BG_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 0.95f);
    private static final Vector4f LABEL_COLOR = new Vector4f(0.9f, 0.9f, 0.9f, 1);

    public SpacialThingEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        background.setColor(DEFAULT_BG_COLOR);
        // labels
        positionLabel = new Text(windowManager);
        scaleLabel = new Text(windowManager);
        rotationLabel = new Text(windowManager);
        positionLabel.setColor(LABEL_COLOR);
        scaleLabel.setColor(LABEL_COLOR);
        rotationLabel.setColor(LABEL_COLOR);
        // inputs
        posEditor = new VectorXfEditor(windowManager, 0, 0);
        scaEditor = new VectorXfEditor(windowManager, 0, 0);
        rotEditor = new VectorXfEditor(windowManager, 0, 0).setScrollDelta(5);

        // set bg size
        setBounds(x, y, LABEL_SIZE + 300, ROW_HEIGHT * 3);

        // configure callbacks
        // special handling for rotation, since need convert from vector3f to quat
        rotEditor.setOnChange(i -> updateRotation());
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
    protected void showInternal() {
        showBackground();

        positionLabel.draw();
        rotationLabel.draw();
        scaleLabel.draw();

        posEditor.show();
        scaEditor.show();
        rotEditor.show();
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        // TODO Auto-generated method stub
        super.setBounds(x, y, w, h);
        // labels
        final float rowHeight = h / 3;
        final float inputWidht = w - LABEL_SIZE > 0 ? w - LABEL_SIZE : INPUT_WIDTH;
        positionLabel.setText("position:", x, y);
        scaleLabel.setText("scale:", x, y + rowHeight);
        rotationLabel.setText("rotation:", x, y + rowHeight * 2);
        // inputs
        posEditor.setBounds(x + LABEL_SIZE, y, inputWidht, rowHeight);
        scaEditor.setBounds(x + LABEL_SIZE, y + rowHeight, inputWidht, rowHeight);
        rotEditor.setBounds(x + LABEL_SIZE, y + rowHeight * 2, inputWidht, rowHeight);
        return this;
    }

}
