package com.play.app.ui.elements;

import com.play.app.ui.UIElement;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.AbstractUIWrapper;

import org.joml.Matrix4f;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Hard wrapper on a real object?
 */
@Log4j2
public class UIAligner extends AbstractUIWrapper {

    public final UIElement el;
    @Setter
    private float width, height;
    // TODO, only align on 1 direction
    @Setter
    private Alignment verticaAlignment = Alignment.MIDDLE;
    @Setter
    private Alignment horizontalAlignment = Alignment.MIDDLE;

    public enum Alignment {
        START, MIDDLE, END
    }

    private final UITransformer container;

    public UIAligner(UIManager uiManager, UIElement element) {
        super(uiManager);
        if (element == null) {
            log.error("UIAligner must have a non-null element argument");
        }
        el = element;
        container = new UITransformer(uiManager, element);
        setDrawBackground(false);
    }

    @Override
    protected void drawInternal(Matrix4f transform) {
        updated();
        super.drawInternal(transform);
    }

    private void updated() {
        switch (horizontalAlignment) {
            case END:
                container.translation.x = getW() - el.getWidth();
                break;
            case MIDDLE:
                container.translation.x = getW() / 2 - el.getWidth() / 2;
                break;
            default:
                container.translation.x = 0;
        }
        switch (verticaAlignment) {
            case END:
                container.translation.y = getH() - el.getHeight();
                break;
            case MIDDLE:
                container.translation.y = getH() / 2 - el.getHeight() / 2;
                break;
            default:
                container.translation.y = 0;
        }
    }

    @Override
    protected float getW() {
        return Math.max(width, el.getWidth());
    }

    @Override
    protected float getH() {
        return Math.max(height, el.getHeight());
    }

    @Override
    protected UIElement getWrappedElement() {
        return container;
    }

}
