- Need to bind VAO for setVertexAttribPointer


# TODO:

- Learn about dynamic draw



## Input

### Keys
```
glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
});
```
- key: GLFW_KEY_<key>
- scancode: system key code used when key is unknown
- action: (state of the key) GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
- mods: (bit field) GLFW_MOD_SHIFT, GLFW_MOD_CONTROL, GLFW_MOD_ALT and GLFW_MOD_SUPER

### Cursor

- Capture input: glfwSetInputMode(window, GLFW_CURSOR, value)
- values: GLFW_CURSOR_NORMAL | GLFW_CURSOR_HIDDEN | GLFW_CURSOR_DISABLED

```
glfwSetMouseButtonCallback(window, mouseButtonCallback = 
        GLFWMouseButtonCallback.create((window, button, action, mods) -> {
    /* Do something */
}));
```
- action: GLFW_PRESS | GLFW_RELEASE

#### Cursor Postion
```
DoubleBuffer xpos = stack.mallocDouble(1);
DoubleBuffer ypos = stack.mallocDouble(1);
glfwGetCursorPos(window, xpos, ypos);

System.out.println("CursorPos: " + xpos.get() + "," + ypos.get());
```

#### Scroll
```
glfwSetScrollCallback(window, scrollCallback = GLFWScrollCallback.create((window, xoffset, yoffset) -> {
    /* Do something */
}));
```
