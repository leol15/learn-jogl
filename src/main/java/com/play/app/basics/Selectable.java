package com.play.app.basics;

import com.play.app.geometry.Ray;

import org.joml.*;

public interface Selectable {

    public Vector3f intersectRay(Ray ray, Matrix4f worldMatrix);

}
