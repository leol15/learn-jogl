package com.play.app.basics;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpacialThing implements SaveLoad {

    public final Vector3f scale = new Vector3f(1);
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f translation = new Vector3f();

    public Matrix4f getModelMatrix(final Matrix4f out) {
        return out.translation(translation).rotate(rotation).scale(scale);
    }

    public SpacialThing set(Matrix4f transform) {
        transform.getUnnormalizedRotation(rotation);
        transform.getTranslation(translation);
        transform.getScale(scale);
        return this;
    }

    public void save(YAMLGenerator generator) throws IOException {
        generator.writeStartObject();
        WorldSerializer.writeObjectField("translation", translation, generator);
        WorldSerializer.writeObjectField("scale", scale, generator);
        WorldSerializer.writeObjectField("rotation", rotation, generator);
        generator.writeEndObject();
    }

}
