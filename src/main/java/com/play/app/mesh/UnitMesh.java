package com.play.app.mesh;

import java.io.IOException;

import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.utils.WorldSerializer;

/**
 * Immutable mesh of unit geometries, cube, sphere, ect
 */
public class UnitMesh implements Mesh {

    private final Type type;
    private final int resolution;
    private final Runnable drawFunc;

    public UnitMesh(Type t) {
        this(t, 20);
    }

    public UnitMesh(Type t, int resolution) {
        this.type = t;
        this.resolution = resolution;
        switch (type) {
            case Circle:
                drawFunc = () -> this.drawCircle();
                break;
            case Sphere:
                drawFunc = () -> this.drawSphere();
                break;
            case Cone:
                drawFunc = () -> this.drawCone();
                break;
            case Cyclinder:
                drawFunc = () -> this.drawCyclinder();
                break;
            case Plane:
                drawFunc = () -> this.drawPlane();
                break;
            default:
            case Cube:
                drawFunc = () -> this.drawCube();
                break;
        }
    }

    @Override
    public void draw() {
        drawFunc.run();
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("type", type.name());
        writer.writeObjectField("resolution", resolution);
        writer.writeEndObject();
    }

    public static UnitMesh create(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        final String type = reader.consumeStringField("type");
        final int resolution = reader.consumeIntField("resolution");
        reader.consumeEndObject();
        return new UnitMesh(Type.valueOf(type), resolution);
    }

    private void drawCube() {
        UnitGeometries.drawCube();
    }

    private void drawCircle() {
        UnitGeometries.drawCircle(resolution);
    }

    private void drawPlane() {
        UnitGeometries.drawPlane();
    }

    private void drawCone() {
        UnitGeometries.drawCone(resolution);
    }

    private void drawCyclinder() {
        UnitGeometries.drawCyclinder(resolution);
    }

    private void drawSphere() {
        UnitGeometries.drawSphere(resolution);
    }
}
