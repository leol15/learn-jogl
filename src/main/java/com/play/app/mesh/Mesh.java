package com.play.app.mesh;

import com.play.app.graphics.UnitGeometries;

public interface Mesh {

    public abstract void drawMesh();

    // common meshes
    public static final Mesh CUBE = () -> UnitGeometries.drawCube();
    public static final Mesh CONE = () -> UnitGeometries.drawCone();
    public static final Mesh SPHERE = () -> UnitGeometries.drawSphere();
    public static final Mesh CYCLINDER = () -> UnitGeometries.drawCyclinder();

    public static final Mesh PLANE = () -> UnitGeometries.drawPlane();
    public static final Mesh CIRCLE = () -> UnitGeometries.drawCircle();

    public static Mesh createSphereMesh(int numSections) {
        return () -> UnitGeometries.drawSphere(numSections);
    }

    public static Mesh createCyclinderMesh(int numSections) {
        return () -> UnitGeometries.drawCyclinder(numSections);
    }

    public static Mesh createConeMesh(int numSections) {
        return () -> UnitGeometries.drawCone(numSections);
    }

    public static Mesh createCircleMesh(int numSections) {
        return () -> UnitGeometries.drawCircle(numSections);
    }
}
