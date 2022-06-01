package com.play.app.mesh;

import com.play.app.basics.*;

public interface Mesh extends Drawable, Savable {

    /**
     * Chance to release resource
     */
    default void destroy() {}
}
