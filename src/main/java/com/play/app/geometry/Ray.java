package com.play.app.geometry;

import com.play.app.basics.Collidable;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Ray implements Collidable {

    public final Vector3f start;
    public final Vector3f direction;

    @Override
    public Vector3f collide(Collidable other, Matrix4f myTransform, Matrix4f otherTransform) {
        return CollisionDetector.collide(this, other, myTransform, otherTransform);
    }

}
