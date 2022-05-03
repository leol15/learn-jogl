package com.play.app.geometry;

import com.play.app.basics.Collidable;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Ray implements Collidable {

    public Vector3f start;
    public Vector3f direction;

    @Override
    public Vector3f collide(Collidable other, Matrix4f transform) {
        return CollisionDetector.collide(this, other);
    }

}
