package com.play.app.ui.editor;

import java.util.*;

import com.play.app.basics.SpacialThing;
import com.play.app.graphics.Text;
import com.play.app.ui.*;
import com.play.app.ui.property.*;
import com.play.app.utils.WindowManager;

import org.joml.*;

import lombok.extern.log4j.Log4j2;

/**
 * used to configure properties
 */
@Log4j2
public class PropertyEditor extends UIBase {

    private static final float WIDTH = 400;
    private static final float LABEL_WIDTH = 120;
    private static final float PROPERTY_GAP = 20;
    private float nextEditorY = 0;

    private int nextActiveSpacialThingEditor = 0;
    private final List<SpacialThingEditor> spacialThingEditors = new ArrayList<>();
    private final List<Text> spacialThingEditorLabels = new ArrayList<>();

    private final List<Text> inlineLabels = new ArrayList<>();

    public static int MAX_NUM_VECTORS = 10;
    private int nextActiveVXEditor = 0;
    private final List<VectorXfEditor> vectorXfEditors = new ArrayList<>();

    public static int MAX_NUM_BOOLEANS = 10;
    private int nextActiveBooleanEditor = 0;
    private final List<BooleanEditor> booleanEditors = new ArrayList<>();

    public PropertyEditor(WindowManager windowManager) {
        super(windowManager);
        setBounds(0, 0, WIDTH, 900);
        setBackgroundColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));

        final float ROW_HEIGHT = new Text(windowManager).getFontHeight() + 5;

        for (int i = 0; i < 2; i++) {
            final SpacialThingEditor editor = new SpacialThingEditor(windowManager, 0, -1000);
            editor.setSize(WIDTH, ROW_HEIGHT * 3);
            spacialThingEditors.add(editor);
            spacialThingEditorLabels.add(new Text(windowManager));
        }

        for (int i = 0; i < MAX_NUM_VECTORS; i++) {
            final VectorXfEditor vXEditor = new VectorXfEditor(windowManager, 0, 0);
            vXEditor.setSize(WIDTH - LABEL_WIDTH, ROW_HEIGHT);
            vectorXfEditors.add(vXEditor);

            inlineLabels.add(new Text(windowManager));
        }

        for (int i = 0; i < MAX_NUM_BOOLEANS; i++) {
            final BooleanEditor editor = new BooleanEditor(windowManager, 0, 0);
            editor.setSize(WIDTH - LABEL_WIDTH, ROW_HEIGHT);
            booleanEditors.add(editor);

            inlineLabels.add(new Text(windowManager));
        }
        visible = false;
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
        addVectorPropertyHelper(name, 0.1f).setVector(ref);
    }

    public void addProperty(String name, final Vector3f ref, float scrollDelta) {
        addVectorPropertyHelper(name, scrollDelta).setVector(ref);
    }

    public void addProperty(String name, final Vector4f ref) {
        addVectorPropertyHelper(name, 0.1f).setVector(ref);
    }

    public void addProperty(String name, final Vector4f ref, final float scrollDelta) {
        addVectorPropertyHelper(name, scrollDelta).setVector(ref);
    }

    public void addProperty(String name, final FloatProperty ref) {
        addVectorPropertyHelper(name, 0.1f).setVector(ref);
    }

    public void addProperty(String name, final FloatProperty ref, final float scrollDelta) {
        addVectorPropertyHelper(name, scrollDelta).setVector(ref);
    }

    public void addProperty(String name, final BooleanProperty ref) {
        if (nextActiveBooleanEditor >= booleanEditors.size()) {
            log.warn("maximum number of boolean editor active");
            return;
        }
        final BooleanEditor editor = booleanEditors.get(nextActiveBooleanEditor);
        final Text label = inlineLabels.get(getNextInlineLabelIdx());
        editor.setPosition(LABEL_WIDTH, nextEditorY);
        editor.setBooleanProperty(ref);
        label.setText(name, 0, nextEditorY);

        nextEditorY += editor.getHeight() + PROPERTY_GAP;
        nextActiveBooleanEditor++;
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
            inlineLabels.get(i).setText("");
            vectorXfEditors.get(i).clear();
        }
        nextEditorY = 0;
        nextActiveSpacialThingEditor = 0;
        nextActiveVXEditor = 0;
        nextActiveBooleanEditor = 0;
        visible = false;
    }

    private VectorXfEditor addVectorPropertyHelper(String name, float scrollDelta) {
        if (nextActiveVXEditor >= vectorXfEditors.size()) {
            log.warn("maximum number of vector editor active");
            return null;
        }
        final VectorXfEditor editor = vectorXfEditors.get(nextActiveVXEditor);
        final Text label = inlineLabels.get(getNextInlineLabelIdx());
        editor.setPosition(LABEL_WIDTH, nextEditorY);
        editor.setScrollDelta(scrollDelta);
        label.setText(name, 0, nextEditorY);

        nextEditorY += editor.getHeight() + PROPERTY_GAP;
        nextActiveVXEditor++;
        visible = true;

        return editor;
    }

    private int getNextInlineLabelIdx() {
        return nextActiveBooleanEditor + nextActiveVXEditor;
    }

    @Override
    protected void showInternal() {
        showBackground();
        for (int i = 0; i < nextActiveSpacialThingEditor; i++) {
            spacialThingEditorLabels.get(i).draw();
            spacialThingEditors.get(i).show();
        }

        final int numInlineLabelsUsed = getNextInlineLabelIdx();
        for (int i = 0; i < numInlineLabelsUsed; i++) {
            inlineLabels.get(i).draw();
        }
        for (int i = 0; i < nextActiveVXEditor; i++) {
            vectorXfEditors.get(i).show();
        }
        for (int i = 0; i < nextActiveBooleanEditor; i++) {
            booleanEditors.get(i).show();
        }
    }

}
