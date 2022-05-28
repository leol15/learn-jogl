package com.play.app.basics;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.*;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpacialThing implements Savable, Loadable {

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

    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();
        writer.writeObjectField("translation", translation);
        writer.writeObjectField("scale", scale);
        writer.writeObjectField("rotation", rotation);
        writer.writeEndObject();
    }

    public void load(WorldSerializer reader) throws IOException {
        reader.consumeStartObject();
        reader.consumeObjectField("translation", translation);
        reader.consumeObjectField("scale", scale);
        reader.consumeObjectField("rotation", rotation);
        reader.consumeEndObject();
    }

}
