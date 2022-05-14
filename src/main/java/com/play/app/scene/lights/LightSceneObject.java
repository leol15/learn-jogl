package com.play.app.scene.lights;

import com.play.app.geometry.*;
import com.play.app.graphics.ShaderProgram;
import com.play.app.mesh.Mesh;
import com.play.app.scene.*;
import com.play.app.ui.PropertyEditor;
import com.play.app.utils.CONST;

import org.joml.*;

public class LightSceneObject extends SimpleSceneObject {
    private PointLight pointLight;

    public LightSceneObject(ShaderProgram lineShader) {
        super();
        setMesh(Mesh.CIRCLE);
        setCollidable(new Sphere());
        setShader(lineShader);

        pointLight = new PointLight();

    }
}
