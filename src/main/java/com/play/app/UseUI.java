package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.physics.TimeStepEngine;
import com.play.app.scene.SceneNode;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.VectorXfEditor;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.Padding;
import com.play.app.ui.elements.TextInput;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.UIText;
import com.play.app.utils.SceneManager;
import com.play.app.utils.WindowManager;

import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UseUI {

    public UseUI(long window) {

        final WindowManager windowManager = new WindowManager(window);
        final SceneNode root = new SceneNode();
        final SceneManager sceneManager = new SceneManager(windowManager, root);
        final TimeStepEngine timeStepEngine = new TimeStepEngine();

        final UIManager uiManager = sceneManager.uiManager;
        // final UIManager uiManager = new UIManager(windowManager);

        final ContainerV box = new ContainerV(uiManager);
        box.addChild(new Padding(uiManager));
        box.addChild(new Padding(uiManager));
        box.addChild(new Padding(uiManager));

        final ContainerH row = new ContainerH(uiManager);
        row.addChild(new Padding(uiManager));
        row.addChild(new Padding(uiManager));
        row.addChild(new Padding(uiManager));
        row.addChild(new Padding(uiManager));
        row.addChild(new UIText(uiManager, "Row text"));
        row.addChild(new Button(uiManager, "Button 1"));
        row.addChild(new TextInput(uiManager, "input 1"));
        box.addChild(row);

        box.addChild(new Padding(uiManager));
        box.addChild(new Padding(uiManager));
        box.addChild(new Padding(uiManager));
        box.addChild(new UIText(uiManager, "hello world"));

        final TextInput input2 = new TextInput(uiManager, "2");
        input2.setWidth(100);
        input2.setScrollable(true);
        input2.setScrollDelta(0.1f);
        box.addChild(input2);

        final VectorXfEditor vEditor = new VectorXfEditor(uiManager);
        box.addChild(vEditor);
        final Vector3f v3 = new Vector3f(1, 2, 3);
        vEditor.setVector(v3);

        final Button button2 = new Button(uiManager, "Button 2");
        box.addChild(button2);
        button2.onClickEvent.addListener(e -> {
            log.info("vector is now {}", v3);
            vEditor.clear();
        });

        uiManager.roots.add(box);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);
            sceneManager.render();

            glfwPollEvents();

            // glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            // uiManager.draw();
        }

        timeStepEngine.terminated = true;

    }
}
