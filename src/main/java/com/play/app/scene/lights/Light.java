package com.play.app.scene.lights;

import com.play.app.basics.*;
import com.play.app.mesh.Mesh;

import org.joml.*;

public interface Light extends Editable {
    public void addToUBO(Matrix4f worldTransform);

    // what is shown
    public Mesh getDebugMesh();
}
