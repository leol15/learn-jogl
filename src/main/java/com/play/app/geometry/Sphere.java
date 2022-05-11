package com.play.app.geometry;

import com.play.app.basics.Collidable;

import org.joml.*;

public class Sphere implements Collidable {

    @Override
    public Vector3f collide(Collidable other, Matrix4f myTransform, Matrix4f otherTransform) {
        return CollisionDetector.collide(this, other, myTransform, otherTransform);
    }

}
