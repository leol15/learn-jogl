package com.play.app.scene;

import java.util.*;
import java.util.function.BiConsumer;

import com.play.app.basics.*;
import com.play.app.geometry.Ray;
import com.play.app.ui.PropertyEditor;

import org.joml.*;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Accessors(chain = true)
public class SceneNode {

    public final SpacialThing modelInfo = new SpacialThing();

    private Set<SceneNode> children = new HashSet<>();

    @Getter
    @Setter
    private SceneObject sceneObject;

    public SceneNode addChild(final SceneNode child) {
        children.add(child);
        return this;
    }

    public SceneNode removeChild(final SceneNode child) {
        children.remove(child);
        return this;
    }

    public SceneNode createChild() {
        final SceneNode child = new SceneNode();
        children.add(child);
        return child;
    }

    public void draw(final Matrix4f transform) {
        treeTransformVisitor(this, transform, (node, newTransform) -> {
            if (node.sceneObject != null) {
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

    public void accept(SceneVisitor sceneVisitor) {
        final Matrix4f identity = new Matrix4f();
        treeTransformVisitor(this, identity, (node, newTransform) -> {
            if (node.sceneObject != null) {
                node.sceneObject.accept(sceneVisitor, newTransform);
            }
        });
    }

    public void select(PropertyEditor editor) {
        editor.addProperty("SceneNode", modelInfo);
        sceneObject.addToEditor(editor);
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

}
