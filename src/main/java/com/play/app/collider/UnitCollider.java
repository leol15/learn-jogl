package com.play.app.collider;

import java.io.IOException;

import com.play.app.basics.Collidable;
import com.play.app.geometry.*;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

/**
 * Collider for all the unit geometries, parallel to UnitMesh
 */
public class UnitCollider implements Collider {

    private final Type type;
    private final Collidable collidable;

    public UnitCollider(Type t) {
        type = t;
        switch (type) {
            case Circle:
                collidable = new Sphere();
                break;
            case Sphere:
                collidable = new Sphere();
                break;
            case Cone:
                collidable = new Sphere();
                break;
            case Cyclinder:
                collidable = new Sphere();
                break;
            case Plane:
                collidable = new Plane();
                break;
            default:
            case Cube:
                collidable = new Cube();
                break;
        }
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("type", type.name());
        writer.writeEndObject();
    }

    public static UnitCollider create(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        final String type = reader.consumeStringField("type");
        reader.consumeEndObject();
        return new UnitCollider(Type.valueOf(type));
    }

    @Override
    public Vector3f collide(Collidable other, Matrix4f myTransform, Matrix4f otherTransform) {
        return collidable.collide(other, myTransform, otherTransform);
    }

}
