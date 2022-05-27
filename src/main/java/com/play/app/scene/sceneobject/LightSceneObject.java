package com.play.app.scene.sceneobject;

import com.play.app.scene.SceneObjectVisitor;
import com.play.app.scene.lights.Light;
import com.play.app.ui.editor.PropertyEditor;
import com.play.app.utils.*;

import org.joml.Matrix4f;

public class LightSceneObject extends SimpleSceneObject {

    private Light light;

    public LightSceneObject(Light light) {
        super();

        this.light = light;
        setMesh(light.getDebugMesh());
        setCollidable(light.getDebugCollidable());
        setShader(ShaderUtils.getShader("Simple3D"));
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void addToEditor(PropertyEditor editor) {
        light.addToEditor(editor);
    }

    @Override
    public void accept(SceneObjectVisitor visitor, Matrix4f worldTransform) {
        visitor.visitLightSceneObject(this, worldTransform);
    }

    // override the color to match the light color
    @Override
    public void draw(Matrix4f transform) {
        if (this.mesh == null) {
            return;
        }

        bindAll();

        if (shader != null) {
            shader.uniformMatrix4fv(CONST.MODEL_MATRIX, transform);
            // override color
            if (light != null) {
                shader.uniform4f(CONST.MATERIAL_COLOR, light.getColor());
            }
            shader.useProgram();
        }

        mesh.drawMesh();

        unbindAll();
    }
}
