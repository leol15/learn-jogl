package com.play.app.ui;

import java.util.*;

import com.play.app.basics.SpacialThing;
import com.play.app.graphics.Text;
import com.play.app.utils.WindowManager;

import org.joml.*;

import lombok.extern.log4j.Log4j2;

/**
 * used to configure properties
 */
@Log4j2
public class PropertyEditor extends UIBase {

    private static final float LABEL_WIDTH = 120;
    private static final float PROPERTY_GAP = 20;
    private float nextEditorY = 0;
    private int nextActiveSpacialThingEditor = 0;
    private final List<SpacialThingEditor> spacialThingEditors = new ArrayList<>();
    private final List<Text> spacialThingEditorLabels = new ArrayList<>();

    public static int MAX_NUM_VECTORS = 10;
    private int nextActiveV4Editor = 0;
    private final List<Text> vector4fEditorLabels = new ArrayList<>();
    private final List<Vector4fEditor> vector4fEditors = new ArrayList<>();

    private int nextActiveV3Editor = 0;
    private final List<Text> vector3fEditorLabels = new ArrayList<>();
    private final List<VectorXfEditor> vector3fEditors = new ArrayList<>();

    public PropertyEditor(WindowManager windowManager) {
        super(windowManager);
        setBounds(0, 0, 400, 900);
        setBackgroundColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));

        spacialThingEditors.add(new SpacialThingEditor(windowManager, 0, -1000));
        spacialThingEditors.add(new SpacialThingEditor(windowManager, 0, -1000));
        for (int i = 0; i < spacialThingEditors.size(); i++) {
            spacialThingEditors.get(i).setSize(400, 150);
        }
        spacialThingEditorLabels.add(new Text(windowManager));
        spacialThingEditorLabels.add(new Text(windowManager));

        for (int i = 0; i < MAX_NUM_VECTORS; i++) {
            vector4fEditorLabels.add(new Text(windowManager));
            final Vector4fEditor v4Editor = new Vector4fEditor(windowManager, 0, 0);
            v4Editor.setSize(400 - LABEL_WIDTH, 50);
            vector4fEditors.add(v4Editor);

            final VectorXfEditor v3Editor = new VectorXfEditor(windowManager, 3, 0, 0);
            v3Editor.setSize(400 - LABEL_WIDTH, 50);
            vector3fEditorLabels.add(new Text(windowManager));
            vector3fEditors.add(v3Editor);
        }
    }

    public void addProperty(String name, final SpacialThing ref) {
        if (nextActiveSpacialThingEditor >= spacialThingEditors.size()) {
            log.warn("maximum spacialThingEditors active");
            return;
        }
        SpacialThingEditor editor = spacialThingEditors.get(nextActiveSpacialThingEditor);
        Text label = spacialThingEditorLabels.get(nextActiveSpacialThingEditor);
        label.setText(name, 0, nextEditorY);
        nextEditorY += label.getHeight();

        editor.setPosition(0, nextEditorY);
        editor.setSpacialThing(ref);
        nextEditorY += editor.getHeight() + PROPERTY_GAP;

        nextActiveSpacialThingEditor++;
        visible = true;
    }

    public void addProperty(String name, final Vector3f ref) {
        addProperty(name, ref, 0.1f);
    }

    public void addProperty(String name, final Vector3f ref, float scrollDelta) {
        if (nextActiveV3Editor >= vector3fEditors.size()) {
            log.warn("maximum number of vec3 editor active");
            return;
        }
        final VectorXfEditor editor = vector3fEditors.get(nextActiveV3Editor);
        final Text label = vector3fEditorLabels.get(nextActiveV3Editor);
        editor.setVector3f(ref);
        editor.setPosition(LABEL_WIDTH, nextEditorY);
        editor.setScrollDelta(scrollDelta);
        label.setText(name, 0, nextEditorY);

        nextEditorY += editor.getHeight() + PROPERTY_GAP;
        nextActiveV3Editor++;

        visible = true;
    }

    public void addProperty(String name, final Vector4f ref) {
        addProperty(name, ref, 0.1f);
    }

    public void addProperty(String name, final Vector4f ref, final float scrollDelta) {
        if (nextActiveV4Editor >= vector4fEditors.size()) {
            log.warn("maximum number of vec4 editor active");
            return;
        }
        final Vector4fEditor editor = vector4fEditors.get(nextActiveV4Editor);
        final Text label = vector4fEditorLabels.get(nextActiveV4Editor);
        editor.setVector4f(ref);
        editor.setPosition(LABEL_WIDTH, nextEditorY);
        editor.setScrollDelta(scrollDelta);
        label.setText(name, 0, nextEditorY);

        nextEditorY += editor.getHeight() + PROPERTY_GAP;
        nextActiveV4Editor++;

        visible = true;
    }

    public void clear() {
        for (int i = 0; i < spacialThingEditors.size(); i++) {
            spacialThingEditors.get(i).setSpacialThing(null);
            // TODO, implement and use setVisible() 
            spacialThingEditors.get(i).setPosition(0, -1000);
            spacialThingEditorLabels.get(i).setText("");
        }
        for (int i = 0; i < MAX_NUM_VECTORS; i++) {
            vector3fEditorLabels.get(i).setText("");
            vector4fEditorLabels.get(i).setText("");
            vector3fEditors.get(i).setVector3f(null);
            vector4fEditors.get(i).setVector4f(null);
        }
        nextActiveV3Editor = 0;
        nextActiveV4Editor = 0;
        nextActiveSpacialThingEditor = 0;
        nextEditorY = 0;
        visible = false;
    }

    @Override
    protected void showInternal() {
        showBackground();
        for (int i = 0; i < nextActiveSpacialThingEditor; i++) {
            spacialThingEditorLabels.get(i).draw();
            spacialThingEditors.get(i).show();
        }
        for (int i = 0; i < nextActiveV3Editor; i++) {
            vector3fEditorLabels.get(i).draw();
            vector3fEditors.get(i).show();
        }
        for (int i = 0; i < nextActiveV4Editor; i++) {
            vector4fEditorLabels.get(i).draw();
            vector4fEditors.get(i).show();
        }
    }

}
