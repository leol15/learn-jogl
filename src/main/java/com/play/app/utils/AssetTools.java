
package com.play.app.utils;


import java.nio.*;
import java.io.*;
import java.util.*;


public class AssetTools {

    // loads a file as text from /resources folder
    public static String loadTextFile(String path) {
        InputStream in = AssetTools.class.getResourceAsStream(path);
        if (in == null) {
            System.err.println("Cannot find resource: " + path);
            return null;
        }
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : null;
    }
}
