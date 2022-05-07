
package com.play.app.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

import javax.imageio.ImageIO;

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

    public static void saveBufferedImage(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
