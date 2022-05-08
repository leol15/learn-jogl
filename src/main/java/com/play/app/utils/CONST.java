package com.play.app.utils;

public class CONST {

    public static final String VIEW_MATRIX = "view";
    public static final String PROJECTION_MATRIX = "projection";
    public static final String MODEL_MATRIX = "model";

    public static final String SHADER_COLOR = "color";

    public static final String SHADER_FOLDER = "resources/shaders/";

    public static final int VERT_IN_POSITION = 0;
    public static final int VERT_IN_NORMAL = 1;
    public static final int VERT_IN_UV = 2;

    // indexed by above constant
    // [location in shader, size of vertex attribute (in number of floats)]
    public static final int[][] VERT_IN_ATTRS = { { 0, 3 }, { 1, 3 }, { 2, 2 } };
}