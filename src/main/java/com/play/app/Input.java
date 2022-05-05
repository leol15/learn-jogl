package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL33.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL32.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.awt.Color;

import com.play.app.basics.SpacialThing;
import com.play.app.geometry.Cube;
import com.play.app.graphics.ShaderProgram;
import com.play.app.graphics.Text;
import com.play.app.mesh.Mesh;
import com.play.app.scene.SceneNode;
import com.play.app.scene.SceneObject;
import com.play.app.ui.Button;
import com.play.app.ui.CameraControl;
import com.play.app.ui.WindowManager;
import com.play.app.utils.CONST;
import com.play.app.utils.Func;

import org.joml.Matrix4f;

public class Input {

    public Input(long window) {

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        final WindowManager windowManager = new WindowManager(window);
        final CameraControl cam = new CameraControl(windowManager);
        final ShaderProgram simple3dShader = new ShaderProgram()
                .withShader(CONST.SHADER_FOLDER + "simple3D.vert", GL_VERTEX_SHADER)
                .withShader(CONST.SHADER_FOLDER + "/Simple3D.geom", GL_GEOMETRY_SHADER)
                .withShader(CONST.SHADER_FOLDER + "simple3D.frag", GL_FRAGMENT_SHADER)
                .linkProgram();

        final Matrix4f iden = new Matrix4f();
        final SceneNode rootNode = new SceneNode();
        final SpacialThing cube1Transform = new SpacialThing();

        SceneNode cube1 = new SceneNode().setSceneObject(
                new SceneObject()
                        .setShader(simple3dShader)
                        .setCollidable(new Cube())
                        .setMesh(Mesh.CUBE)
                        .addInstance(cube1Transform));
        rootNode.addChild(cube1);

        Text fpsCounter = new Text(windowManager, "FPS: 1", 200, 200);
        fpsCounter.setColor(Color.RED);
        double previousTime = 0;

        Button button = new Button(windowManager, 600, 300, 100f, 100f);
        button.setColor(Color.GREEN);
        Button button2 = new Button(windowManager, 100, 100, 100f, 100f);
        Button button3 = new Button(windowManager, 300, 300, "Yoooo");

        int[] tmp = new int[1];
        button.setAction(() -> {
            System.out.println("Hello world button");
            tmp[0]++;
            button.setColor(tmp[0] % 2 == 0 ? Color.RED : Color.GREEN);
        });

        glClearColor(0.12f, 0.12f, 0.12f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // loop
            double time = glfwGetTime();
            glfwSwapBuffers(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // draw
            cam.setViewAndProjection(simple3dShader);
            rootNode.draw(iden);
            cam.draw();

            float fps = (float) (1 / (time - previousTime));
            fpsCounter.setText(String.format("FPS: %.2f", fps), 200, 200);
            fpsCounter.draw();
            previousTime = time;

            button.show();
            button2.show();
            button3.show();

            glfwPollEvents();
        }
    }

}
