package com.play.app.ui.elements;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.AbstractUIWrapper;

import lombok.Getter;

public class DropdownList extends AbstractUIWrapper {

    @Getter
    private boolean expanded = false;
    // when not showButton, this can be just a toggle-able list
    private boolean showButton = true;
    private final Button button;
    private final ContainerV items;
    private final ContainerV container;
    private String dropDownLabel = "dropdown";

    public DropdownList(UIManager uiManager) {
        super(uiManager);
        container = new ContainerV(uiManager);
        items = new ContainerV(uiManager);
        button = new Button(uiManager);
        container.addChild(button);

        items.padding = 4;
        button.onClickEvent.addListener(e -> toggle());

        updateLabel();
    }

    public void addItem(UIElement e) {
        items.addChild(e);
        updateLabel();
    }

    public UIElement removeItem(UIElement e) {
        final UIElement el = items.removeChild(e);
        updateLabel();
        return el;
    }

    public int size() {
        return items.numChildren();
    }

    public void clear() {
        items.clear();
        updateLabel();
    }

    public void toggle() {
        // not expand if there is no element
        if (items.numChildren() == 0) {
            return;
        }
        expanded = !expanded;
        if (expanded) {
            container.addChild(items);
        } else {
            container.removeChild(items);
        }
        updateLabel();
    }

    public void setDropDownLabel(String label) {
        dropDownLabel = label;
        updateLabel();
    }

    public void setShowButton(boolean showButton) {
        if (this.showButton == showButton) {
            return;
        }
        this.showButton = showButton;
        if (this.showButton) {
            container.preprendChild(button);
        } else {
            container.removeChild(button);
        }
    }

    private void updateLabel() {
        if (expanded) {
            button.setLabel(String.format("%s -(%d)", dropDownLabel, items.numChildren()));
        } else {
            button.setLabel(String.format("%s +(%d)", dropDownLabel, items.numChildren()));
        }
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
