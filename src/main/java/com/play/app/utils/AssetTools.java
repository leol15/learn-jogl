
package com.play.app.utils;


import java.io.*;
import java.util.*;


public class AssetTools {

    // loads a file as text from /resources folder
    public static String loadResourcesTextFile(String path) {
        InputStream in = AssetTools.class.getResourceAsStream(path);
        if (in == null) {
            System.err.println("Cannot find resource: " + path);
            return null;
        }
        try (Scanner s = new Scanner(in).useDelimiter("\\A")) {
            String text = s.hasNext() ? s.next() : null;
            return text;
        }
    }

    // loads any text file as string
    public static String loadTextFile(String path) {
        try {
            InputStream in = new FileInputStream(new File(path));
            try (Scanner s = new Scanner(in).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : null;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file: " + path);
            return null;
        }
    }

}
