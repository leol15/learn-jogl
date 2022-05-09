package com.play.app.ui;

import java.awt.Color;
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

    // temp
    private final Text vector4fEditorLabel;
    private final Vector4fEditor vector4fEditor;

    public PropertyEditor(WindowManager windowManager) {
        super(windowManager);
        setBounds(0, 0, 400, 900);
        setBackgroundColor(Color.RED);
        spacialThingEditors.add(new SpacialThingEditor(windowManager, 0, 100));
        spacialThingEditors.add(new SpacialThingEditor(windowManager, 0, 100));
        for (int i = 0; i < spacialThingEditors.size(); i++) {
            spacialThingEditors.get(i).setSize(400, 150);
        }
        spacialThingEditorLabels.add(new Text(windowManager));
        spacialThingEditorLabels.add(new Text(windowManager));

        vector4fEditorLabel = new Text(windowManager);
        vector4fEditorLabel.setText("", 0, 550);
        vector4fEditor = new Vector4fEditor(windowManager, 0, 600);
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
        visible = true;
    }

    public void addProperty(String name, final Vector4f ref) {
        vector4fEditorLabel.setText(name);
        vector4fEditor.setVector4f(ref);
        visible = true;
    }

    public void clear() {
        for (int i = 0; i < spacialThingEditors.size(); i++) {
            spacialThingEditors.get(i).setSpacialThing(null);
            spacialThingEditorLabels.get(i).setText("");
        }
        vector4fEditor.setVector4f(null);
        vector4fEditorLabel.setText("");
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
        vector4fEditor.show();
        vector4fEditorLabel.draw();
    }

}
