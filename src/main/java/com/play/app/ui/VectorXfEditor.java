package com.play.app.ui;

import java.util.*;
import java.util.function.Consumer;

import com.play.app.utils.*;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

/**
 * provides UI to edit a Vector3f
 */
@Log4j2
@Accessors(chain = true)
public class VectorXfEditor extends UIBase {
    private static final float INPUT_WIDTH = 70f;
    public String NUMBER_FORMAT = "%.1f";

    // dont know how to design this... so have both field
    // referencing the thing being edited
    private Vector3f vector3f;
    private Vector4f vector4f;

    @Setter
    private Consumer<VectorXfEditor> onChange;
    private final List<TextInput> inputs = new ArrayList<>();

    public VectorXfEditor(WindowManager windowManager, int numComponent, float x, float y) {
        super(windowManager);
        if (numComponent < 1) {
            log.warn("cannot edit vector with less than 1 component");
        }
        for (int i = 0; i < numComponent; i++) {
            final TextInput input = new TextInput(windowManager, 0, 0).setScrollable(true).setScrollDelta(0.1f);
            inputs.add(input);
            final int index = i;
            input.setOnChange(textInput -> setComponentValue(index, textInput.getAsFloat()));
        }

        setBounds(x, y, INPUT_WIDTH * numComponent, inputs.get(0).getHeight());
        configureInputDisplays();
    }

    public VectorXfEditor setScrollDelta(float delta) {
        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setScrollDelta(delta);
        }

        // update the display format
        final String deltaStr = String.valueOf(delta);
        final int dotIdx = deltaStr.indexOf(".");
        NUMBER_FORMAT = "%." + (dotIdx == -1 ? 0 : deltaStr.length() - dotIdx - 1) + "f";
        configureInputDisplays();
        return this;
    }

    public VectorXfEditor setVector3f(final Vector3f v) {
        vector3f = v;
        vector4f = null;
        configureInputDisplays();
        return this;
    }

    public VectorXfEditor setVector4f(final Vector4f v) {
        vector3f = null;
        vector4f = v;
        configureInputDisplays();
        return this;
    }

    private Float getComponentValue(int index) {
        if (index < 0) {
            return null;
        }
        if (index < 3 && vector3f != null) {
            return vector3f.get(index);
        }
        if (index < 4 && vector4f != null) {
            return vector4f.get(index);
        }
        return null;
    }

    private void configureInputDisplays() {
        for (int i = 0; i < inputs.size(); i++) {
            final Float v = getComponentValue(i);
            if (v == null) {
                final String componentName = String.valueOf((char) ('x' + i));
                inputs.get(i).setText(componentName);
            } else {
                final String value = String.format(NUMBER_FORMAT, v);
                inputs.get(i).setText(value);
            }
        }
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        final float componentW = w / inputs.size();
        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setBounds(x + componentW * i, y, componentW, h);
        }
        return this;
    }

    private void setComponentValue(int index, float value) {
        if (vector3f != null) {
            if (index == 0) {
                vector3f.x = value;
            } else if (index == 1) {
                vector3f.y = value;
            } else if (index == 2) {
                vector3f.z = value;
            }
        } else if (vector4f != null) {
            if (index == 0) {
                vector4f.x = value;
            } else if (index == 1) {
                vector4f.y = value;
            } else if (index == 2) {
                vector4f.z = value;
            } else if (index == 2) {
                vector4f.w = value;
            }
        }
        fireOnChange();
    }

    private void fireOnChange() {
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    @Override
    public void showInternal() {
        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).show();
        }
    }

}
