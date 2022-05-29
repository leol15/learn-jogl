package com.play.app.scene.camera;

/**
 * A way to interact/control the camera
 */
public interface CameraControl {

    // draw anything
    public void show();

    public void setCamera(Camera camera);

    ///////////////////
    // callbacks, return true indicates the event is handled
    ///////////////////
    public boolean onMouseButton(int button, int action, int mode);

    public boolean onCursorPos(double xpos, double ypos);

    public boolean onCursorEnter(boolean entered);

    public boolean onScroll(double xoffset, double yoffset);

    public boolean onKey(int key, int code, int action, int mods);

    public boolean onChar(int character);

}
