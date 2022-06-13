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
        // sample a bunch for points
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

}
