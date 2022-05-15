package com.play.app.scene.sceneobject;

import com.play.app.geometry.Sphere;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneVisitor;
import com.play.app.scene.lights.*;

import org.joml.Matrix4f;

import lombok.Getter;

public class LightSceneObject extends SimpleSceneObject {

    private Light light;

    public LightSceneObject(ShaderProgram lineShader) {
        super();
        setMesh(Mesh.CIRCLE);
        setCollidable(new Sphere());
        setShader(lineShader);

        // debug
        light = new PointLight();
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void accept(SceneVisitor visitor, Matrix4f worldTransform) {
        visitor.visitLightSceneObject(this, worldTransform);
    }
}
