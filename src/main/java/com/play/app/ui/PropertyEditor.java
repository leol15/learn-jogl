package com.play.app.ui;

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.utils.WindowManager;

import org.joml.Vector3f;

/**
 * used to configure properties
 */
public class PropertyEditor extends UIBase {

    private SpacialThingEditor spacialThingEditor;

    public PropertyEditor(WindowManager windowManager) {
        super(windowManager);
        setBounds(0, 0, 400, 900);
        setBackgroundColor(Color.RED);
        spacialThingEditor = new SpacialThingEditor(windowManager, 0, 100);
    }

    public void setSpacialThing(final SpacialThing ref) {
        spacialThingEditor.setSpacialThing(ref);
        visible = true;
    }

    public void addProperty(String name, final Vector3f ref) {
        visible = true;
    }

    public void clear() {
        spacialThingEditor.setSpacialThing(null);
        visible = false;
    }

    @Override
    protected void showInternal() {
        showBackground();
        spacialThingEditor.show();
    }

}
