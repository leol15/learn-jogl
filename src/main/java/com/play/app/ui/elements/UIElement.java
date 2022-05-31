package com.play.app.ui.elements;

import com.play.app.ui.UIEventHandler;

import org.joml.Matrix4f;

/**
 * A ui element
 * It does not know where it is, (I'm at [0, 0])
 * It does not know if it's visible 
 */
public interface UIElement extends UIEventHandler {

    public float getWidth();

    public float getHeight();

    default boolean contains(float xPos, float yPos) {
        return 0 <= xPos && xPos <= getWidth()
                && 0 <= yPos && yPos <= getHeight();
    }

    /**
     * @param transform will be unchanged
     */
    public void draw(Matrix4f transform);

}
