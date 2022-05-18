package com.play.app.ui;

import java.util.function.Consumer;

import com.play.app.utils.*;

import org.joml.Vector3f;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * provides UI to edit a Vector3f
 */
@Accessors(chain = true)
public class Vector3fEditor extends UIBase {
    private static final float INPUT_WIDTH = 70f;
    public String NUMBER_FORMAT = "%.1f";

    private Vector3f vector3f;
    @Setter
    private Consumer<Vector3f> onChange;
    private final TextInput xInput, yInput, zInput;

    public Vector3fEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        xInput = new TextInput(windowManager, 0, 0).setScrollable(true).setScrollDelta(0.1f);
        yInput = new TextInput(windowManager, 0, 0).setScrollable(true).setScrollDelta(0.1f);
        zInput = new TextInput(windowManager, 0, 0).setScrollable(true).setScrollDelta(0.1f);

        setBounds(x, y, INPUT_WIDTH * 3, xInput.getHeight());
        setDefaultLabel();

        // callbacks
        xInput.setOnChange(this::updateX);
        yInput.setOnChange(this::updateY);
        zInput.setOnChange(this::updateZ);
    }

    public Vector3fEditor setScrollDelta(float delta) {
        xInput.setScrollDelta(delta);
        yInput.setScrollDelta(delta);
        zInput.setScrollDelta(delta);
        // update the display format
        final String deltaStr = String.valueOf(delta);
        final int dotIdx = deltaStr.indexOf(".");
        NUMBER_FORMAT = "%." + (dotIdx == -1 ? 0 : deltaStr.length() - dotIdx - 1) + "f";
        return this;
    }

    public Vector3fEditor setVector3f(final Vector3f v) {
        vector3f = v;
        if (vector3f != null) {
            xInput.setText(String.format(NUMBER_FORMAT, vector3f.x));
            yInput.setText(String.format(NUMBER_FORMAT, vector3f.y));
            zInput.setText(String.format(NUMBER_FORMAT, vector3f.z));
        } else {
            setDefaultLabel();
        }
        return this;
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        xInput.setBounds(x, y, w / 3, h);
        yInput.setBounds(x + w / 3, y, w / 3, h);
        zInput.setBounds(x + w / 3 * 2, y, w / 3, h);
        return this;
    }

    private void fireOnChange() {
        if (onChange != null) {
            onChange.accept(vector3f);
        }
    }

    private void setDefaultLabel() {
        xInput.setText("x");
        yInput.setText("y");
        zInput.setText("z");
    }

    private void updateX(final TextInput input) {
        if (vector3f != null) {
            vector3f.x = input.getAsFloat();
            fireOnChange();
        }
    }

    private void updateY(final TextInput input) {
        if (vector3f != null) {
            vector3f.y = input.getAsFloat();
            fireOnChange();
        }
    }

    private void updateZ(final TextInput input) {
        if (vector3f != null) {
            vector3f.z = input.getAsFloat();
            fireOnChange();
        }
    }

    @Override
    public void showInternal() {
        xInput.show();
        yInput.show();
        zInput.show();
    }

}
