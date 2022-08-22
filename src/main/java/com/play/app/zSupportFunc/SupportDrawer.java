package com.play.app.zSupportFunc;

import com.play.app.mesh.Mesh;
import com.play.app.mesh.NormalMesh;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SupportDrawer {

    public static Mesh toMesh(SupportFunc f) {
        final NormalMesh mesh = new NormalMesh();
        // sample a bunch of points
        int count = 100;
        Vector3f prevP = null;
        for (int i = 0; i < count; i++) {
            final Vector3f dir = new Vector3f(
                    Math.cos(Math.toRadians(360f * i / count)),
                    Math.sin(Math.toRadians(360f * i / count)),
                    0);
            final Vector3f p = f.getMaxWorld(dir);
            // optimize, duplicate `p` is possible
            if (prevP != null && prevP.equals(p, 0.001f)) {
                continue;
            }
            mesh.addPos(p);
            mesh.addNormal(0, 0, 1);
            mesh.addElement(i);
        }
        mesh.setGlPrimitiveType(GL11.GL_TRIANGLE_FAN);
        mesh.uploadData();
        return mesh;
    }

    public static Mesh toLineMesh(SupportFunc f) {
        final NormalMesh mesh = new NormalMesh();
        // sample a 3D thing, slice it
        // sphere radius 1 centered at origin
        final Vector3f dir = new Vector3f();
        final int numLevels = 20;
        final int numSlices = 20;
        int vertexCt = 0;
        // bottom
        dir.set(0, -1, 0);
        mesh.addPos(f.getMaxWorld(dir));
        mesh.addElement(vertexCt);
        vertexCt++;
        // circle
        for (int i = 1; i < numLevels; i++) {
            float y = 2.0f * i / numLevels - 1;
            for (int j = 0; j <= numSlices; j++) {
                float angle = (float) (2 * Math.PI * j / numSlices);
                float x = Math.cos(angle);
                float z = Math.sin(angle);
                dir.set(x, y, z);
                final Vector3f p = f.getMaxWorld(dir);
                mesh.addPos(p);
                mesh.addElement(vertexCt);
                vertexCt++;
            }
        }
        // top
        dir.set(0, 1, 0);
        mesh.addPos(f.getMaxWorld(dir));
        mesh.addElement(vertexCt);
        vertexCt++;

        mesh.setGlPrimitiveType(GL11.GL_LINE_LOOP);
        mesh.uploadData();
        return mesh;
    }

}
