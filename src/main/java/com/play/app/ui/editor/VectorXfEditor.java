package com.play.app.ui.editor;

import java.util.*;
import java.util.function.Consumer;

import com.play.app.ui.*;
import com.play.app.ui.property.FloatProperty;
import com.play.app.utils.WindowManager;

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
    private FloatProperty floatProperty;

    @Setter
    private Consumer<VectorXfEditor> onChange;
    // TextInput respects visible attribute, so will create 4 of them and use as appropriate
    private final List<TextInput> inputs = new ArrayList<>();

    public VectorXfEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        for (int i = 0; i < 4; i++) {
            final TextInput input = new TextInput(windowManager, 0, 0).setScrollable(true).setScrollDelta(0.1f);
            inputs.add(input);
            final int index = i;
            input.setOnChange(textInput -> setComponentValue(index, textInput.getAsFloat()));
            input.setVisible(false);
        }

        setBounds(x, y, INPUT_WIDTH, inputs.get(0).getHeight());
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

    public void clear() {
        floatProperty = null;
        vector3f = null;
        vector4f = null;
        configureInputDisplays();
    }

    public VectorXfEditor setVector(FloatProperty f) {
        clear();
        floatProperty = f;
        configureInputDisplays();
        return this;
    }

    public VectorXfEditor setVector(final Vector3f v) {
        clear();
        vector3f = v;
        configureInputDisplays();
        return this;
    }

    public VectorXfEditor setVector(final Vector4f v) {
        clear();
        vector4f = v;
        configureInputDisplays();
        return this;
    }

    private Float getComponentValue(int index) {
        if (index < 0) {
            return null;
        }
        if (index == 0 && floatProperty != null) {
            return floatProperty.getValue();
        }
        if (index < 3 && vector3f != null) {
            return vector3f.get(index);
        }
        if (index < 4 && vector4f != null) {
            return vector4f.get(index);
        }
        return null;
    }

    private void setComponentValue(int index, float value) {
        if (floatProperty != null) {
            floatProperty.setValue(value);
        } else if (vector3f != null) {
            vector3f.setComponent(index, value);
        } else if (vector4f != null) {
            vector4f.setComponent(index, value);
        }
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    private int getNumberOfComponents() {
        for (int i = inputs.size() - 1; i > -1; i--) {
            if (getComponentValue(i) != null) {
                return i + 1;
            }
        }
        return 0;
    }

    // reference has changed, will update TextInput display now
    private void configureInputDisplays() {
        setBounds(getX(), getY(), getWidth(), getHeight());

        for (int i = 0; i < inputs.size(); i++) {
            final Float v = getComponentValue(i);
            final TextInput input = inputs.get(i);
            if (v == null) {
                input.setVisible(false);
            } else {
                input.setVisible(true);
                input.setText(String.format(NUMBER_FORMAT, v));
            }
        }
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        final int ct = getNumberOfComponents();
        if (ct != 0) {
            final float componentW = w / ct;
            for (int i = 0; i < ct; i++) {
                inputs.get(i).setBounds(x + componentW * i, y, componentW, h);
            }
        }
        return this;
    }

    @Override
    public void showInternal() {
        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).show();
        }
    }

}
