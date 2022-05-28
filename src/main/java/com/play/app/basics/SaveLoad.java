package com.play.app.basics;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public interface SaveLoad {
    public void save(YAMLGenerator generator) throws IOException;

}
