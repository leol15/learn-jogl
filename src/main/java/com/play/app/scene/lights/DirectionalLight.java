package com.play.app.scene.lights;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.basics.Collidable;
import com.play.app.geometry.Cube;
import com.play.app.mesh.Mesh;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.WorldSerializer;

import org.joml.*;

/**
 * light direction is (0, 1, 0)
 */
public class DirectionalLight implements Light {
    public Vector4f color = new Vector4f(1, 1, 0, 1);
    private static final Vector4f DIRECTION = new Vector4f(0, 1, 0, 0);

    @Override
    public void addToUBO(Matrix4f worldTransform) {
        LightUBO.getInstance().acceptDirectionalLight(this, worldTransform);
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        editor.addProperty("DL Color", color);
    }

    @Override
    public Mesh getDebugMesh() {
        return Mesh.createConeMesh(10);
    }

    @Override
    public Collidable getDebugCollidable() {
        return new Cube();
    }

    public Vector4f getDirection() {
        return DIRECTION;
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public void save(YAMLGenerator generator) throws IOException {
        generator.writeStartObject();
        WorldSerializer.writeObjectType(this.getClass(), generator);
        WorldSerializer.writeObjectField("color", color, generator);
        generator.writeEndObject();
    }

}
