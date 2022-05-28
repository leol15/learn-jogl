package com.play.app.utils;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.play.app.basics.SaveLoad;
import com.play.app.scene.SceneNode;

import org.joml.*;

public class WorldSerializer {

    public static final String OBJ_TYPE = "OBJ_TYPE";

    public void save(String path, SceneNode root) {
        try {
            save(new PrintStream(new File(path)), root);
            // save(System.out, root);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(OutputStream out, SceneNode root) {
        YAMLFactory factory = new YAMLFactory();
        try {
            YAMLGenerator generator = factory.createGenerator(out);
            generator.setCodec(new ObjectMapper());
            generator.writeStartObject();
            generator.writeStringField("WORLD_FILE", "v1");
            generator.writeFieldName("root");
            root.save(generator);
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SceneManager load(String path) {
        return null;

        // Func.p("");

        // try {
        //     YAMLParser parser = factory.createParser(new File("test-scene.yaml"));
        //     // parser.setCodec(new ObjectMapper());
        //     parser.nextToken();
        //     while (JsonToken.END_OBJECT != parser.nextToken()) {
        //         System.out.println(parser.getCurrentName());
        //         parser.nextToken();
        //         System.out.println(parser.getText());
        //     }
        //     // final String name = parser.getCurrentName();

        //     // Func.p("name is " + name);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public static void writeObjectType(Class<?> clazz, YAMLGenerator generator) throws IOException {
        generator.writeStringField(WorldSerializer.OBJ_TYPE, clazz.getName());
    }

    public static void writeObjectField(String name, SaveLoad obj, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        if (obj != null) {
            obj.save(generator);
        } else {
            generator.writeNull();
        }
    }

    public static void writeObjectField(String name, Vector3f obj, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        generator.writeString(toYAML(obj));
    }

    public static void writeObjectField(String name, Vector4f obj, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        generator.writeString(toYAML(obj));
    }

    public static void writeObjectField(String name, Quaternionf obj, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        generator.writeString(toYAML(obj));
    }

    public static void writeObjectField(String name, float f, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        generator.writeNumber(f);
    }

    public static void writeObjectField(String name, boolean b, YAMLGenerator generator) throws IOException {
        generator.writeFieldName(name);
        generator.writeBoolean(b);
    }

    private static String toYAML(Quaternionf q) {
        return String.format("%.3f, %.3f, %.3f, %.3f", q.x, q.y, q.z, q.w);
    }

    private static String toYAML(Vector3f v) {
        return String.format("%.3f, %.3f, %.3f", v.x, v.y, v.z);
    }

    private static String toYAML(Vector4f v) {
        return String.format("%.3f, %.3f, %.3f, %.3f", v.x, v.y, v.z, v.w);
    }

}
