package com.play.app.basics;

import java.io.IOException;

import com.play.app.utils.WorldSerializer;

public interface Savable {
    public void save(WorldSerializer writer) throws IOException;
}
