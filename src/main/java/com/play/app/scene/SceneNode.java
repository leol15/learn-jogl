package com.play.app.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonToken;
import com.play.app.basics.Savable;
import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.CONST;
import com.play.app.utils.WorldSerializer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class SceneNode implements Savable {

    public final SpacialThing modelInfo = new SpacialThing();
    private final List<SceneNode> children = new ArrayList<>();
    private SceneNode parent = null;

    @Getter
    @Setter
    private SceneObject sceneObject;

    public SceneNode addChild(final SceneNode child) {
        children.add(child);
        child.parent = this;
        return this;
    }

    public SceneNode removeChild(final SceneNode child) {
        children.remove(child);
        child.parent = null;
        return this;
    }

    public SceneNode createChild() {
        final SceneNode child = new SceneNode();
        addChild(child);
        return child;
    }

    public int getNumChildren() {
        return children.size();
    }

    public List<SceneNode> getChildren() {
        return new ArrayList<>(children);
    }

    public void draw(final Matrix4f transform) {
        treeTransformVisitor(this, transform, (node, newTransform) -> {
            if (node.sceneObject != null) {
                if (node.sceneObject.hasTransparency() ^ CONST.drawTransparent) {
                    // not draw
                    return;
                }
                node.sceneObject.draw(newTransform);
            }
        });
    }

    public SceneNode castRay(final Ray ray) {
        final Matrix4f objMat = new Matrix4f();
        final SceneNode[] bestNode = { null };
        final float[] bestDist = { Float.POSITIVE_INFINITY };

        treeTransformVisitor(this, objMat, (node, newTransform) -> {
            if (node.sceneObject == null) {
                return;
            }
            final Vector3f intersect = node.sceneObject.intersectRay(ray, newTransform);
            if (intersect == null) {
                return;
            }

            final float dist = ray.start.distance(intersect);
            if (dist < bestDist[0]) {
                bestDist[0] = dist;
                bestNode[0] = node;
            }
        });

        return bestNode[0];
    }

    public Vector3f getPosition(final Vector3f position) {
        SceneNode n = this;
        final Matrix4f transform = new Matrix4f();
        final Vector4f position4 = new Vector4f(0, 0, 0, 1);
        while (n != null) {
            n.modelInfo.getModelMatrix(transform);
            position4.mul(transform);
            n = n.parent;
        }
        return position.set(position4.x, position4.y, position4.z);
    }

    public void accept(SceneObjectVisitor sceneVisitor) {
        final Matrix4f identity = new Matrix4f();
        treeTransformVisitor(this, identity, (node, newTransform) -> {
            if (node.sceneObject != null) {
                node.sceneObject.accept(sceneVisitor, newTransform);
            }
        });
    }

    public void select(PropertyEditor editor) {
        editor.addProperty("SceneNode", modelInfo);
        if (sceneObject != null) {
            sceneObject.addToEditor(editor);
        }
    }

    public void deselect(PropertyEditor editor) {
        // stub
    }

    ///////////////
    // helper
    ///////////////

    /**
     * traverse the scenenode tree from root, while updating transform matrix along
     * 
     * @param node      root
     * @param transform starting transform matrix
     * @param process   the work of the visitor
     */
    private static void treeTransformVisitor(final SceneNode node, final Matrix4f transform,
            BiConsumer<SceneNode, Matrix4f> process) {

        final Matrix4f tmpMat = new Matrix4f();
        final Matrix4f tmpMatCopy = new Matrix4f();

        // apply current transform
        node.modelInfo.getModelMatrix(tmpMat);
        tmpMat.mulLocal(transform);
        tmpMatCopy.set(tmpMat);

        // work
        process.accept(node, tmpMat);

        for (final SceneNode child : node.children) {
            // apply current transform
            tmpMat.set(tmpMatCopy);
            treeTransformVisitor(child, tmpMat, process);
        }
    }

    @Override
    public void save(WorldSerializer writer) throws IOException {
        writer.writeStartObject();

        writer.writeObjectField("modelInfo", modelInfo);

        writer.writeArrayFieldStart("children");
        for (final SceneNode child : children) {
            child.save(writer);
        }
        writer.writeEndArray();

        writer.writeInterfaceField("sceneObject", sceneObject);

        writer.writeEndObject();
    }

    public static SceneNode create(WorldSerializer reader) throws IOException {
        final SceneNode node = new SceneNode();
        reader.consumeStartObject();

        reader.consumeObjectField("modelInfo", node.modelInfo);

        reader.consumeArrayFieldStart("children");
        while (reader.currentToken() != JsonToken.END_ARRAY) {
            final SceneNode child = SceneNode.create(reader);
            child.parent = node;
            node.children.add(child);
        }
        reader.consumeEndArray();

        node.sceneObject = (SceneObject) reader.consumeInterfaceField("sceneObject");

        reader.consumeEndObject();
        return node;
    }

}
