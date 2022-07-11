package com.play.app.ui.editor;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.TextInput;
import com.play.app.ui.property.StringProperty;

public class StringEditor extends AbstractUIWrapper {

    private final TextInput textInput;

    public StringEditor(UIManager uiManager, StringProperty ref) {
        super(uiManager);
        textInput = new TextInput(uiManager, ref.getValue());
        // TODO, what if ref is updated by something else than input?
        textInput.changeEvent.addListener(t -> {
            ref.setValue(t.getAsString());
            ref.updateEvent.fire();
        });
    }

    @Override
    protected UIElement getWrappedElement() {
        return textInput;
    }

}
