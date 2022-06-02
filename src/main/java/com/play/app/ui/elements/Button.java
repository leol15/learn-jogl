package com.play.app.ui.elements;

import com.play.app.basics.Event;
import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.enums.ButtonAction;
import com.play.app.ui.enums.MouseButtonType;
import com.play.app.utils.Color;

import org.joml.Matrix4f;

import lombok.extern.log4j.Log4j2;

/**
 * Basically a clickabe text
 */
@Log4j2
public class Button extends AbstractUIElement {

    public float padding = 5;
    public final Event<Button> onClickEvent;

    private String label;
    private final UIText text; // the space padded `label`  
    private final Matrix4f textTransform = new Matrix4f();

    public Button(UIManager uiManager) {
        this(uiManager, "button");
    }

    public Button(UIManager uiManager, CharSequence label) {
        super(uiManager);
        this.label = " " + label + " ";
        text = new UIText(uiManager, this.label);
        onClickEvent = new Event<Button>(this);
        setDefaultColors();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = " " + label + " ";
        text.setText(this.label);
    }

    public Color textColor() {
        return text.textColor;
    }

    private void setDefaultColors() {
        bgColor.set(0, 0.6, 0);
        text.bgColor.set(0);
    }

    private void setActiveColors() {
        bgColor.set(0, 0.9, 0);
        text.bgColor.set(0);
    }

    @Override
    public float getW() {
        return text.getWidth() + padding * 2;
    }

    @Override
    public float getH() {
        return text.getHeight() + padding * 2;
    }

    @Override
    public void drawInternal(Matrix4f transform) {
        textTransform.identity().translate(padding, padding, 0).mulLocal(transform);
        text.draw(textTransform);
    }

    @Override
    public void destroy() {
        text.destroy();
    }

    @Override
    public UIElement onMouseButton(MouseButtonType button, ButtonAction action, int mods, float mouseX, float mouseY) {
        if (button != MouseButtonType.LEFT) {
            return this;
        }
        if (action == ButtonAction.PRESS) {
            setActiveColors();
        } else {
            setDefaultColors();
            onClickEvent.fire();
        }
        return this;
    }

}
