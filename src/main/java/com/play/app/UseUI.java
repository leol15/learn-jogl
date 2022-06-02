package com.play.app;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.play.app.physics.TimeStepEngine;
import com.play.app.scene.SceneNode;
import com.play.app.ui.UIManager;
import com.play.app.ui.editor.SceneTreeView;
import com.play.app.ui.editor.VectorXfEditor;
import com.play.app.ui.elements.Button;
import com.play.app.ui.elements.ContainerH;
import com.play.app.ui.elements.ContainerV;
import com.play.app.ui.elements.DropdownList;
import com.play.app.ui.elements.Padding;
import com.play.app.ui.elements.TextInput;
import com.play.app.ui.elements.UIAligner;
import com.play.app.ui.elements.UIText;
import com.play.app.ui.elements.UITransformer;
import com.play.app.ui.elements.UIAligner.Alignment;
import com.play.app.utils.SceneManager;
import com.play.app.utils.WindowManager;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

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
        row.addChild(new UIText(uiManager, "Row text"));
        row.addChild(new Button(uiManager, "Button 1"));
        row.addChild(new TextInput(uiManager, "input 1"));

        final Padding padding = new Padding(uiManager);
        padding.bgColor.set(1, 0, 0);
        final UITransformer transformer = new UITransformer(uiManager);
        transformer.setTarget(padding);
        transformer.scale.set(1.5, 1.2);
        transformer.translation.set(50, 20);
        row.addChild(transformer);

        final DropdownList dropdownList = new DropdownList(uiManager);
        dropdownList.addItem(new Padding(uiManager));
        dropdownList.addItem(new Padding(uiManager));
        dropdownList.addItem(new Padding(uiManager));
        row.addChild(dropdownList);

        row.addChild(new Padding(uiManager));

        final SceneTreeView sceneTreeView = new SceneTreeView(uiManager);
        final SceneNode sceneNode = new SceneNode();
        sceneNode.createChild();
        sceneNode.createChild();
        sceneNode.createChild().createChild().createChild();
        sceneNode.createChild();
        sceneTreeView.setSceneNode(sceneNode);
        final UIAligner uiAligner = new UIAligner(uiManager, sceneTreeView);
        uiAligner.setWidth(500);
        uiAligner.setHorizontalAlignment(Alignment.START);
        uiAligner.setHeight(0);
        uiAligner.bgColor.set(0, 0.3, 0);
        row.addChild(uiAligner);

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
        vEditor.setScrollDelta(0.01f);

        final Button button2 = new Button(uiManager, "Button 2");
        box.addChild(button2);
        button2.onClickEvent.addListener(e -> {
            log.info("vector is now {}", v3);
            vEditor.clear();
        });

        // box.addChild(new SpacialThingEditor(uiManager));

        // uiManager.roots.add(box);

        while (!glfwWindowShouldClose(window)) {
            // loop
            glfwSwapBuffers(window);

            sceneManager.render();

            // GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            // uiManager.draw();

            glfwPollEvents();
        }

        timeStepEngine.terminated = true;

    }
}
