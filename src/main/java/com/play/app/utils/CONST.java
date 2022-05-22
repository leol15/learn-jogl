package com.play.app.utils;

public class CONST {

    public static final String VIEW_MATRIX = "view";
    public static final String PROJECTION_MATRIX = "projection";
    public static final String MODEL_MATRIX = "model";

    public static final String SHADER_COLOR = "color";

    public static final String SHADER_FOLDER = "resources/shaders/";
    public static final String SHADER_DEFAULT_FOLDER = SHADER_FOLDER + "essential/";

    public static final int SIZE_MAT4 = 4 * 4 * Float.BYTES;
    public static final int SIZE_VEC4 = 4 * Float.BYTES;
    // vec3 is still 16 bytes in GLSL
    public static final int SIZE_VEC3 = 4 * Float.BYTES;

    public static final int VERT_IN_POSITION = 0;
    public static final int VERT_IN_NORMAL = 1;
    public static final int VERT_IN_UV = 2;

    // indexed by above constant
    // [location in shader, size of vertex attribute (in number of floats)]
    public static final int[][] VERT_IN_ATTRS = { { 0, 3 }, { 1, 3 }, { 2, 2 } };

    public static final String UBO_CAMERA_INFO = "CAMERA_INFO";
    public static final String UBO_LIGHTS = "ALL_THE_LIGHTS";
    public static final String SHADER_SPECULAR_HARDNESS = "specularHardness";
}