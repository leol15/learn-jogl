package com.play.app.basics;

import com.play.app.geometry.Ray;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

public interface Selectable {

    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix);

    public void select(PropertyEditor editor);

    public void deselect();

}
