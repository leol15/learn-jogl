package com.play.app.scene.sceneobject;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.basics.*;
import com.play.app.geometry.Ray;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.CONST;

import org.joml.*;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Accessors(chain = true)
public class InstancingObject implements SceneObject {

    public final SORenderProperty property = new SORenderProperty();
    public final SOShape shape = new SOShape();

    private final Set<SpacialThing> instances = new HashSet<>();

    // helper
    private final Matrix4f tmpMatrix = new Matrix4f();

    public InstancingObject addInstance(final SpacialThing t) {
        instances.add(t);
        return this;
    }

    public InstancingObject removeInstance(final SpacialThing t) {
        instances.remove(t);
        return this;
    }

    @Override
    public void draw(Matrix4f transform) {

        property.bind(transform);

        for (final SpacialThing modelInfo : instances) {
            // override the instance model matrix
            if (property.shader != null) {
                modelInfo.getModelMatrix(tmpMatrix);
                tmpMatrix.mulLocal(transform);
                property.bind(tmpMatrix);
            }
            shape.draw();
        }

        property.unbind();
    }

    // return the first point of intersection
    @Override
    public Vector3f intersectRay(final Ray ray, final Matrix4f worldMatrix) {
        if (shape.collidable == null) {
            return null;
        }
        final Matrix4f tmpMat = new Matrix4f();
        for (final SpacialThing model : instances) {
            model.getModelMatrix(tmpMat);
            tmpMat.mulLocal(worldMatrix);

            final Vector3f intersect = shape.intersectRay(ray, tmpMat);
            if (intersect != null) {
                return intersect;
            }
        }

        return null;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        property.addToEditor(editor);
        shape.addToEditor(editor);
        // TODO not really good to edit all instance?
        if (instances.size() > 0) {
            for (SpacialThing s : instances) {
                editor.addProperty("instance 0", s);
                break;
            }
        }
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitInstancingObject(this, worldTransform);
    }

    @Override
    public void save(YAMLGenerator generator) throws IOException {
        generator.writeString("InstancingObject stub");

    }

    @Override
    public boolean hasTransparency() {
        return property.material.hasTransparency();
    }

}
