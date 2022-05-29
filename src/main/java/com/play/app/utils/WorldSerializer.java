package com.play.app.utils;

import java.io.*;
import java.lang.reflect.*;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.*;
import com.play.app.basics.*;
import com.play.app.scene.*;
import com.play.app.scene.camera.Camera;

import org.joml.*;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WorldSerializer {

    @AllArgsConstructor
    public class GlobalData {
        // some scene node construction need these
        public final Camera camera;
    }

    public final GlobalData GLOBAL_DATA;

    public WorldSerializer(Camera camera) {
        GLOBAL_DATA = new GlobalData(camera);
    }

    public static final String OBJ_TYPE = "OBJ_TYPE";
    public static final String OBJ_DATA = "OBJ_DATA";
    public static final String FILE_VERSION = "FILE_VERSION";
    private final YAMLFactory factory = new YAMLFactory();
    private YAMLGenerator generator;
    private YAMLParser parser;

    public void save(String path, SceneNode root) {
        try {
            save(new PrintStream(new File(path)), root);
            // save(System.out, root);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(OutputStream out, SceneNode root) {
        try {
            generator = factory.createGenerator(out);
            generator.setCodec(new ObjectMapper());
            generator.writeStartObject();
            generator.writeStringField(FILE_VERSION, "v1");
            generator.writeFieldName("root");
            root.save(this);
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SceneNode load(String path) {
        try {
            log.info("begin loading file {}", path);
            parser = factory.createParser(new File(path));

            // header
            nextToken();
            consumeStartObject();
            final String v1 = consumeStringField(FILE_VERSION);
            log.info("{}: {}", FILE_VERSION, v1);
            consumeFieldName("root");

            final SceneNode n = SceneNode.create(this);
            parser.close();
            return n;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /////////////////////
    // save/load pairs
    /////////////////////

    public void writeStartObject() throws IOException {
        generator.writeStartObject();
    }

    public void consumeStartObject() throws IOException {
        if (!parser.isExpectedStartObjectToken()) {
            log.error("Expecting { but is {}", parser.getText());
        }
        nextToken();
    }

    public void writeEndObject() throws IOException {
        generator.writeEndObject();
    }

    public void consumeEndObject() throws IOException {
        if (!(parser.currentToken() == JsonToken.END_OBJECT)) {
            log.error("Expecting } but is {}", parser.getText());
        }
        nextToken();
    }

    public void writeFieldName(String name) throws IOException {
        generator.writeFieldName(name);
    }

    public void consumeFieldName(String name) throws IOException {
        if (!parser.getText().equals(name)) {
            log.error("YAML parsing error, expecting field {} but is {}", name, parser.getText());
        }
        // log.debug("comsumed {} [{}]", name, parser.getCurrentLocation());
        parser.nextValue();
    }

    public void writeString(String s) throws IOException {
        generator.writeString(s);
    }

    public String consumeString() throws IOException {
        final String s = parser.getValueAsString();
        nextToken();
        return s;
    }

    public void writeStartArray() throws IOException {
        generator.writeStartArray();
    }

    public void consumeStartArray() throws IOException {
        if (!parser.isExpectedStartArrayToken()) {
            log.error("expection [ but is {}", parser.getText());
        }
        nextToken();
    }

    public void writeEndArray() throws IOException {
        generator.writeEndArray();
    }

    public void consumeEndArray() throws IOException {
        if (currentToken() != JsonToken.END_ARRAY) {
            log.error("expection ] but is {}", parser.getText());
        }
        nextToken();
    }

    public void writeArrayFieldStart(String name) throws IOException {
        generator.writeArrayFieldStart(name);
    }

    public void consumeArrayFieldStart(String name) throws IOException {
        consumeFieldName(name);
        consumeStartArray();
    }

    public void writeObjectField(String name, String value) throws IOException {
        generator.writeFieldName(name);
        generator.writeString(value);
    }

    public String consumeStringField(String name) throws IOException {
        consumeFieldName(name);
        return consumeString();
    }

    public void writeObjectField(String name, Vector3f obj) throws IOException {
        writeObjectField(name, toStringRep(obj));
    }

    public void consumeObjectField(String name, Vector3f out) throws IOException {
        fromStringRep(out, consumeStringField(name));
    }

    public void writeObjectField(String name, Vector4f obj) throws IOException {
        writeObjectField(name, toStringRep(obj));
    }

    public void consumeObjectField(String name, Vector4f out) throws IOException {
        fromStringRep(out, consumeStringField(name));
    }

    public void writeObjectField(String name, Quaternionf obj) throws IOException {
        writeObjectField(name, toStringRep(obj));
    }

    public void consumeObjectField(String name, Quaternionf out) throws IOException {
        fromStringRep(out, consumeStringField(name));
    }

    public void writeObjectField(String name, int i) throws IOException {
        generator.writeFieldName(name);
        generator.writeNumber(i);
    }

    public int consumeIntField(String name) throws IOException {
        consumeFieldName(name);
        return consumeInt();
    }

    public void writeObjectField(String name, float f) throws IOException {
        generator.writeFieldName(name);
        generator.writeNumber(f);
    }

    public float consumeFloatField(String name) throws IOException {
        consumeFieldName(name);
        return consumeFloat();
    }

    public void writeObjectField(String name, boolean b) throws IOException {
        generator.writeFieldName(name);
        generator.writeBoolean(b);
    }

    public boolean consumeBooleanField(String name) throws IOException {
        consumeFieldName(name);
        final boolean b = parser.getBooleanValue();
        nextToken();
        return b;
    }

    //////////////////////////////
    // save / load for interfaces
    //////////////////////////////

    public void writeInterfaceField(String name, Savable interfase) throws IOException {
        writeFieldName(name);
        if (interfase == null) {
            generator.writeNull();
        } else {
            writeInterface(interfase);
        }
    }

    public Object consumeInterfaceField(String name) throws IOException {
        consumeFieldName(name);
        if (currentToken() == JsonToken.VALUE_NULL) {
            nextToken();
            return null;
        } else {
            return consumeInterface();
        }
    }

    public void writeInterface(@NonNull Savable interfase) throws IOException {
        writeStartObject();
        writeObjectField(OBJ_TYPE, toClassString(interfase.getClass()));
        writeFieldName(OBJ_DATA);
        writeObject(interfase);
        writeEndObject();
    }

    public Object consumeInterface() throws IOException {
        Object o = null;
        consumeStartObject();
        final String clazz = consumeStringField(OBJ_TYPE);
        consumeFieldName(OBJ_DATA);
        try {
            Class<?> interfase = Class.forName(clazz);
            // TODO, redesign?
            final Method createMethod = interfase.getMethod("create", new Class<?>[] { WorldSerializer.class });
            o = createMethod.invoke(null, this);
        } catch (Exception e) {
            log.error("cannot find class or class does not support create: {}", clazz);
            e.printStackTrace();
        }
        consumeEndObject();
        return o;
    }

    ///////////////////
    // one way helpers
    ///////////////////

    public void writeObjectField(String name, Savable obj) throws IOException {
        writeFieldName(name);
        if (obj != null) {
            writeObject(obj);
        } else {
            generator.writeNull();
        }
    }

    public void consumeObjectField(String name, Loadable obj) throws IOException {
        consumeFieldName(name);
        obj.load(this);
    }

    public void writeObject(@NonNull Savable obj) throws IOException {
        obj.save(this);
    }

    public void writeObjectType(Class<?> clazz) throws IOException {
        generator.writeStringField(WorldSerializer.OBJ_TYPE, toClassString(clazz));
    }

    public int consumeInt() throws IOException {
        final int i = parser.getIntValue();
        nextToken();
        return i;
    }

    public float consumeFloat() throws IOException {
        final float f = parser.getFloatValue();
        nextToken();
        return f;
    }

    ///////////////////////////////
    // exposed generator functions
    ///////////////////////////////

    public JsonToken currentToken() throws IOException {
        return parser.currentToken();
    }

    public JsonToken nextToken() throws IOException {
        return parser.nextToken();
    }

    ///////////////////////////
    // helper for joml objects
    ///////////////////////////

    private static String toClassString(Class<?> clazz) {
        return clazz.getName();
    }

    private static void fromStringRep(Vector3f v, String str) {
        final Scanner s = new Scanner(str);
        v.x = s.nextFloat();
        v.y = s.nextFloat();
        v.z = s.nextFloat();
        s.close();
    }

    private static String toStringRep(Vector3f v) {
        return String.format("%.3f %.3f %.3f", v.x, v.y, v.z);
    }

    private static void fromStringRep(Vector4f v, String str) {
        final Scanner s = new Scanner(str);
        v.x = s.nextFloat();
        v.y = s.nextFloat();
        v.z = s.nextFloat();
        v.w = s.nextFloat();
        s.close();
    }

    private static String toStringRep(Vector4f v) {
        return String.format("%.3f %.3f %.3f %.3f", v.x, v.y, v.z, v.w);
    }

    private static void fromStringRep(Quaternionf q, String str) {
        final Scanner s = new Scanner(str);
        q.x = s.nextFloat();
        q.y = s.nextFloat();
        q.z = s.nextFloat();
        q.w = s.nextFloat();
        s.close();
    }

    private static String toStringRep(Quaternionf q) {
        return String.format("%.3f %.3f %.3f %.3f", q.x, q.y, q.z, q.w);
    }

}
