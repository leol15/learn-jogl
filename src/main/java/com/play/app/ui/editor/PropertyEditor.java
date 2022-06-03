package com.play.app.ui.editor;

import com.play.app.basics.SpacialThing;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.Padding;
import com.play.app.ui.elements.UIText;
import com.play.app.ui.property.BooleanProperty;
import com.play.app.ui.property.FloatProperty;

import org.joml.Vector3f;
import org.joml.Vector4f;

import lombok.extern.log4j.Log4j2;

/**
 * used to configure properties
 */
@Log4j2
public class PropertyEditor extends AbstractUIWrapper {

    private final ContainerV container;

    public PropertyEditor(UIManager uiManager) {
        super(uiManager);
        container = new ContainerV(uiManager);
    }

    public void addDivider() {
        final Padding p = new Padding(uiManager, 150, 8);
        p.bgColor.set(0, 0.7, 0.7);
        container.addChild(p);
    }

    public void addHeader(String name) {
        container.addChild(new UIText(uiManager, name));
    }

    public void addProperty(String name, final SpacialThing ref) {
        container.addChild(new UIText(uiManager, name));
        final SpacialThingEditor editor = new SpacialThingEditor(uiManager).setSpacialThing(ref);
        container.addChild(editor);
    }

    public void addProperty(String name, final Vector3f ref) {
        addProperty(name, ref, 0.1f);
    }

    public void addProperty(String name, final Vector3f ref, float scrollDelta) {
        addRow(name).addChild(new VectorXfEditor(uiManager)
                .setVector(ref)
                .setScrollDelta(scrollDelta));
    }

    public void addProperty(String name, final Vector4f ref) {
        addProperty(name, ref, 0.1f);
    }

    public void addProperty(String name, final Vector4f ref, final float scrollDelta) {
        final VectorXfEditor editor = new VectorXfEditor(uiManager).setVector(ref).setScrollDelta(scrollDelta);
        container.addChild(new UIText(uiManager, name));
        container.addChild(editor);
    }

    public void addProperty(String name, final FloatProperty ref) {
        addProperty(name, ref, 0.1f);
    }

    public void addProperty(String name, final FloatProperty ref, final float scrollDelta) {
        addRow(name).addChild(new VectorXfEditor(uiManager)
                .setVector(ref)
                .setScrollDelta(scrollDelta));
    }

    public void addProperty(String name, final BooleanProperty ref) {
        addRow(name).addChild(new BooleanEditor(uiManager, ref));
    }

    public void clear() {
        container.clear();
    }

    private ContainerH addRow(String name) {
        final ContainerH row = new ContainerH(uiManager);
        row.addChild(new UIText(uiManager, name));
        container.addChild(row);
        return row;
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
