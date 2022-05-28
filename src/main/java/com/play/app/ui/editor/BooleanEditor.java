package com.play.app.ui.editor;

import com.play.app.ui.*;
import com.play.app.ui.property.BooleanProperty;
import com.play.app.utils.*;

public class BooleanEditor extends UIBase {

    private final Button stateButton;
    private BooleanProperty booleanProperty;

    public BooleanEditor(WindowManager windowManager, float x, float y) {
        super(windowManager);
        stateButton = new Button(windowManager, x, y, "null");
        stateButton.setAction(() -> {
            if (booleanProperty != null) {
                booleanProperty.setValue(!booleanProperty.getValue());
                updateDisplay();
            }
        });
        visible = false;
    }

    public void setBooleanProperty(BooleanProperty ref) {
        booleanProperty = ref;
        updateDisplay();
    }

    private void updateDisplay() {
        if (booleanProperty != null) {
            visible = true;
            stateButton.setLabel(String.valueOf(booleanProperty.getValue()));
        } else {
            visible = false;
            stateButton.setLabel("null");
        }
    }

    @Override
    public UIBase setBounds(float x, float y, float w, float h) {
        super.setBounds(x, y, w, h);
        stateButton.setBounds(x, y, w, h);
        return this;
    }

    @Override
    protected void showInternal() {
        super.showInternal();
        stateButton.show();
    }

}
