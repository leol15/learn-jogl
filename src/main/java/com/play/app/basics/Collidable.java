package com.play.app.basics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Collidable {

    public Vector3f collide(Collidable other, Matrix4f myTransform, Matrix4f otherTransform);

}
