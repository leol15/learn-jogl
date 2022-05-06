package com.play.app.ui;

import com.play.app.Input;
import com.play.app.basics.SpacialThing;
import com.play.app.graphics.Text;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * a form like thing to edit attribute of SpacialThing
 */
public class SpacialThingEditor extends UIBase {

    private SpacialThing spacialThing;
    private final Vector3f eulerAngles = new Vector3f();

    private final Text positionLabel, rotationLabel, scaleLabel;
    private final TextInput inPosX, inPosY, inPosZ;
    private final TextInput inScaX, inScaY, inScaZ;
    private final TextInput inRotX, inRotY, inRotZ;

    private static final float ROW_HEIGHT = 35;
    private static final float COLUMN_SIZE = 100;
    private static final Vector4f DEFAULT_BG_COLOR = new Vector4f(0.4f, 0.4f, 0.4f, 0.75f);
    private static final Vector4f LABEL_COLOR = new Vector4f(0.9f, 0.9f, 0.9f, 1);
    private static final String NUMBER_FORMAT = "%.1f";

    public SpacialThingEditor(WindowManager windowManager, float x, float y) {
        super(windowManager, x, y, 400, 200);
        setBackgroundColor(DEFAULT_BG_COLOR);
        // labels
        positionLabel = new Text(windowManager, "position:", x, y);
        scaleLabel = new Text(windowManager, "scale:", x, y + ROW_HEIGHT);
        rotationLabel = new Text(windowManager, "rotation:", x, y + ROW_HEIGHT * 2);
        positionLabel.setColor(LABEL_COLOR);
        scaleLabel.setColor(LABEL_COLOR);
        rotationLabel.setColor(LABEL_COLOR);
        // inputs
        x += COLUMN_SIZE + 30;
        inPosX = new TextInput(windowManager, x, y).setScrollable(true);
        inPosY = new TextInput(windowManager, x + COLUMN_SIZE, y).setScrollable(true);
        inPosZ = new TextInput(windowManager, x + COLUMN_SIZE * 2, y).setScrollable(true);

        y += ROW_HEIGHT;
        inScaX = new TextInput(windowManager, x, y).setScrollable(true);
        inScaY = new TextInput(windowManager, x + COLUMN_SIZE, y).setScrollable(true);
        inScaZ = new TextInput(windowManager, x + COLUMN_SIZE * 2, y).setScrollable(true);

        y += ROW_HEIGHT;
        inRotX = new TextInput(windowManager, x, y).setScrollable(true).setScrollDelta(5);
        inRotY = new TextInput(windowManager, x + COLUMN_SIZE, y).setScrollable(true).setScrollDelta(5);
        inRotZ = new TextInput(windowManager, x + COLUMN_SIZE * 2, y).setScrollable(true).setScrollDelta(5);

        // set bg size
        setSize(30 + COLUMN_SIZE * 4, ROW_HEIGHT * 2 + rotationLabel.getHeight());

        // configure callbacks
        inPosX.setOnChange((i) -> updatePosition(i.getAsFloat(), null, null));
        inPosY.setOnChange((i) -> updatePosition(null, i.getAsFloat(), null));
        inPosZ.setOnChange((i) -> updatePosition(null, null, i.getAsFloat()));

        inScaX.setOnChange((i) -> updateScale(i.getAsFloat(), null, null));
        inScaY.setOnChange((i) -> updateScale(null, i.getAsFloat(), null));
        inScaZ.setOnChange((i) -> updateScale(null, null, i.getAsFloat()));

        inRotX.setOnChange((i) -> updateRotation(i.getAsFloat(), null, null));
        inRotY.setOnChange((i) -> updateRotation(null, i.getAsFloat(), null));
        inRotZ.setOnChange((i) -> updateRotation(null, null, i.getAsFloat()));
    }

    public SpacialThingEditor setSpacialThing(final SpacialThing s) {
        spacialThing = s;
        if (spacialThing != null) {
            inPosX.setText(String.format(NUMBER_FORMAT, s.translation.x));
            inPosY.setText(String.format(NUMBER_FORMAT, s.translation.y));
            inPosZ.setText(String.format(NUMBER_FORMAT, s.translation.z));

            inScaX.setText(String.format(NUMBER_FORMAT, s.scale.x));
            inScaY.setText(String.format(NUMBER_FORMAT, s.scale.y));
            inScaZ.setText(String.format(NUMBER_FORMAT, s.scale.z));

            spacialThing.rotation.getEulerAnglesXYZ(eulerAngles);
            inRotX.setText(String.format(NUMBER_FORMAT, (float) Math.toDegrees(eulerAngles.x)));
            inRotY.setText(String.format(NUMBER_FORMAT, (float) Math.toDegrees(eulerAngles.y)));
            inRotZ.setText(String.format(NUMBER_FORMAT, (float) Math.toDegrees(eulerAngles.z)));
        } else {
            inPosX.setText("None");
            inPosY.setText("None");
            inPosZ.setText("None");

            inScaX.setText("None");
            inScaY.setText("None");
            inScaZ.setText("None");

            inRotX.setText("None");
            inRotY.setText("None");
            inRotZ.setText("None");
        }
        return this;
    }

    private void updatePosition(Float x, Float y, Float z) {
        if (spacialThing == null) {
            return;
        }
        if (x != null) {
            spacialThing.translation.x = x;
        }
        if (y != null) {
            spacialThing.translation.y = y;
        }
        if (z != null) {
            spacialThing.translation.z = z;
        }
    }

    private void updateScale(Float x, Float y, Float z) {
        if (spacialThing == null) {
            return;
        }
        if (x != null) {
            spacialThing.scale.x = x;
        }
        if (y != null) {
            spacialThing.scale.y = y;
        }
        if (z != null) {
            spacialThing.scale.z = z;
        }
    }

    private void updateRotation(Float x, Float y, Float z) {
        if (spacialThing == null) {
            return;
        }
        spacialThing.rotation.getEulerAnglesXYZ(eulerAngles);
        if (x != null) {
            eulerAngles.x = (float) Math.toRadians(x);
        }
        if (y != null) {
            eulerAngles.y = (float) Math.toRadians(y);
        }
        if (z != null) {
            eulerAngles.z = (float) Math.toRadians(z);
        }
        spacialThing.rotation.rotationXYZ(eulerAngles.x, eulerAngles.y, eulerAngles.z);
    }

    @Override
    protected void showInternal() {
        showBackground();
        positionLabel.draw();
        rotationLabel.draw();
        scaleLabel.draw();

        inPosX.show();
        inPosY.show();
        inPosZ.show();

        inScaX.show();
        inScaY.show();
        inScaZ.show();

        inRotX.show();
        inRotY.show();
        inRotZ.show();
    }

}
