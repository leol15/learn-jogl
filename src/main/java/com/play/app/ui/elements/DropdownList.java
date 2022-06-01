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

    public DropdownList(UIManager uiManager) {
        super(uiManager);
        container = new ContainerV(uiManager);
        items = new ContainerV(uiManager);
        button = new Button(uiManager, "Expand (0)");
        container.addChild(button);

        items.padding = 4;
        button.onClickEvent.addListener(e -> toggle());
    }

    public void addItem(UIElement e) {
        items.addChild(e);
    }

    public UIElement removeItem(UIElement e) {
        final UIElement el = items.removeChild(e);
        return el;
    }

    public void clear() {
        items.clear();
    }

    public void toggle() {
        // not expand if there is no element
        if (items.numChildren() == 0) {
            return;
        }
        expanded = !expanded;
        if (expanded) {
            button.setLabel("Collapse");
            container.addChild(items);
        } else {
            button.setLabel(String.format("Expand (%d)", items.numChildren()));
            container.removeChild(items);
        }
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

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
