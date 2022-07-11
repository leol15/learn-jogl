package com.play.app.utils;

import java.util.function.Consumer;

import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.UnitMesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.sceneobject.SOShape;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.AbstractContainer;
import com.play.app.ui.elements.Button;

/**
 * Factory to support create scene objects
 */
public class SceneNodeFactory {

    private static final UnitGeometries.Type[] SHAPES = Type.values();

    public static void setupCreateButtons(UIManager uiManager, AbstractContainer uiContainer,
            Consumer<SceneNode> addFunc) {
        for (final Type t : SHAPES) {
            final Button createShapeBtn = new Button(uiManager, String.format("+ %s", t.name()));
            createShapeBtn.onClickEvent.addListener(btn -> {
                addFunc.accept(create(t.name(), t, "Simple3D"));
            });
            uiContainer.addChild(createShapeBtn);
        }
    }

    public static SceneNode createCube() {
        return create("Cube", UnitGeometries.Type.Cube, "Simple3D");
    }

    private static SceneNode create(String name, UnitGeometries.Type type, String shaderName) {
        final SceneNode node = new SceneNode().setName(name);
        final SimpleSceneObject so = new SimpleSceneObject();
        node.setSceneObject(so);

        configureShape(so.shape, type);
        so.property.shader = ShaderUtils.getShader(shaderName);

        return node;
    }

    private static void configureShape(SOShape shape, UnitGeometries.Type type) {
        shape.setMesh(new UnitMesh(type))
                .setCollider(new UnitCollider(type));
    }
}
