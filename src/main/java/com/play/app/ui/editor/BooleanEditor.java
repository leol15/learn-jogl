package com.play.app.ui.editor;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.Button;
import com.play.app.ui.property.BooleanProperty;

public class BooleanEditor extends AbstractUIWrapper {

    // this is a werid composition, might just subclass?
    private final Button stateButton;
    private BooleanProperty booleanProperty;

    public BooleanEditor(UIManager uiManager) {
        this(uiManager, null);
    }

    public BooleanEditor(UIManager uiManager, BooleanProperty bool) {
        super(uiManager);
        stateButton = new Button(uiManager, "boolean");
        stateButton.onClickEvent.addListener(this::onClick);
        setProperty(bool);
    }

    private void onClick(Button stateButton) {
        if (booleanProperty != null) {
            booleanProperty.setValue(!booleanProperty.getValue());
        }
        updated();
    }

    public void setProperty(BooleanProperty bool) {
        booleanProperty = bool;
        updated();
    }

    private void updated() {
        if (booleanProperty != null) {
            stateButton.setLabel(String.valueOf(booleanProperty.getValue()));
        }
    }

    @Override
    protected UIElement getWrappedElement() {
        return stateButton;
    }

}
