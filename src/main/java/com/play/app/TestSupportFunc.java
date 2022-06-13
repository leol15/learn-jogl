package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;
import java.util.List;

import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.sceneobject.SimpleSceneObject;
import com.play.app.utils.SceneManager;
import com.play.app.utils.ShaderUtils;
import com.play.app.utils.WindowManager;
import com.play.app.zSupportFunc.GJKSolver;
import com.play.app.zSupportFunc.SupportDrawer;
import com.play.app.zSupportFunc.SupportFunc;
import com.play.app.zSupportFunc.funcs.CircleSupp;
import com.play.app.zSupportFunc.funcs.CompositeSupp;
import com.play.app.zSupportFunc.funcs.SquareSupp;
import com.play.app.zSupportFunc.funcs.TriangleSupp;

import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestSupportFunc {

    private final CompositeSupp compositeSupp;
    private final SimpleSceneObject compositeSO;
    private final SceneNode circleNode, squareNode, triangleNode;

    public TestSupportFunc(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);

        final SupportFunc square = new SquareSupp();
        squareNode = createSO(root, square);
        square.setModel(squareNode.modelInfo);
        squareNode.modelInfo.translation.set(-0.8, -0.7, -0.5);

        final TriangleSupp triangle = new TriangleSupp();
        triangleNode = createSO(root, triangle);
        triangle.setModel(triangleNode.modelInfo);
        triangleNode.modelInfo.translation.x = 2;

        final CircleSupp circle = new CircleSupp();
        // circleNode = null;
        circleNode = createSO(root, circle);
        circle.setModel(circleNode.modelInfo);

        // for collision
        final List<SupportFunc> shapes = new ArrayList<>();
        shapes.add(square);
        shapes.add(circle);
        final List<SceneNode> shapeNodes = new ArrayList<>();
        shapeNodes.add(squareNode);
        shapeNodes.add(circleNode);

        compositeSupp = new CompositeSupp();
        compositeSupp.add(square);
        // compositeSupp.subtract(triangle);
        // compositeSupp.subtract(square);
        compositeSupp.subtract(circle);

        // debug
        final Vector3f pOnComp = compositeSupp.getMaxWorld(new Vector3f(1, -2, 1).normalize());
        log.debug("pOnComp {}", pOnComp);

        compositeSO = (SimpleSceneObject) createSO(root, compositeSupp).getSceneObject();

        sceneManager.rootNodeUpdate();

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            // draw some 2D support functions

            sceneManager.render();

            recomputeComposite();
            // sceneManager.rootNodeUpdate();
            solveCollisions(shapes, shapeNodes);

            glfwPollEvents();
        }
    }

    private SceneNode createSO(SceneNode root, SupportFunc f) {
        final SimpleSceneObject so = new SimpleSceneObject();
        so.property.shader = ShaderUtils.getShader("Simple3D");
        so.shape.mesh = SupportDrawer.toMesh(f);
        return root.createChild().setSceneObject(so);
    }

    private void recomputeComposite() {
        compositeSO.shape.mesh.destroy();
        compositeSO.shape.mesh = SupportDrawer.toMesh(compositeSupp);
    }

    private void solveCollisions(List<SupportFunc> shapes, List<SceneNode> nodes) {
        for (int i = 0; i < shapes.size(); i++) {
            ((SimpleSceneObject) nodes.get(i).getSceneObject()).property.material.color.x = 1;
        }
        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                final SupportFunc A = shapes.get(i);
                final SupportFunc B = shapes.get(j);
                if (GJKSolver.intersect(A, B)) {
                    // draw a color for them
                    ((SimpleSceneObject) nodes.get(i).getSceneObject()).property.material.color.x = 0;
                    ((SimpleSceneObject) nodes.get(j).getSceneObject()).property.material.color.x = 0;
                }
                GJKSolver.drawDebugSimpelx();
            }
        }
    }
}
