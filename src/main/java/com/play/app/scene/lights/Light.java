package com.play.app.scene.lights;

import com.play.app.basics.*;
import com.play.app.collider.Collider;
import com.play.app.mesh.Mesh;

import org.joml.*;

public interface Light extends Editable, Savable {
    public void addToUBO(Matrix4f worldTransform);

    public Vector4f getColor();

    // what is shown
    public Mesh getDebugMesh();

    public Collider getDebugCollider();

}
