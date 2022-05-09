package com.play.app.ui;

import java.util.function.Consumer;

import com.play.app.Input;
import com.play.app.utils.WindowManager;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Vector4fEditor extends UIBase {

    @Setter
    private Consumer<Vector4f> onChange;
    private Vector4f vector4f;
    private final Vector3f internalV3 = new Vector3f();

    private final Vector3fEditor vector3fEditor;
    private final TextInput wInput;

    public Vector4fEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        // wInput = new TextInput(windowManager, 0, 0);
        //TODO Auto-generated constructor stub
        vector3fEditor = new Vector3fEditor(windowManager, x, y);
        wInput = new TextInput(windowManager, x, y).setScrollable(true).setScrollDelta(0.1f);
        setBounds(x, y, 400, 30);

        vector3fEditor.setOnChange(this::onV3Changed);
        wInput.setOnChange(this::updateW);

        setDefaultLabel();
    }

    public Vector4fEditor setScrollDelta(float delta) {
        vector3fEditor.setScrollDelta(delta);
        wInput.setScrollDelta(delta);
        return this;
    }

    public Vector4fEditor setVector4f(final Vector4f v) {
        vector4f = v;
        if (vector4f != null) {
            internalV3.set(vector4f.x, vector4f.y, vector4f.z);
            vector3fEditor.setVector3f(internalV3);
            wInput.setText(String.format(Vector3fEditor.NUMBER_FORMAT, vector4f.w));
        } else {
            setDefaultLabel();
        }
        return this;
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        vector3fEditor.setBounds(x, y, w * 3f / 4f, h);
        wInput.setBounds(x + w * 3f / 4f, y, w / 4f, h);
        return this;
    }

    private void setDefaultLabel() {
        vector3fEditor.setVector3f(null);
        wInput.setText("w");
    }

    private void fireOnChange() {
        if (onChange != null) {
            onChange.accept(vector4f);
        }
    }

    private void updateW(final TextInput input) {
        if (vector4f != null) {
            vector4f.w = input.getAsFloat();
            fireOnChange();
        }
    }

    private void onV3Changed(Vector3f v) {
        if (v != null) {
            vector4f.set(v.x, v.y, v.z, vector4f.w);
            fireOnChange();
        }
    }

    @Override
    protected void showInternal() {
        vector3fEditor.show();
        wInput.show();
    }
}
