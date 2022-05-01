package com.play.app.drawable;

import java.util.HashSet;
import java.util.Set;

import com.play.app.basics.Thing;
import com.play.app.geometry.Cube;
import com.play.app.graphics.UnitGeometries;

public class CubeDrawable extends ADrawable {

    public CubeDrawable() {
        super();
        addInstance(new Cube());
    }

    @Override
    protected void drawInstance() {
        UnitGeometries.drawCube();
    }

}
