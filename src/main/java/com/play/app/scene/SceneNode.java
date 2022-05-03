package com.play.app.scene;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import com.play.app.basics.Drawable;
import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Ray;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SceneNode implements Drawable {

    public final SpacialThing modelInfo = new SpacialThing();

    private Set<SceneNode> children = new HashSet<>();
    private SceneObject sceneObject;

    public SceneNode setSceneObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
        return this;
    }

    public SceneNode addChild(final SceneNode child) {
        children.add(child);
        return this;
    }

    public SceneNode removeChild(final SceneNode child) {
        children.remove(child);
        return this;
    }

    @Override
    public void draw(final Matrix4f transform) {
        treeTransformVisitor(this, transform, (node, newTransform) -> {
            if (node.sceneObject != null) {
                node.sceneObject.draw(newTransform);
            }
        });
    }

    public SceneNode castRay(final Ray ray) {
        final Matrix4f tmpMat = new Matrix4f();
        final SceneNode[] bestNode = { null };
        final float[] bestDist = { Float.POSITIVE_INFINITY };

        treeTransformVisitor(this, tmpMat, (node, newTransoform) -> {
            if (node.sceneObject == null) {
                return;
            }

            final Vector3f intersect = node.sceneObject.castRay(ray, newTransoform);
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

    /**
     * traverse the scenenode tree from root, while updating transform matrix along
     * 
     * @param node      root
     * @param transform starting transform matrix
     * @param process   the work of the visitor
     */
    public static void treeTransformVisitor(final SceneNode node, final Matrix4f transform,
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
