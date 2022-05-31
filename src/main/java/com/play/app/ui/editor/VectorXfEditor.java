package com.play.app.ui.editor;

import java.util.ArrayList;
import java.util.List;

import com.play.app.basics.Event;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.TextInput;
import com.play.app.ui.elements.UIElement;
import com.play.app.ui.property.FloatProperty;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import lombok.experimental.Accessors;

/**
 * provides UI to edit a Vector3f
 */
@Accessors(chain = true)
public class VectorXfEditor extends AbstractUIWrapper {

    // dont know how to design this... so have both field
    // referencing the thing being edited
    private FloatProperty floatProperty;
    private Vector2f vector2f;
    private Vector3f vector3f;
    private Vector4f vector4f;
    public final Event<VectorXfEditor> valueChangeEvent;

    // TextInput respects visible attribute, so will create 4 of them and use as appropriate
    private final List<TextInput> inputs = new ArrayList<>();
    private final ContainerH container;

    public VectorXfEditor(UIManager uiManager) {
        super(uiManager);
        container = new ContainerH(uiManager);
        for (int i = 0; i < 4; i++) {
            final TextInput input = new TextInput(uiManager);
            input.setScrollDelta(0.1f);
            final int index = i;
            input.changeEvent.addListener(e -> setComponentValue(index, e.getAsFloat()));
            container.addChild(input);
            inputs.add(input);
        }
        valueChangeEvent = new Event<VectorXfEditor>(this);
    }

    public VectorXfEditor setScrollDelta(float delta) {
        for (final TextInput e : inputs) {
            e.setScrollDelta(delta);
        }
        return this;
    }

    public void clear() {
        floatProperty = null;
        vector2f = null;
        vector3f = null;
        vector4f = null;
        displayComponents();
    }

    public VectorXfEditor setVector(FloatProperty f) {
        clear();
        floatProperty = f;
        displayComponents();
        return this;
    }

    public VectorXfEditor setVector(final Vector2f v) {
        clear();
        vector2f = v;
        displayComponents();
        return this;
    }

    public VectorXfEditor setVector(final Vector3f v) {
        clear();
        vector3f = v;
        displayComponents();
        return this;
    }

    public VectorXfEditor setVector(final Vector4f v) {
        clear();
        vector4f = v;
        displayComponents();
        return this;
    }

    private Float getComponentValue(int index) {
        if (index < 0) {
            return null;
        }
        if (index < 1 && floatProperty != null) {
            return floatProperty.getValue();
        }
        if (index < 2 && vector2f != null) {
            return vector2f.get(index);
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
        if (index < 0) {
            return;
        }
        if (index < 1 && floatProperty != null) {
            floatProperty.setValue(value);
        }
        if (index < 2 && vector2f != null) {
            vector2f.setComponent(index, value);
        }
        if (index < 3 && vector3f != null) {
            vector3f.setComponent(index, value);
        }
        if (index < 4 && vector4f != null) {
            vector4f.setComponent(index, value);
        }
    }

    // reference has changed, will update TextInput display now
    private void displayComponents() {
        for (int i = 0; i < inputs.size(); i++) {
            final Float v = getComponentValue(i);
            final TextInput input = inputs.get(i);
            if (v == null) {
                input.setVisible(false);
                input.setContent("comp " + i);
            } else {
                input.setVisible(true);
                input.setContent(v);
            }
        }
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
