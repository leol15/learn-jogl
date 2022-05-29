package com.play.app.geometry;

import com.play.app.basics.*;

import org.joml.*;

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

    public Ray transform(Matrix4f transform) {
        final Vector3f newStart = new Vector3f();
        final Vector3f newDirection = new Vector3f();

        final Vector4f src = new Vector4f();

        src.set(start, 1);
        src.mul(transform);
        newStart.set(src.x, src.y, src.z);

        src.set(direction, 0);
        src.mul(transform);
        newDirection.set(src.x, src.y, src.z);

        return new Ray(newStart, newDirection.normalize());
    }

    public Matrix4f getTransform(Matrix4f out, float length, float lineWidth) {
        out.identity();
        out.translateLocal(start);
        Quaternionf q = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), direction);
        out.rotate(q);
        out.scale(lineWidth, length, lineWidth);
        return out;
    }

}
