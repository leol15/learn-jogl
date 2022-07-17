package com.play.app.utils;

import java.util.function.Consumer;

import com.play.app.collider.UnitCollider;
import com.play.app.graphics.UnitGeometries;
import com.play.app.graphics.UnitGeometries.Type;
import com.play.app.mesh.UnitMesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.lights.DirectionalLight;
import com.play.app.scene.lights.Light;
import com.play.app.scene.lights.PointLight;
import com.play.app.scene.lights.SpotLight;
import com.play.app.scene.sceneobject.LightSceneObject;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.ui.UIManager;
import com.play.app.ui.elements.AbstractContainer;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.DropdownList;

/**
 * Factory to support create scene objects
 */
public class SceneNodeFactory {

    private static final UnitGeometries.Type[] SHAPES = Type.values();

    public static void setupCreateButtons(UIManager uiManager, AbstractContainer uiContainer,
            Consumer<SceneNode> addFunc) {
        // shapes
        final DropdownList shapeDropdown = new DropdownList(uiManager);
        shapeDropdown.setDropDownLabel("Add Shapes");
        uiContainer.addChild(shapeDropdown);
        for (final Type t : SHAPES) {
            final Button createShapeBtn = new Button(uiManager, String.format("+ %s", t.name()));
            createShapeBtn.onClickEvent.addListener(btn -> {
                addFunc.accept(createShape(t.name(), t, "Simple3D"));
            });
            shapeDropdown.addItem(createShapeBtn);
        }

        // lights
        final DropdownList lightsDropdown = new DropdownList(uiManager);
        lightsDropdown.setDropDownLabel("Add Lights");
        uiContainer.addChild(lightsDropdown);
        addLightButton(uiManager, lightsDropdown, addFunc);
    }

    private static SceneNode createShape(String name, UnitGeometries.Type type, String shaderName) {
        final SceneNode node = new SceneNode().setName(name);
        final SimpleSceneObject so = new SimpleSceneObject();
        node.setSceneObject(so);

        so.property.setShader(ShaderUtils.getShader(shaderName));
        so.shape.setMesh(new UnitMesh(type))
                .setCollider(new UnitCollider(type));

        return node;
    }

    private static void addLightButton(UIManager uiManager, DropdownList lightsDropdown, Consumer<SceneNode> addFunc) {
        final Button dirLight = new Button(uiManager, "+ Dir Light");
        dirLight.onClickEvent.addListener(btn -> {
            addFunc.accept(createLight("Dir Light", new DirectionalLight()));
        });
        lightsDropdown.addItem(dirLight);

        final Button pointLight = new Button(uiManager, "+ Point Light");
        pointLight.onClickEvent.addListener(btn -> {
            addFunc.accept(createLight("Point Light", new PointLight()));
        });
        lightsDropdown.addItem(pointLight);

        final Button spotLight = new Button(uiManager, "+ Spot Light");
        spotLight.onClickEvent.addListener(btn -> {
            addFunc.accept(createLight("Spot Light", new SpotLight()));
        });
        lightsDropdown.addItem(spotLight);
    }

    private static SceneNode createLight(String name, Light light) {
        final SceneNode node = new SceneNode().setName(name);
        final LightSceneObject lso = new LightSceneObject(light);
        node.setSceneObject(lso);
        return node;
    }

}
